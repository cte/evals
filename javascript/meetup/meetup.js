//
// This is only a SKELETON file for the 'Meetup' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

const DAYS_OF_WEEK = {
  Sunday: 0,
  Monday: 1,
  Tuesday: 2,
  Wednesday: 3,
  Thursday: 4,
  Friday: 5,
  Saturday: 6,
};

const WEEK_DESCRIPTOR_OFFSET = {
  first: 1,
  second: 8,
  third: 15,
  fourth: 22,
  teenth: 13,
  last: -6, // Start check from 6 days before the end of the month
};

export const meetup = (year, month, weekDescriptor, dayOfWeek) => {
  const targetDay = DAYS_OF_WEEK[dayOfWeek];
  const jsMonth = month - 1; // Date uses 0-indexed months

  const startDay = WEEK_DESCRIPTOR_OFFSET[weekDescriptor];

  if (weekDescriptor === 'last') {
    // Find the last day of the month
    const lastDayOfMonth = new Date(year, jsMonth + 1, 0).getDate();
    // Iterate backwards from the last day
    for (let day = lastDayOfMonth; day > lastDayOfMonth - 7; day--) {
      const date = new Date(year, jsMonth, day);
      if (date.getDay() === targetDay) {
        return date;
      }
    }
  } else {
    // Iterate forwards from the start day defined by the descriptor
    const limit = weekDescriptor === 'teenth' ? startDay + 6 : startDay + 6; // 'teenth' checks 13-19, others check a 7-day window
    for (let day = startDay; day <= limit; day++) {
       // Ensure we don't go past the actual end of the month for descriptors like 'fourth' or 'last' logic handled above
       // For 'teenth', the limit is 19, which is always valid.
       // For 'first' to 'fourth', the limit could exceed month days, but we only care about the first match within the 7-day window starting from startDay.
       const date = new Date(year, jsMonth, day);
       // Check if the date is still in the correct month (important for startDay + offset calculations)
       if (date.getMonth() !== jsMonth) continue;

       if (date.getDay() === targetDay) {
         // For 'first' through 'fourth', we need the *first* match in the respective 7-day window.
         // For 'teenth', we also need the first match in the 13-19 window.
         return date;
       }
    }
  }

  // Should theoretically not be reached if inputs are valid per exercise constraints
  throw new Error('Could not find a matching date.');
};
