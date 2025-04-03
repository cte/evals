//
// This is only a SKELETON file for the 'Promises' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export const promisify = (fn) => {
  return (...args) =>
    new Promise((resolve, reject) => {
      fn(...args, (err, result) => {
        if (err) {
          reject(err);
        } else {
          resolve(result);
        }
      });
    });
};

export const all = (promises) => {
  if (promises === undefined) return Promise.resolve(undefined);
  if (!Array.isArray(promises) || promises.length === 0) return Promise.resolve(promises || []);
  return Promise.all(promises);
};

export const allSettled = (promises) => {
  if (promises === undefined) return Promise.resolve(undefined);
  if (!Array.isArray(promises) || promises.length === 0) return Promise.resolve(promises || []);
  return Promise.all(
    promises.map((p) =>
      Promise.resolve(p).catch((err) => err)
    )
  );
};

export const race = (promises) => {
  if (promises === undefined) return Promise.resolve(undefined);
  if (!Array.isArray(promises) || promises.length === 0) return Promise.resolve(promises || []);
  return Promise.race(promises);
};

export const any = (promises) => {
  if (promises === undefined) return Promise.resolve(undefined);
  if (!Array.isArray(promises) || promises.length === 0) return Promise.resolve(promises || []);

  return new Promise((resolve, reject) => {
    let rejections = [];
    let pending = promises.length;
    let resolved = false;

    promises.forEach((p, index) => {
      Promise.resolve(p)
        .then((value) => {
          if (!resolved) {
            resolved = true;
            resolve(value);
          }
        })
        .catch((err) => {
          rejections[index] = err;
          pending--;
          if (pending === 0 && !resolved) {
            reject(rejections);
          }
        });
    });
  });
};
