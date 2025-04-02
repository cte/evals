#!/usr/bin/env bash

# https://github.com/exercism/rust/blob/main/docs/TESTS.md
# https://www.rust-lang.org/tools/install

lang="rust"
success_count=0
failure_count=0
total_count=0

cleanup() {
  exit 1
}

trap cleanup SIGINT SIGTERM

echo "--------------------------------------------------------------------------------"

for dir in */; do
  if [ -d "$dir" ]; then
    name=${dir%/} # Remove trailing slash from directory name.

    if [ -f "$dir/Cargo.toml" ]; then
      ((total_count++))
      (timeout 15s bash -c "cd '$dir' && cargo test > /dev/null 2>&1")

      if [ $? -eq 0 ]; then
        echo "🟢 $lang/$name"
        ((success_count++))
      else
        echo "🔴 $lang/$name"
        ((failure_count++))
      fi
    else
      echo "⚠️ Skipped (no Cargo.toml found)"
    fi
  fi
done

echo "$success_count / $total_count ($(((success_count * 100) / (total_count > 0 ? total_count : 1)))%)"
