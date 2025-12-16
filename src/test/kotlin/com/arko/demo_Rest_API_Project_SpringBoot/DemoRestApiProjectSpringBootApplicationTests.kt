package com.arko.demo_Rest_API_Project_SpringBoot

import com.arko.analytics.kafka.EventProducer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class DemoRestApiProjectSpringBootApplicationTests {

	@MockBean
	private lateinit var eventProducer: EventProducer

	@Test
	fun contextLoads() {
	}

}
