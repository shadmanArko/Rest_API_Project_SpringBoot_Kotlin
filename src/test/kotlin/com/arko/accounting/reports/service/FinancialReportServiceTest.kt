package com.arko.accounting.reports.service

import com.arko.accounting.account.domain.Account
import com.arko.accounting.account.domain.AccountType
import com.arko.accounting.account.repository.AccountRepository
import com.arko.accounting.ledger.repository.LedgerEntryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class FinancialReportServiceTest {

    @Mock
    lateinit var accountRepo: AccountRepository

    @Mock
    lateinit var ledgerRepo: LedgerEntryRepository

    @InjectMocks
    lateinit var reportService: FinancialReportServiceImpl

    @Test
    fun `generateBalanceSheet should not crash with empty data`() {
        // Given
        val companyId = UUID.randomUUID()
        `when`(accountRepo.findAll()).thenReturn(emptyList())

        // When
        val result = reportService.generateBalanceSheet(companyId)

        // Then
        assertNotNull(result)
        assertEquals(0, result.assets.items.size)
        assertEquals(0, result.liabilities.items.size)
        assertEquals(1, result.equity.items.size) // Net Income
        assertTrue(result.isBalanced)
    }

    @Test
    fun `generateBalanceSheet should calculate correctly`() {
        // Given
        val companyId = UUID.randomUUID()
        val assetAccount = Account(name = "Cash", type = AccountType.ASSET, code = "100")
        
        `when`(accountRepo.findAll()).thenReturn(listOf(assetAccount))
        `when`(ledgerRepo.findByAccountId(assetAccount.id)).thenReturn(emptyList())

        // When
        val result = reportService.generateBalanceSheet(companyId)

        // Then
        assertNotNull(result)
        assertEquals(1, result.assets.items.size)
        assertEquals("Cash", result.assets.items[0].accountName)
    }
}
