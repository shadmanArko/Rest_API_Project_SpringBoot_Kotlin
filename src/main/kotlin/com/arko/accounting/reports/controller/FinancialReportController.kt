package com.arko.accounting.reports.controller

import com.arko.accounting.reports.dto.*
import com.arko.accounting.reports.service.FinancialReportService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/reports")
class FinancialReportController(
    private val reportService: FinancialReportService
) {

    @GetMapping("/balance-sheet")
    fun balanceSheet(@RequestHeader("X-Company-ID") companyId: UUID): BalanceSheetDto {
        return reportService.generateBalanceSheet(companyId)
    }

    @GetMapping("/income-statement")
    fun incomeStatement(@RequestHeader("X-Company-ID") companyId: UUID): IncomeStatementDto {
        return reportService.generateIncomeStatement(companyId)
    }

    @GetMapping("/cash-flow")
    fun cashFlow(@RequestHeader("X-Company-ID") companyId: UUID): CashFlowStatementDto {
        return reportService.generateCashFlow(companyId)
    }
}
