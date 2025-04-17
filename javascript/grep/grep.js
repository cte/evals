#!/usr/bin/env node

// The above line is a shebang. On Unix-like operating systems, or environments,
// this will allow the script to be run by node, and thus turn this JavaScript
// file into an executable. In other words, to execute this file, you may run
// the following from your terminal:
//
// ./grep.js args
//
// If you don't have a Unix-like operating system or environment, for example
// Windows without WSL, you can use the following inside a window terminal,
// such as cmd.exe:
//
// node grep.js args
//
// Read more about shebangs here: https://en.wikipedia.org/wiki/Shebang_(Unix)

const fs = require('fs');
const path = require('path');

/**
 * Reads the given file and returns lines.
 *
 * This function works regardless of POSIX (LF) or windows (CRLF) encoding.
 *
 * @param {string} file path to file
 * @returns {string[]} the lines
 */
function readLines(file) {
  const data = fs.readFileSync(path.resolve(file), { encoding: 'utf-8' });
  return data.split(/\r?\n/);
}

const VALID_OPTIONS = [
  'n', // add line numbers
  'l', // print file names where pattern is found
  'i', // ignore case
  'v', // reverse files results
  'x', // match entire line
];

const ARGS = process.argv;

const args = ARGS.slice(2);

// Extract flags (starting with -)
const flags = [];
while (args[0] && args[0].startsWith('-')) {
  const flagChars = args.shift().slice(1);
  for (const char of flagChars) {
    if (VALID_OPTIONS.includes(char)) {
      flags.push(char);
    }
  }
}

const pattern = args.shift();
const files = args;

// Prepare regex flags
const regexFlags = flags.includes('i') ? 'i' : '';
const fullLineMatch = flags.includes('x');
const invertMatch = flags.includes('v');
const listFilenames = flags.includes('l');
const addLineNumbers = flags.includes('n');

const matchedFiles = [];
const outputLines = [];

for (const file of files) {
  const lines = readLines(file);
  let fileMatched = false;

  for (let idx = 0; idx < lines.length; idx++) {
    const line = lines[idx];
    let match = false;

    if (fullLineMatch) {
      const regex = new RegExp(`^${pattern}$`, regexFlags);
      match = regex.test(line);
    } else {
      const regex = new RegExp(pattern, regexFlags);
      match = regex.test(line);
    }

    if (invertMatch) {
      match = !match;
    }

    if (match) {
      fileMatched = true;
      if (!listFilenames) {
        const prefix = addLineNumbers ? `${idx + 1}:` : '';
        outputLines.push(`${files.length > 1 ? file + ':' : ''}${prefix}${line}`);
      }
    }
  }

  if (fileMatched && listFilenames) {
    matchedFiles.push(file);
  }
}

if (listFilenames) {
  console.log(matchedFiles.join('\n'));
} else {
  console.log(outputLines.join('\n'));
}
