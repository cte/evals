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
  try {
    const data = fs.readFileSync(path.resolve(file), { encoding: 'utf-8' });
    // Split lines and handle potential trailing newline which results in an empty string
    const lines = data.split(/\r?\n/);
    if (lines[lines.length - 1] === '') {
      lines.pop();
    }
    return lines;
  } catch (err) {
    console.error(`Error reading file ${file}: ${err.message}`);
    process.exit(1); // Exit if file cannot be read
  }
}

const VALID_OPTIONS = [
  'n', // add line numbers
  'l', // print file names where pattern is found
  'i', // ignore case
  'v', // reverse files results
  'x', // match entire line
];

const ARGS = process.argv.slice(2); // Remove 'node' and script path

// --- Argument Parsing ---
let pattern = '';
const flags = new Set();
const files = [];

// Identify flags, pattern, and files
let patternFound = false;
for (const arg of ARGS) {
  if (arg.startsWith('-')) {
    // It's a flag (or potentially multiple flags combined like -nix)
    for (let i = 1; i < arg.length; i++) {
      const flag = arg[i];
      if (VALID_OPTIONS.includes(flag)) {
        flags.add(flag);
      } else {
        console.error(`Invalid flag: -${flag}`);
        process.exit(1);
      }
    }
  } else if (!patternFound) {
    pattern = arg;
    patternFound = true;
  } else {
    files.push(arg);
  }
}

if (!pattern) {
  console.error('Error: Search pattern is required.');
  process.exit(1);
}

if (files.length === 0) {
  console.error('Error: At least one file path is required.');
  process.exit(1);
}

// --- Grep Logic ---
const results = [];
const matchedFiles = new Set();
const multipleFiles = files.length > 1;

for (const file of files) {
  const lines = readLines(file);
  let fileHasMatch = false;

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    const lineNumber = i + 1;

    let lineToCompare = line;
    let patternToCompare = pattern;

    if (flags.has('i')) {
      lineToCompare = line.toLowerCase();
      patternToCompare = pattern.toLowerCase();
    }

    let isMatch;
    if (flags.has('x')) {
      isMatch = lineToCompare === patternToCompare;
    } else {
      isMatch = lineToCompare.includes(patternToCompare);
    }

    if (flags.has('v')) {
      isMatch = !isMatch;
    }

    if (isMatch) {
      fileHasMatch = true;
      if (flags.has('l')) {
        // If -l is set, we only need to know the file matched, break early
        matchedFiles.add(file);
        break; // Move to the next file
      }

      let outputLine = '';
      if (multipleFiles) {
        outputLine += `${file}:`;
      }
      if (flags.has('n')) {
        outputLine += `${lineNumber}:`;
      }
      outputLine += line;
      results.push(outputLine);
    }
  }
  // If -l flag is set and we found a match in this file, add it to the set
  // (This handles the case where the loop didn't break early because -l wasn't the only flag)
  if (flags.has('l') && fileHasMatch) {
      matchedFiles.add(file);
  }
}

// --- Output ---
if (flags.has('l')) {
  matchedFiles.forEach(file => console.log(file));
} else {
  results.forEach(line => console.log(line));
}
