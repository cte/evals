use std::collections::HashMap;

#[derive(Clone, Debug, PartialEq)]
pub struct Edge {
    u: String,
    v: String,
    attrs: HashMap<String, String>,
}

impl Edge {
    pub fn new(u: &str, v: &str) -> Self {
        Edge {
            u: u.to_string(),
            v: v.to_string(),
            attrs: HashMap::new(),
        }
    }

    pub fn with_attrs(mut self, attrs: &[(&str, &str)]) -> Self {
        self.attrs = attrs.iter().map(|(k, v)| (k.to_string(), v.to_string())).collect();
        self
    }

    pub fn attr(&self, key: &str) -> Option<&str> {
        self.attrs.get(key).map(|s| s.as_str())
    }
}