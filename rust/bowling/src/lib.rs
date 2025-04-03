#[derive(Debug, PartialEq, Eq)]
pub enum Error {
    NotEnoughPinsLeft,
    GameComplete,
}

pub struct BowlingGame {
    rolls: Vec<u16>,
    frame: u8,
    roll_in_frame: u8,
    bonus_rolls: u8,
    pins_in_frame: u16,
    complete: bool,
}

impl BowlingGame {
    pub fn new() -> Self {
        BowlingGame {
            rolls: Vec::new(),
            frame: 1,
            roll_in_frame: 1,
            bonus_rolls: 0,
            pins_in_frame: 10,
            complete: false,
        }
    }

    pub fn roll(&mut self, pins: u16) -> Result<(), Error> {
        if self.complete {
            return Err(Error::GameComplete);
        }

        if pins > self.pins_in_frame {
            return Err(Error::NotEnoughPinsLeft);
        }

        let len = self.rolls.len();

        if self.frame < 10 {
            // Frames 1-9
            self.rolls.push(pins);
            if self.roll_in_frame == 1 {
                if pins == 10 {
                    self.frame += 1;
                    self.roll_in_frame = 1;
                    self.pins_in_frame = 10;
                } else {
                    self.roll_in_frame = 2;
                    self.pins_in_frame -= pins;
                }
            } else {
                self.frame += 1;
                self.roll_in_frame = 1;
                self.pins_in_frame = 10;
            }
        } else if self.frame == 10 {
            self.rolls.push(pins);
            if self.bonus_rolls == 0 {
                if self.roll_in_frame == 1 {
                    if pins == 10 {
                        self.bonus_rolls = 2;
                        self.roll_in_frame = 2;
                        self.pins_in_frame = 10;
                    } else {
                        self.roll_in_frame = 2;
                        self.pins_in_frame -= pins;
                    }
                } else if self.roll_in_frame == 2 {
                    let first = self.rolls[len - 1];
                    if first == 10 {
                        if pins == 10 {
                            self.bonus_rolls = 1;
                            self.pins_in_frame = 10;
                        } else {
                            self.bonus_rolls = 1;
                            self.pins_in_frame = 10 - pins;
                        }
                    } else if first + pins == 10 {
                        self.bonus_rolls = 1;
                        self.pins_in_frame = 10;
                    } else {
                        self.complete = true;
                    }
                    self.roll_in_frame = 3;
                }
            } else {
                // Bonus rolls
                let first_bonus = self.rolls.get(20).copied().unwrap_or(0);
                if self.bonus_rolls == 2 {
                    if first_bonus != 10 && first_bonus + pins > 10 {
                        return Err(Error::NotEnoughPinsLeft);
                    }
                }
                self.bonus_rolls -= 1;
                if self.bonus_rolls == 0 {
                    self.complete = true;
                } else {
                    if pins == 10 {
                        self.pins_in_frame = 10;
                    } else {
                        self.pins_in_frame = 10 - pins;
                    }
                }
            }
        }

        Ok(())
    }

    pub fn score(&self) -> Option<u16> {
        let mut score = 0;
        let mut roll_index = 0;
        for _ in 0..10 {
            if roll_index >= self.rolls.len() {
                return None;
            }
            let first = self.rolls[roll_index];
            let second = *self.rolls.get(roll_index + 1).unwrap_or(&0);

            if first == 10 {
                let bonus1 = *self.rolls.get(roll_index + 1)?;
                let bonus2 = *self.rolls.get(roll_index + 2)?;
                score += 10 + bonus1 + bonus2;
                roll_index += 1;
            } else if first + second == 10 {
                let bonus = *self.rolls.get(roll_index + 2)?;
                score += 10 + bonus;
                roll_index += 2;
            } else {
                if first + second > 10 {
                    return None;
                }
                score += first + second;
                roll_index += 2;
            }
        }
        Some(score)
    }
}
