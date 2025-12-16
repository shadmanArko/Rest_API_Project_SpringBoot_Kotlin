package com.arko.analytics.migration

data class ClickHouseMigration(
    val version: Int,
    val description: String,
    val path: String
)
