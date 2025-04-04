#[derive(Debug, PartialEq, Eq, Clone, Copy)]
pub enum Error {
    NotEnoughPinsLeft,
    GameComplete,
}

#[derive(Default)]
pub struct BowlingGame {
    rolls: Vec<u16>,
}

impl BowlingGame {
    pub fn new() -> Self {
        Self::default()
    }

    // Helper to get roll value safely
    fn get_roll(&self, index: usize) -> Option<u16> {
        self.rolls.get(index).copied()
    }

    // Helper to check game state and validate the next roll
    fn check_roll_validity(&self, pins: u16) -> Result<(), Error> {
        if pins > 10 {
            return Err(Error::NotEnoughPinsLeft);
        }

        let mut frame_number = 1;
        let mut roll_idx = 0;
        let mut frame_first_roll: Option<u16> = None;

        while frame_number <= 10 && roll_idx < self.rolls.len() {
            let current_roll = self.rolls[roll_idx];

            if frame_number == 10 {
                let roll1 = current_roll;
                let roll2_opt = self.get_roll(roll_idx + 1);
                let roll3_opt = self.get_roll(roll_idx + 2);

                let is_strike = roll1 == 10;
                let is_spare = !is_strike && roll2_opt.map_or(false, |r2| roll1 + r2 == 10);

                let rolls_in_frame = self.rolls.len() - roll_idx;

                if is_strike {
                    if rolls_in_frame == 1 { // Adding second roll after strike
                        // No pin limit relative to first roll (strike)
                    } else if rolls_in_frame == 2 { // Adding third roll after strike
                        let roll2 = roll2_opt.unwrap(); // Must exist if rolls_in_frame == 2
                        if roll2 < 10 && roll2 + pins > 10 {
                            return Err(Error::NotEnoughPinsLeft); // e.g. X, 5, 6
                        }
                    } else if rolls_in_frame >= 3 {
                         return Err(Error::GameComplete);
                    }
                } else if is_spare {
                     if rolls_in_frame == 2 { // Adding third roll after spare
                         // No pin limit relative to spare rolls
                     } else if rolls_in_frame >= 3 {
                         return Err(Error::GameComplete);
                     }
                } else { // Open frame
                    if rolls_in_frame == 1 { // Adding second roll
                        if roll1 + pins > 10 {
                            return Err(Error::NotEnoughPinsLeft);
                        }
                    } else if rolls_in_frame >= 2 {
                        return Err(Error::GameComplete);
                    }
                }
                // If we reach here in 10th frame logic, the current state is valid for adding 'pins'
                // We don't need to continue the loop
                return Ok(());

            } else { // Frames 1-9
                if frame_first_roll.is_none() { // This is the first roll of the frame
                    if current_roll == 10 { // Strike
                        frame_number += 1;
                        roll_idx += 1;
                        frame_first_roll = None; // Reset for next frame
                    } else {
                        frame_first_roll = Some(current_roll);
                        roll_idx += 1;
                    }
                } else { // This is the second roll of the frame
                    let first_roll = frame_first_roll.unwrap();
                    if first_roll + current_roll > 10 {
                        // Should not happen with valid history, but check defensively
                        // This indicates an error in *past* rolls, not the current 'pins' attempt.
                        // Let's assume history is valid based on tests passing for this.
                    }
                    frame_number += 1;
                    roll_idx += 1;
                    frame_first_roll = None; // Reset for next frame
                }
            }
        } // End while loop

        // If loop finished, we are adding a roll to an incomplete game.
        // Check validity based on the state where the loop left off.
        if frame_number > 10 {
             // Should have been caught by 10th frame logic if roll_idx < len
             return Err(Error::GameComplete);
        }

        if let Some(first_roll) = frame_first_roll {
            // We are adding the second roll to the current frame (1-9)
            if first_roll + pins > 10 {
                return Err(Error::NotEnoughPinsLeft);
            }
        } else {
            // We are adding the first roll of a frame (1-10) or a bonus roll.
            // pins > 10 already checked.
            // 10th frame bonus roll validity was checked inside the loop if applicable.
        }

        Ok(())
    }


