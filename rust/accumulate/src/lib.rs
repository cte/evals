/// What should the type of _function be?
pub fn map<T, U, F>(input: Vec<T>, mut function: F) -> Vec<U>
where
    F: FnMut(T) -> U,
{
    input.into_iter().map(|x| function(x)).collect()
}
