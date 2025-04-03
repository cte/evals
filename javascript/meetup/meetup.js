//
// This is only a SKELETON file for the 'Meetup' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const meetup = (year, month, weekDescriptor, weekdayName) => {
  const weekdayMap = {
    Sunday: 0,
    Monday: 1,
    Tuesday: 2,
    Wednesday: 3,
    Thursday: 4,
    Friday: 5,
    Saturday: 6,
  };

  const targetWeekday = weekdayMap[weekdayName];
  const monthIndex = month - 1;

  let day;

  if (weekDescriptor === 'teenth') {
    for (let d = 13; d <= 19; d++) {
      const date = new Date(year, monthIndex, d);
      if (date.getDay() === targetWeekday) {
        day = d;
        break;
      }
    }
  } else if (['first', 'second', 'third', 'fourth'].includes(weekDescriptor)) {
    let count = 0;
    for (let d = 1; d <= 31; d++) {
      const date = new Date(year, monthIndex, d);
      if (date.getMonth() !== monthIndex) break; // next month reached
      if (date.getDay() === targetWeekday) {
        count++;
        if (
          (weekDescriptor === 'first' && count === 1) ||
          (weekDescriptor === 'second' && count === 2) ||
          (weekDescriptor === 'third' && count === 3) ||
          (weekDescriptor === 'fourth' && count === 4)
        ) {
          day = d;
          break;
        }
      }
    }
  } else if (weekDescriptor === 'last') {
    const lastDayOfMonth = new Date(year, month, 0).getDate();
    for (let d = lastDayOfMonth; d >= 1; d--) {
      const date = new Date(year, monthIndex, d);
      if (date.getDay() === targetWeekday) {
        day = d;
        break;
      }
    }
  } else {
    throw new Error('Invalid week descriptor');
  }

  return new Date(year, monthIndex, day);
};
