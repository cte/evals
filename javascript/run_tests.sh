#!/usr/bin/env bash

# https://github.com/exercism/javascript/blob/main/docs/TESTS.md

lang="javascript"
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

    if [ -f "$dir/package.json" ]; then
      ((total_count++))
      (timeout 30s bash -c "cd '$dir' && pnpm install >/dev/null 2>&1 && pnpm test >/dev/null 2>&1")

      if [ $? -eq 0 ]; then
        echo "🟢 $lang/$name"
        ((success_count++))
      else
        echo "🔴 $lang/$name"
        ((failure_count++))
      fi
    else
      echo "⚠️ Skipped (no package.json found)"
    fi
  fi
done

echo "$success_count / $total_count ($(((success_count * 100) / (total_count > 0 ? total_count : 1)))%)"
