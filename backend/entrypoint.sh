#!/bin/sh
set -e
mkdir -p /data
if [ ! -f "$DB_PATH" ]; then
  echo "No database found, seeding from repo copy"
  cp /seed/database.db "$DB_PATH"
fi
exec "$@"