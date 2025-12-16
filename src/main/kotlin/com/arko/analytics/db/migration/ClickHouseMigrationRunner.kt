import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class ClickHouseMigrationRunner(
    private val dataSource: DataSource
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val scripts = listOf(
            "db/clickhouse/V1__init_schema.sql",
            "db/clickhouse/V2__hourly_campaign_agg.sql"
        )

        dataSource.connection.use { conn ->
            scripts.forEach { path ->
                val sql = ClassPathResource(path).inputStream.bufferedReader().readText()
                conn.createStatement().execute(sql)
            }
        }
    }
}
