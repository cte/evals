use std::marker::PhantomData;
use std::ptr::NonNull;

// this module adds some functionality based on the required implementations
// here like: `LinkedList::pop_back` or `Clone for LinkedList<T>`
// You are free to use anything in it, but it's mainly for the test framework.
mod pre_implemented;

// --- Node Definition ---
// Represents a node in the doubly linked list.
struct Node<T> {
    val: T,
    // Using NonNull as raw pointers must not be null when pointing to a valid node.
    // Option is used because head's prev and tail's next are null.
    next: Option<NonNull<Node<T>>>,
    prev: Option<NonNull<Node<T>>>,
}

// --- LinkedList Definition ---
// The main doubly linked list structure.
pub struct LinkedList<T> {
    head: Option<NonNull<Node<T>>>,
    tail: Option<NonNull<Node<T>>>,
    len: usize,
    // Marker for variance and drop check.
    // PhantomData<Box<Node<T>>> indicates that LinkedList logically owns Nodes allocated via Box.
    // This helps with drop check and variance.
    _marker: PhantomData<Box<Node<T>>>,
}

// --- Cursor Definition ---
// A cursor for navigating and modifying the list.
// It holds a mutable reference to the list and a pointer to the current node.
pub struct Cursor<'a, T> {
    list: &'a mut LinkedList<T>,
    current: Option<NonNull<Node<T>>>,
}

// --- Iter Definition ---
// An iterator for traversing the list immutably.
pub struct Iter<'a, T> {
    head: Option<NonNull<Node<T>>>,
    tail: Option<NonNull<Node<T>>>,
    len: usize,
    // Marker for indicating the iterator borrows elements with lifetime 'a.
    _marker: PhantomData<&'a Node<T>>,
}


// --- LinkedList Implementation (Step 1 & Core Logic) ---
impl<T> LinkedList<T> {
    /// Creates a new, empty LinkedList.
    pub fn new() -> Self {
        LinkedList {
            head: None,
            tail: None,
            len: 0,
            _marker: PhantomData,
        }
    }

    /// Checks if the list is empty.
    /// Complexity: O(1)
    pub fn is_empty(&self) -> bool {
        self.len == 0
    }

    /// Returns the number of elements in the list.
    /// Complexity: O(1)
    pub fn len(&self) -> usize {
        self.len
    }

    /// Returns a cursor positioned at the front element.
    /// The cursor allows mutation of the list.
    pub fn cursor_front(&mut self) -> Cursor<'_, T> {
         Cursor {
            current: self.head,
            list: self, // Borrows the list mutably for the cursor's lifetime
        }
    }

    /// Returns a cursor positioned at the back element.
    /// The cursor allows mutation of the list.
    pub fn cursor_back(&mut self) -> Cursor<'_, T> {
         Cursor {
            current: self.tail,
            list: self, // Borrows the list mutably
        }
    }

    /// Returns an iterator that moves from front to back.
    /// The iterator provides immutable references to the elements.
    pub fn iter(&self) -> Iter<'_, T> {
        Iter {
            head: self.head,
            tail: self.tail,
            len: self.len,
            _marker: PhantomData,
        }
    }

    // Note: push_front, pop_front, push_back, pop_back are provided by
    // the `pre_implemented` module, which uses the cursor methods below.
}

// --- Drop Implementation (Step 4) ---
impl<T> Drop for LinkedList<T> {
    fn drop(&mut self) {
        // Use a helper to pop nodes directly to avoid relying on potentially
        // complex cursor logic during drop, ensuring all nodes are freed.
        while self.pop_front_node().is_some() {
            // Loop continues until the list is empty.
            // The Box returned by pop_front_node is dropped here, freeing the node.
        }
    }
}

