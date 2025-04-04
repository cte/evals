#[macro_export]
macro_rules! hashmap {
    // Base case: empty hashmap
    () => {
        ::std::collections::HashMap::new()
    };
    // Case: one or more key-value pairs, handles optional trailing comma
    ( $( $key:expr => $value:expr ),+ $(,)? ) => {
        {
            let mut hm = ::std::collections::HashMap::new();
            $(
                hm.insert($key, $value);
            )*
            hm
        }
    };
}
