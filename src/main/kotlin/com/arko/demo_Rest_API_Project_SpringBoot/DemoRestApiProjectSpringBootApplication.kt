package com.arko.demo_Rest_API_Project_SpringBoot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableFeignClients(basePackages = ["com.arko.accounting.client"])
@ComponentScan(basePackages = ["com.arko"])
@EntityScan(basePackages = ["com.arko"])
@EnableJpaRepositories(basePackages = ["com.arko"])
class DemoRestApiProjectSpringBootApplication

fun main(args: Array<String>) {
    runApplication<DemoRestApiProjectSpringBootApplication>(*args)
}