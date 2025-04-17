// the PhantomData instances in this file are just to stop compiler complaints
// about missing generics; feel free to remove them

/// A Matcher is a single rule of fizzbuzz: given a function on T, should
/// a word be substituted in? If yes, which word?
pub struct Matcher<T> {
    predicate: Box<dyn Fn(T) -> bool>,
    substitution: String,
}

/// A Fizzy is a set of matchers, which may be applied to an iterator.
///
/// Strictly speaking, it's usually more idiomatic to use `iter.map()` than to
/// consume an iterator with an `apply` method. Given a Fizzy instance, it's
/// pretty straightforward to construct a closure which applies it to all
/// elements of the iterator. However, we're using the `apply` pattern
/// here because it's a simpler interface for students to implement.
///
/// Also, it's a good excuse to try out using impl trait.
pub struct Fizzy<T> {
    matchers: Vec<Matcher<T>>,
}

impl<T> Matcher<T> {
    pub fn new<F, S>(matcher: F, subs: S) -> Matcher<T>
    where
        F: Fn(T) -> bool + 'static,
        S: Into<String>,
    {
        Matcher {
            predicate: Box::new(matcher),
            substitution: subs.into(),
        }
    }
}

impl<T> Fizzy<T> {
    pub fn new() -> Self {
        Fizzy {
            matchers: Vec::new(),
        }
    }

    #[must_use]
    pub fn add_matcher(mut self, matcher: Matcher<T>) -> Self {
        self.matchers.push(matcher);
        self
    }

    /// map this fizzy onto every element of an iterator, returning a new iterator
    pub fn apply<I>(self, iter: I) -> impl Iterator<Item = String>
    where
        I: IntoIterator<Item = T>,
        T: ToString + Copy,
    {
        iter.into_iter().map(move |item| {
            let mut result = String::new();
            for matcher in &self.matchers {
                if (matcher.predicate)(item) {
                    result.push_str(&matcher.substitution);
                }
            }
            if result.is_empty() {
                item.to_string()
            } else {
                result
            }
        })
    }
}

/// convenience function: return a Fizzy which applies the standard fizz-buzz rules
pub fn fizz_buzz<T>() -> Fizzy<T>
where
    T: Copy + PartialEq + Rem<Output = T> + From<u8> + ToString,
{
    Fizzy::new()
        .add_matcher(Matcher::new(
            |n: T| n % T::from(3u8) == T::from(0u8),
            "fizz",
        ))
        .add_matcher(Matcher::new(
            |n: T| n % T::from(5u8) == T::from(0u8),
            "buzz",
        ))
}

use std::ops::Rem;