// Private helper for Drop implementation.
impl<T> LinkedList<T> {
    /// Pops the front node Box without calling T's destructor immediately.
    /// Used internally by Drop to ensure node memory is freed.
    fn pop_front_node(&mut self) -> Option<Box<Node<T>>> {
        // Take the head pointer
        self.head.map(|old_head_ptr| {
            // SAFETY: `old_head_ptr` is the valid head of a non-empty list (due to .map).
            // We are converting it back to a Box to take ownership and manage its memory.
            // We have `&mut self`, ensuring exclusive access to the list structure.
            let old_head_box = unsafe { Box::from_raw(old_head_ptr.as_ptr()) };

            // Update list's head to the next node
            self.head = old_head_box.next;
            match self.head {
                None => {
                    // The list became empty
                    self.tail = None;
                }
                Some(new_head_ptr) => {
                    // The list is not empty, update the new head's prev pointer.
                    // SAFETY: `new_head_ptr` is the new valid head. `&mut self` ensures exclusivity.
                    // We are modifying the `prev` pointer of the node it points to.
                    unsafe {
                        (*new_head_ptr.as_ptr()).prev = None;
                    }
                }
            }

            self.len -= 1;
            old_head_box // Return the Box owning the former head node
        })
    }
}


// --- Iter Implementation (Step 2) ---
impl<'a, T> Iterator for Iter<'a, T> {
    type Item = &'a T;

    /// Advances the iterator and returns the next value.
    fn next(&mut self) -> Option<Self::Item> {
        if self.len == 0 {
            None // Iterator is exhausted
        } else {
            // Take the current head pointer
            self.head.map(|head_ptr| {
                self.len -= 1;
                // SAFETY: `head_ptr` points to a valid node within the list.
                // The list is borrowed immutably (`&self` in `iter()`), so the node
                // won't be deallocated or mutated during iteration via this iterator.
                // The lifetime `'a` ensures the returned reference doesn't outlive the list borrow.
                // We dereference the raw pointer to get an immutable reference to the node.
                unsafe {
                    let node = &*head_ptr.as_ptr();
                    self.head = node.next; // Move iterator's head pointer forward
                    &node.val // Return immutable reference to the value
                }
            })
        }
    }
}

// Implement DoubleEndedIterator for bidirectional iteration.
impl<'a, T> DoubleEndedIterator for Iter<'a, T> {
    /// Moves the iterator from the back and returns the previous value.
    fn next_back(&mut self) -> Option<Self::Item> {
        if self.len == 0 {
            None // Iterator is exhausted
        } else {
            // Take the current tail pointer
            self.tail.map(|tail_ptr| {
                self.len -= 1;
                // SAFETY: `tail_ptr` points to a valid node within the list.
                // Immutable borrow (`&self` in `iter()`) ensures safety. Lifetime 'a is valid.
                // We dereference the raw pointer to get an immutable reference to the node.
                unsafe {
                    let node = &*tail_ptr.as_ptr();
                    self.tail = node.prev; // Move iterator's tail pointer backward
                    &node.val // Return immutable reference to the value
                }
            })
        }
    }
}

// --- Cursor Implementation (Step 3) ---
impl<'a, T> Cursor<'a, T> {
    // --- Private Helpers for Node Access and Linking ---

