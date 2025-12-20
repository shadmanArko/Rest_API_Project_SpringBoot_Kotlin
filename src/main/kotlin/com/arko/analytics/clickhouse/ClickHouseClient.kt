package com.arko.analytics.clickhouse

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.sql.Connection
import java.sql.DriverManager

@Configuration
@ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class ClickHouseClientConfig(
    @Value("\${app.clickhouse.url}") private val url: String,
    @Value("\${app.clickhouse.user}") private val user: String,
    @Value("\${app.clickhouse.password}") private val password: String
) {

    @Bean
    fun clickhouseConnection(): Connection {
        return DriverManager.getConnection(url, user, password)
    }

    @Bean("clickHouseDataSource")
    fun clickHouseDataSource(): com.clickhouse.jdbc.ClickHouseDataSource {
        val props = java.util.Properties()
        props.setProperty("user", user)
        props.setProperty("password", password)
        return com.clickhouse.jdbc.ClickHouseDataSource(url, props)
    }
}
