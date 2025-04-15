pub fn map<T, U, F>(input: Vec<T>, mut f: F) -> Vec<U>
where
    F: FnMut(T) -> U,
{
    let mut output = Vec::with_capacity(input.len());
    for element in input {
        output.push(f(element));
    }
    output
}
