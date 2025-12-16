package com.arko.analytics.migration

object ClickHouseMigrations {

    val ALL = listOf(
        ClickHouseMigration(1, "init_schema", "db/clickhouse/V1__init_schema.sql"),
        ClickHouseMigration(2, "raw_events", "db/clickhouse/V2__raw_events.sql"),
        ClickHouseMigration(3, "campaign_hourly", "db/clickhouse/V3__campaign_hourly.sql"),
        ClickHouseMigration(4, "materialized_views", "db/clickhouse/V4__materialized_views.sql")
    )
}
