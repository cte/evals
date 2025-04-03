use time::PrimitiveDateTime as DateTime;

// Returns a DateTime one billion seconds after start.
pub fn after(start: DateTime) -> DateTime {
    use time::Duration;
    start + Duration::seconds(1_000_000_000)
}
