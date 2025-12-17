package com.arko.demo_Rest_API_Project_SpringBoot.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

@Configuration
class DataSourceConfig {



    @Bean
    @Primary
    fun dataSource(properties: DataSourceProperties): DataSource {
        return properties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }
}
