#!/bin/bash

# Start Spring Boot app with ClickHouse enabled
echo "Starting Spring Boot application with ClickHouse enabled..."

export CLICKHOUSE_ENABLED=true

# Run the app
./gradlew bootRun
