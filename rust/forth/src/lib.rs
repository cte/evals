use std::collections::{HashMap, VecDeque}; // Import VecDeque

pub type Value = i32;
pub type Result = std::result::Result<(), Error>;

#[derive(Debug, PartialEq, Eq)]
pub enum Error {
    DivisionByZero,
    StackUnderflow,
    UnknownWord,
    InvalidWord,
}

#[derive(Default)]
pub struct Forth {
    stack: Vec<Value>,
    definitions: HashMap<String, Vec<String>>,
}

impl Forth {
    pub fn new() -> Forth {
        Forth::default()
    }

    pub fn stack(&self) -> &[Value] {
        &self.stack
    }

    // Main evaluation function - now handles both definitions and execution iteratively
    pub fn eval(&mut self, input: &str) -> Result {
        // Tokenize and lowercase immediately
        let mut tokens: VecDeque<String> = input
            .split_whitespace()
            .map(|s| s.to_lowercase()) // Lowercase all tokens upfront
            .collect();

        if tokens.is_empty() {
            return Ok(());
        }

        // Handle definition mode separately first
        if tokens[0] == ":" {
            if tokens.len() < 3 || tokens.back() != Some(&";".to_string()) {
                return Err(Error::InvalidWord);
            }
            let name = tokens[1].clone(); // Already lowercase
            if name.parse::<Value>().is_ok() {
                return Err(Error::InvalidWord);
            }

            // Extract definition body (lowercase already done)
            let definition_body: Vec<String> = tokens.range(2..tokens.len() - 1).cloned().collect();

            // Insert the new definition
            self.definitions.insert(name, definition_body);
            return Ok(()); // Definition handled, return
        }

        // --- Iterative Evaluation Loop ---
        while let Some(token) = tokens.pop_front() {
            if let Ok(value) = token.parse::<Value>() {
                self.push(value); // Push number onto the stack
            } else {
                // It's a word (already lowercase)
                if let Some(definition) = self.definitions.get(&token).cloned() {
                    // User-defined word: insert definition at the front
                    for def_token in definition.iter().rev() { // Insert in reverse order
                        tokens.push_front(def_token.clone());
                    }
                } else {
                    // Built-in word or unknown
                    match token.as_str() {
                        "+" => self.add()?,
                        "-" => self.sub()?,
                        "*" => self.mul()?,
                        "/" => self.div()?,
                        "dup" => self.dup()?,
                        "drop" => self.drop()?,
                        "swap" => self.swap()?,
                        "over" => self.over()?,
                        _ => return Err(Error::UnknownWord), // Word is not recognized
                    }
                }
            }
        }

        Ok(())
    }

    // --- Stack Manipulation Helpers --- (Keep these as they are)

    fn pop(&mut self) -> std::result::Result<Value, Error> {
        self.stack.pop().ok_or(Error::StackUnderflow)
    }

    fn push(&mut self, value: Value) {
        self.stack.push(value);
    }

    fn check_stack(&self, required: usize) -> Result {
        if self.stack.len() < required {
            Err(Error::StackUnderflow)
        } else {
            Ok(())
        }
    }

    // --- Built-in Word Implementations --- (Keep these as they are)

    fn add(&mut self) -> Result {
        self.check_stack(2)?;
        let b = self.pop().unwrap();
        let a = self.pop().unwrap();
        self.push(a + b);
        Ok(())
    }

    fn sub(&mut self) -> Result {
        self.check_stack(2)?;
        let b = self.pop().unwrap();
        let a = self.pop().unwrap();
        self.push(a - b);
        Ok(())
    }

    fn mul(&mut self) -> Result {
        self.check_stack(2)?;
        let b = self.pop().unwrap();
        let a = self.pop().unwrap();
        self.push(a * b);
        Ok(())
    }

    fn div(&mut self) -> Result {
        self.check_stack(2)?;
        let b = *self.stack.last().ok_or(Error::StackUnderflow)?; // Peek divisor safely
        if b == 0 {
            return Err(Error::DivisionByZero);
        }
        let b = self.pop().unwrap();
        let a = self.pop().unwrap();
        self.push(a / b);
        Ok(())
    }

    fn dup(&mut self) -> Result {
        self.check_stack(1)?;
        let a = *self.stack.last().unwrap();
        self.push(a);
        Ok(())
    }

    fn drop(&mut self) -> Result {
        self.check_stack(1)?;
        self.pop().unwrap();
        Ok(())
    }

    fn swap(&mut self) -> Result {
        self.check_stack(2)?;
        let b = self.pop().unwrap();
        let a = self.pop().unwrap();
        self.push(b);
        self.push(a);
        Ok(())
    }

    fn over(&mut self) -> Result {
        self.check_stack(2)?;
        let a = self.stack[self.stack.len() - 2];
        self.push(a);
        Ok(())
    }
}
