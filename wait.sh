#!/bin/sh

host=$1
port=$2

echo "Waiting for connection to $host:$port"

status=1
while [[ "$status" -ne "0" ]]; do
    nc "$host" "$port" -z
    status=$?

    if [[ "$status" -ne "0" ]]; then
     echo "Connection $host:$port is down"
     sleep 1
    fi
done

echo "Connection to $host:$port successful"

return 0