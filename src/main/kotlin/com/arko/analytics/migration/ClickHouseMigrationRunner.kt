package com.arko.analytics.migration

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.sql.Connection

@Component
@ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class ClickHouseMigrationRunner(
    private val conn: Connection
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        ensureSchemaVersionTable(conn)

        val appliedVersions = loadAppliedVersions(conn)

        migrations()
            .filter { it.version !in appliedVersions }
            .sortedBy { it.version }
            .forEach { migration ->
                applyMigration(conn, migration)
            }
    }

    // ---- MIGRATIONS REGISTRY ----
    private fun migrations() = listOf(
        Migration(1, "init_schema", "db/clickhouse/V1__init_schema.sql"),
        Migration(2, "agg_campaign_hourly", "db/clickhouse/V2__agg_campaign_hourly.sql"),
        Migration(3, "agg_campaign_daily", "db/clickhouse/V3__agg_campaign_daily.sql"),
        Migration(4, "mv_hourly_to_daily", "db/clickhouse/V4__mv_hourly_to_daily.sql"),
    )

    // ---- CORE METHODS ----
    private fun ensureSchemaVersionTable(conn: Connection) {
        conn.createStatement().use { stmt ->
            stmt.execute("CREATE DATABASE IF NOT EXISTS analytics")
            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS analytics.schema_version
                (
                    version UInt32,
                    description String,
                    applied_at DateTime
                )
                ENGINE = TinyLog
                """
            )
        }
    }

    private fun loadAppliedVersions(conn: Connection): Set<Int> {
        val versions = mutableSetOf<Int>()

        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(
                "SELECT version FROM analytics.schema_version"
            )
            while (rs.next()) {
                versions.add(rs.getInt("version"))
            }
        }

        return versions
    }

    private fun applyMigration(conn: Connection, migration: Migration) {
        val sqlStatements = ClassPathResource(migration.path)
            .inputStream
            .bufferedReader()
            .readText()
            .split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        conn.createStatement().use { stmt ->
            sqlStatements.forEach { statement ->
                stmt.execute(statement)
            }
        }

        conn.prepareStatement(
            """
            INSERT INTO analytics.schema_version (version, description, applied_at)
            VALUES (?, ?, now())
            """
        ).apply {
            setInt(1, migration.version)
            setString(2, migration.description)
            execute()
        }

        println("✅ Applied ClickHouse migration V${migration.version} (${migration.description})")
    }

    // ---- INTERNAL MODEL ----
    data class Migration(
        val version: Int,
        val description: String,
        val path: String
    )
}
