use std::ops::Rem;
use std::fmt::Display;

/// A Matcher is a single rule: if a value matches the predicate, substitute a word.
// T must be Copy because we store Fn(T) and call it with a copied value.
// T must be 'static because it's used in Box<dyn Fn(T)>.
pub struct Matcher<T: Copy + 'static> {
    matcher_fn: Box<dyn Fn(T) -> bool>,
    subs: String,
}

impl<T: Copy + 'static> Matcher<T> {
    /// Creates a new Matcher.
    ///
    /// # Arguments
    ///
    /// * `matcher` - A closure that takes a value of type T (by value) and returns true if the substitution should occur.
    /// * `subs` - The string substitution to use if the matcher returns true.
    pub fn new<F, S>(matcher: F, subs: S) -> Matcher<T>
    where
        F: Fn(T) -> bool + 'static, // Expect Fn(T) now
        S: Into<String>,
    {
        Matcher {
            matcher_fn: Box::new(matcher),
            subs: subs.into(),
        }
    }
}

/// A Fizzy is a set of matchers, which can be applied to an iterator.
// T must be Copy + Display + 'static to meet Matcher requirements and for Display in apply.
pub struct Fizzy<T: Copy + Display + 'static> {
    matchers: Vec<Matcher<T>>,
}

impl<T: Copy + Display + 'static> Fizzy<T> {
    /// Creates a new, empty Fizzy instance.
    pub fn new() -> Self {
        Fizzy {
            matchers: Vec::new(),
        }
    }

    /// Adds a matcher to this Fizzy instance.
    #[must_use]
    pub fn add_matcher(mut self, matcher: Matcher<T>) -> Self {
        self.matchers.push(matcher);
        self
    }

    /// Applies the Fizzy rules to each element of an iterator.
    pub fn apply<I>(self, iter: I) -> impl Iterator<Item = String>
    where
        I: Iterator<Item = T>, // T is already Copy from the struct bound
    {
        iter.map(move |item| { // item is T (Copy)
            let mut output = String::new();
            for matcher in &self.matchers {
                // Call the stored Box<dyn Fn(T) -> bool> with the copied item
                if (matcher.matcher_fn)(item) {
                    output.push_str(&matcher.subs);
                }
            }
            if output.is_empty() {
                item.to_string() // Use Display trait via to_string()
            } else {
                output
            }
        })
    }
}

/// Creates a Fizzy instance configured with the standard FizzBuzz rules.
pub fn fizz_buzz<T>() -> Fizzy<T>
where
    // T needs all these bounds for the matchers and the default case
    T: Copy + Default + PartialEq + From<u8> + Rem<Output = T> + Display + 'static,
{
    Fizzy::new()
        // Update closures to take T by value, matching Matcher::new expectation
        .add_matcher(Matcher::new(|n: T| n % T::from(3u8) == T::default(), "fizz"))
        .add_matcher(Matcher::new(|n: T| n % T::from(5u8) == T::default(), "buzz"))
}

// Default implementation for Fizzy<T>
impl<T: Copy + Display + 'static> Default for Fizzy<T> {
    fn default() -> Self {
        Self::new()
    }
}