    pub fn roll(&mut self, pins: u16) -> Result<(), Error> {
        self.check_roll_validity(pins)?;
        // If validity check passes, add the roll
        self.rolls.push(pins);
        Ok(())
    }

    pub fn score(&self) -> Option<u16> {
        let mut total_score = 0;
        let mut roll_idx = 0;
        let mut frame_number = 0;
        let mut game_complete = false;

        while frame_number < 10 {
            let roll1_opt = self.get_roll(roll_idx);
            if roll1_opt.is_none() { return None; } // Game definitely incomplete
            let roll1 = roll1_opt.unwrap();
            if roll1 > 10 { return None; } // Invalid data

            if frame_number == 9 { // --- 10th Frame Scoring ---
                let roll2_opt = self.get_roll(roll_idx + 1);
                let roll3_opt = self.get_roll(roll_idx + 2);

                if roll1 == 10 { // Strike
                    match (roll2_opt, roll3_opt) {
                        (Some(r2), Some(r3)) => {
                            if r2 > 10 || r3 > 10 { return None; }
                            if r2 < 10 && r2 + r3 > 10 { /* Allowed */ }
                            else if r2 == 10 && r3 > 10 { return None; }
                            total_score += 10 + r2 + r3;
                            roll_idx += 3;
                            game_complete = true; // Consumed 3 rolls for final frame
                        }
                        _ => return None, // Incomplete 10th frame after strike
                    }
                } else { // Not a strike
                    match roll2_opt {
                        Some(r2) => {
                            if r2 > 10 { return None; }
                            if roll1 + r2 > 10 { return None; }

                            if roll1 + r2 == 10 { // Spare
                                match roll3_opt {
                                    Some(r3) => {
                                        if r3 > 10 { return None; }
                                        total_score += 10 + r3;
                                        roll_idx += 3;
                                        game_complete = true; // Consumed 3 rolls for final frame
                                    }
                                    None => return None, // Incomplete 10th frame after spare
                                }
                            } else { // Open Frame
                                total_score += roll1 + r2;
                                roll_idx += 2;
                                game_complete = true; // Consumed 2 rolls for final frame
                                // Check for erroneous 3rd roll
                                if self.get_roll(roll_idx).is_some() { return None; }
                            }
                        }
                        None => return None, // Incomplete 10th frame (need 2nd roll)
                    }
                }
            } else { // --- Frames 1-9 Scoring ---
                if roll1 == 10 { // Strike
                    let bonus1_opt = self.get_roll(roll_idx + 1);
                    let bonus2_opt = self.get_roll(roll_idx + 2);
                    match (bonus1_opt, bonus2_opt) {
                        (Some(b1), Some(b2)) => {
                            if b1 > 10 || b2 > 10 { return None; }
                            // Assume valid history from roll()
                            total_score += 10 + b1 + b2;
                            roll_idx += 1;
                        }
                        _ => return None, // Incomplete game (need strike bonus rolls)
                    }
                } else { // Not a strike
                    let roll2_opt = self.get_roll(roll_idx + 1);
                    match roll2_opt {
                        Some(r2) => {
                            if r2 > 10 { return None; }
                            if roll1 + r2 > 10 { return None; }

                            if roll1 + r2 == 10 { // Spare
                                let bonus_opt = self.get_roll(roll_idx + 2);
                                match bonus_opt {
                                    Some(bonus) => {
                                        if bonus > 10 { return None; }
                                        total_score += 10 + bonus;
                                        roll_idx += 2;
                                    }
                                    None => return None, // Incomplete game (need spare bonus roll)
                                }
                            } else { // Open Frame
                                total_score += roll1 + r2;
                                roll_idx += 2;
                            }
                        }
                        None => return None, // Incomplete game (need 2nd roll)
                    }
                }
            }
            frame_number += 1;
        } // End of while loop

        // Final check: Was the game marked complete AND did we consume all rolls?
        if game_complete && roll_idx == self.rolls.len() {
            Some(total_score)
        } else {
            None // Game was incomplete or had extra rolls
        }
    }
}
