//
// This is only a SKELETON file for the 'Space Age' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

// Define the duration of an Earth year in seconds
const EARTH_YEAR_IN_SECONDS = 31557600;

// Define the orbital periods relative to Earth years
const ORBITAL_PERIODS = {
  mercury: 0.2408467,
  venus: 0.61519726,
  earth: 1.0,
  mars: 1.8808158,
  jupiter: 11.862615,
  saturn: 29.447498,
  uranus: 84.016846,
  neptune: 164.79132,
};

export const age = (planet, seconds) => {
  // Calculate age in Earth years
  const ageInEarthYears = seconds / EARTH_YEAR_IN_SECONDS;

  // Get the orbital period for the given planet
  const orbitalPeriod = ORBITAL_PERIODS[planet.toLowerCase()];

  // Calculate age on the target planet
  const ageOnPlanet = ageInEarthYears / orbitalPeriod;

  // Round the result to two decimal places
  // Use Number.parseFloat and toFixed for reliable rounding
  return Number.parseFloat(ageOnPlanet.toFixed(2));
};
