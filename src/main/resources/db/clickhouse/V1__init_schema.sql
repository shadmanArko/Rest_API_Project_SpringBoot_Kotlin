CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.schema_version
(
    version UInt32,
    description String,
    applied_at DateTime
)
ENGINE = TinyLog;