    /// Gets a mutable reference to the node pointed to by `ptr`.
    /// SAFETY: Caller must ensure `ptr` is valid, non-null, and points to a node
    /// owned by `self.list`. The cursor's mutable borrow `&'a mut list` ensures
    /// exclusive access to the list structure for lifetime 'a.
    #[inline]
    unsafe fn node_mut(ptr: NonNull<Node<T>>) -> &'a mut Node<T> {
        // The lifetime 'a is safe because the cursor holds &'a mut LinkedList<T>
        &mut *ptr.as_ptr()
    }

    /// Gets an immutable reference to the node pointed to by `ptr`.
    /// SAFETY: Caller must ensure `ptr` is valid and non-null.
    /// The lifetime 'a is safe due to the cursor's borrow `&'a mut LinkedList<T>`.
    #[inline]
    unsafe fn node(ptr: NonNull<Node<T>>) -> &'a Node<T> {
        // The lifetime 'a is safe because the cursor holds &'a mut LinkedList<T>
        &*ptr.as_ptr()
    }

    /// Unlinks the node pointed to by `node_ptr` from the list.
    /// Returns the `Box<Node<T>>` owning the unlinked node.
    /// SAFETY: `node_ptr` must be a valid pointer to a node currently in the list.
    /// `&mut self.list` guarantees exclusive access during this operation.
    unsafe fn unlink_node(&mut self, node_ptr: NonNull<Node<T>>) -> Box<Node<T>> {
        // Convert the raw pointer back into a Box to manage its memory and access fields.
        // SAFETY: `node_ptr` is assumed valid and points to a Box-allocated node.
        let node_box = Box::from_raw(node_ptr.as_ptr());

        let prev_ptr = node_box.prev;
        let next_ptr = node_box.next;

        // Update the `next` pointer of the previous node (if it exists).
        if let Some(prev) = prev_ptr {
            // SAFETY: `prev` points to a valid node in the list. We get a mutable reference
            // via `node_mut` (safe due to `&mut self.list`) and update its `next`.
            Self::node_mut(prev).next = next_ptr;
        } else {
            // The unlinked node was the head, update the list's head pointer.
            self.list.head = next_ptr;
        }

        // Update the `prev` pointer of the next node (if it exists).
        if let Some(next) = next_ptr {
            // SAFETY: `next` points to a valid node. We get a mutable reference
            // via `node_mut` and update its `prev`.
            Self::node_mut(next).prev = prev_ptr;
        } else {
            // The unlinked node was the tail, update the list's tail pointer.
            self.list.tail = prev_ptr;
        }

        self.list.len -= 1;
        node_box // Return the Box owning the unlinked node's data and memory.
    }

    /// Links a new node (given as `node_box`) into the list between `prev` and `next`.
    /// Updates list head/tail if necessary and increments length.
    /// Returns a `NonNull` pointer to the newly inserted node.
    /// SAFETY: `prev` and `next` must correctly represent the insertion point
    /// (potentially `None` for list ends). If `Some`, they must point to valid nodes
    /// within `self.list`. `&mut self.list` guarantees exclusive access.
    unsafe fn link_node(
        &mut self,
        mut node_box: Box<Node<T>>,
        prev: Option<NonNull<Node<T>>>,
        next: Option<NonNull<Node<T>>>,
    ) -> NonNull<Node<T>> {
        // Set the prev/next pointers on the new node itself.
        node_box.prev = prev;
        node_box.next = next;

        // Convert the Box into a raw NonNull pointer.
        // SAFETY: `Box::into_raw` never returns a null pointer.
        let node_ptr = NonNull::new_unchecked(Box::into_raw(node_box));

        // Update the `next` pointer of the previous node (if it exists).
        if let Some(p) = prev {
            // SAFETY: `p` points to a valid node. `node_mut` provides safe mutable access.
            Self::node_mut(p).next = Some(node_ptr);
        } else {
            // The new node is the head of the list.
            self.list.head = Some(node_ptr);
        }

        // Update the `prev` pointer of the next node (if it exists).
        if let Some(n) = next {
            // SAFETY: `n` points to a valid node. `node_mut` provides safe mutable access.
            Self::node_mut(n).prev = Some(node_ptr);
        } else {
            // The new node is the tail of the list.
            self.list.tail = Some(node_ptr);
        }

        self.list.len += 1;
        node_ptr // Return the pointer to the newly linked node.
    }

    // --- Public Cursor Methods ---

    /// Returns a mutable reference to the element at the current cursor position.
    pub fn peek_mut(&mut self) -> Option<&mut T> {
        // SAFETY: `self.current` (if Some) must point to a valid node owned by `self.list`.
        // `node_mut` upholds safety by requiring this and leveraging `&mut self.list`.
        // The lifetime of the returned reference is tied to `&mut self` (thus 'a).
        self.current.map(|ptr| unsafe { &mut Self::node_mut(ptr).val })
    }

    /// Moves the cursor one position forward (towards the back) and
    /// returns a mutable reference to the element at the new position.
    #[allow(clippy::should_implement_trait)] // Not implementing std::iter::Iterator
    pub fn next(&mut self) -> Option<&mut T> {
        // Get the `next` pointer from the current node, if the cursor is valid.
        self.current = match self.current {
            None => None, // Cursor is invalid, cannot move.
            Some(ptr) => {
                // SAFETY: `ptr` points to a valid node. `node` safely reads its `next` pointer.
                unsafe { Self::node(ptr).next }
            }
        };
        // `peek_mut` handles safety checks for accessing the value at the new `self.current`.
        self.peek_mut()
    }

    /// Moves the cursor one position backward (towards the front) and
    /// returns a mutable reference to the element at the new position.
    pub fn prev(&mut self) -> Option<&mut T> {
         // Get the `prev` pointer from the current node, if the cursor is valid.
         self.current = match self.current {
            None => None, // Cursor is invalid, cannot move.
            Some(ptr) => {
                // SAFETY: `ptr` points to a valid node. `node` safely reads its `prev` pointer.
                unsafe { Self::node(ptr).prev }
            }
        };
        // `peek_mut` handles safety checks for accessing the value at the new `self.current`.
        self.peek_mut()
    }

    /// Removes and returns the element at the current cursor position.
    /// Moves the cursor to the next element. If there is no next element,
    /// it moves to the previous element.
    pub fn take(&mut self) -> Option<T> {
        // Take the current pointer, leaving `self.current` as None temporarily.
        // This prevents use-after-free if `unlink_node` is called again before `self.current` is updated.
        self.current.take().map(|node_ptr| {
            // SAFETY: `node_ptr` was the valid `current` node. `unlink_node` handles the unlinking safely.
            let node_box = unsafe { self.unlink_node(node_ptr) };

            // Move cursor to the next element. If the removed node was the tail,
            // move to the new tail (original previous element).
            self.current = node_box.next.or(self.list.tail); // Use list.tail if next is None

            node_box.val // Return the value from the unlinked node's Box.
        })
    }


    /// Inserts a new element *after* the current cursor position.
    /// If the cursor is invalid (e.g., after `take` on the tail, or on an empty list),
    /// the element is inserted at the front of the list.
    pub fn insert_after(&mut self, element: T) {
        let (prev, next) = match self.current {
            None => {
                // Cursor is not pointing at a node. Insert at the front.
                // `prev` is None, `next` is the current list head.
                (None, self.list.head)
            }
            Some(current_ptr) => {
                // Insert after the node the cursor is pointing to.
                // `prev` is the current node, `next` is the current node's next.
                // SAFETY: `current_ptr` is valid. `node` safely reads its `next` pointer.
                let current_next = unsafe { Self::node(current_ptr).next };
                (Some(current_ptr), current_next)
            }
        };

        // Create a Box for the new node.
        let node_box = Box::new(Node { val: element, prev: None, next: None }); // prev/next set by link_node
        // SAFETY: `link_node` requires `prev` and `next` to be correct pointers/None
        // representing the insertion point. We derived them correctly above.
        unsafe {
            self.link_node(node_box, prev, next);
            // The cursor's `current` pointer remains unchanged, still pointing at the
            // original element (if it existed).
        }
    }

    /// Inserts a new element *before* the current cursor position.
    /// If the cursor is invalid (e.g., after `take` on the head, or on an empty list),
    /// the element is inserted at the back of the list.
    pub fn insert_before(&mut self, element: T) {
         let (prev, next) = match self.current {
            None => {
                 // Cursor is not pointing at a node. Insert at the back.
                 // `prev` is the current list tail, `next` is None.
                 (self.list.tail, None)
            }
            Some(current_ptr) => {
                // Insert before the node the cursor is pointing to.
                // `prev` is the current node's prev, `next` is the current node.
                // SAFETY: `current_ptr` is valid. `node` safely reads its `prev` pointer.
                let current_prev = unsafe { Self::node(current_ptr).prev };
                (current_prev, Some(current_ptr))
            }
        };

        // Create a Box for the new node.
        let node_box = Box::new(Node { val: element, prev: None, next: None }); // prev/next set by link_node
        // SAFETY: `link_node` requires `prev` and `next` to be correct pointers/None
        // representing the insertion point. We derived them correctly above.
        unsafe {
            // Link the new node. The returned pointer points to the newly inserted node.
            let new_node_ptr = self.link_node(node_box, prev, next);
            // If the cursor was invalid (inserting at the back), update the cursor
            // to point to the newly inserted node. Otherwise, the cursor remains
            // pointing at the original element it was before.
            if self.current.is_none() {
                self.current = Some(new_node_ptr);
            }
        }
    }
}
