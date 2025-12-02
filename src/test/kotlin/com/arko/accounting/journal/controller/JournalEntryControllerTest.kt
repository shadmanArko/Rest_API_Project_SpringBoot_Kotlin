package com.arko.accounting.journal.controller

import com.arko.accounting.journal.domain.JournalEntryStatus
import com.arko.accounting.journal.dto.CreateJournalEntryRequest
import com.arko.accounting.journal.dto.JournalEntryDto
import com.arko.accounting.journal.dto.JournalLineDto
import com.arko.accounting.journal.service.JournalEntryService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.mockito.Mockito

@ContextConfiguration(classes = [JournalEntryControllerTest.TestApplication::class])
@WebMvcTest(JournalEntryController::class)
@AutoConfigureMockMvc(addFilters = false)
class JournalEntryControllerTest {

    @SpringBootApplication(scanBasePackages = ["com.arko.accounting.journal.controller"])
    class TestApplication

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: JournalEntryService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `create should return 200 when request is valid`() {
        val companyId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val req = CreateJournalEntryRequest(
            date = LocalDate.now(),
            reference = "REF-001",
            description = "Test Entry",
            lines = listOf(
                JournalLineDto(accountId = accountId, debit = BigDecimal("100.00")),
                JournalLineDto(accountId = accountId, credit = BigDecimal("100.00"))
            )
        )
        
        val responseDto = JournalEntryDto(
             id = UUID.randomUUID(),
             date = req.date,
             reference = req.reference,
             description = req.description,
             status = JournalEntryStatus.DRAFT,
             lines = req.lines
        )

        `when`(service.create(eq(companyId), anyCreateRequest())).thenReturn(responseDto)

        mockMvc.perform(post("/journal-entries")
            .header("X-Company-ID", companyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk)
    }

    private fun anyCreateRequest(): CreateJournalEntryRequest {
        Mockito.any(CreateJournalEntryRequest::class.java)
        return CreateJournalEntryRequest(LocalDate.now(), null, null, emptyList())
    }

    @Test
    fun `create should return 400 when header is missing`() {
        val accountId = UUID.randomUUID()
        val req = CreateJournalEntryRequest(
            date = LocalDate.now(),
            reference = "REF-001",
            description = "Test Entry",
            lines = listOf(
                JournalLineDto(accountId = accountId, debit = BigDecimal("100.00")),
                JournalLineDto(accountId = accountId, credit = BigDecimal("100.00"))
            )
        )

        mockMvc.perform(post("/journal-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create should return 400 when date is null`() {
        // We can't pass null to non-nullable Kotlin type, but we can simulate invalid JSON
        val invalidJson = """
            {
                "reference": "REF-001",
                "lines": []
            }
        """.trimIndent()

        mockMvc.perform(post("/journal-entries")
            .header("X-Company-ID", UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create should return 400 when lines are empty`() {
        val req = CreateJournalEntryRequest(
            date = LocalDate.now(),
            reference = "REF-001",
            description = "Test Entry",
            lines = emptyList()
        )

        mockMvc.perform(post("/journal-entries")
            .header("X-Company-ID", UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest)
    }
}
