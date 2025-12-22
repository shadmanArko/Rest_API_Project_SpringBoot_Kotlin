package com.arko.accounting.reports.service

import com.arko.accounting.account.domain.AccountType
import com.arko.accounting.account.repository.AccountRepository
import com.arko.accounting.ledger.repository.LedgerEntryRepository
import com.arko.accounting.reports.dto.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class FinancialReportServiceImpl(
    private val accountRepo: AccountRepository,
    private val ledgerRepo: LedgerEntryRepository
) : FinancialReportService {

    private val logger = LoggerFactory.getLogger(FinancialReportServiceImpl::class.java)

    override fun generateBalanceSheet(companyId: UUID): BalanceSheetDto {
        try {

        // Note: Account entity currently doesn't have companyId, so we fetch all accounts
        val accounts = accountRepo.findAll()

        fun calc(type: AccountType): BalanceSheetSectionDto {
            val sectionAccounts = accounts.filter { it.type == type }

            val items = sectionAccounts.map { acc ->
                val entries = ledgerRepo.findByAccountId(acc.id)
                // Calculate balance based on normal balance type
                val balance = if (type == AccountType.ASSET || type == AccountType.EXPENSE) {
                    entries.sumOf { it.debit - it.credit }
                } else {
                    entries.sumOf { it.credit - it.debit }
                }
                BalanceSheetItemDto(acc.name, balance)
            }

            val total = items.sumOf { it.amount }

            return BalanceSheetSectionDto(
                title = type.name.lowercase().replaceFirstChar(Char::uppercase),
                items = items,
                total = total
            )
        }

        val assets = calc(AccountType.ASSET)
        val liabilities = calc(AccountType.LIABILITY)
        
        // Calculate Equity
        val equityBase = calc(AccountType.EQUITY)
        
        // Calculate Net Income for Retained Earnings
        val revenue = calc(AccountType.REVENUE)
        val expenses = calc(AccountType.EXPENSE)
        val netIncome = revenue.total - expenses.total

        // Add Net Income to Equity items
        val equityItems = equityBase.items.toMutableList()
        equityItems.add(BalanceSheetItemDto("Net Income", netIncome))
        
        val equityTotal = equityBase.total + netIncome
        
        val equity = BalanceSheetSectionDto(
            title = "Equity",
            items = equityItems,
            total = equityTotal
        )

        val isBalanced = assets.total.compareTo(liabilities.total + equity.total) == 0

        return BalanceSheetDto(
            assets = assets,
            liabilities = liabilities,
            equity = equity,
            isBalanced = isBalanced
        )
        } catch (e: Exception) {
            logger.error("Error generating balance sheet for company $companyId", e)
            throw e
        }
    }

    override fun generateIncomeStatement(companyId: UUID): IncomeStatementDto {
        try {

        val accounts = accountRepo.findAll()

        fun calc(type: AccountType): IncomeStatementSectionDto {
            val sectionAccounts = accounts.filter { it.type == type }

            val items = sectionAccounts.map { acc ->
                val entries = ledgerRepo.findByAccountId(acc.id)
                // Calculate balance based on normal balance type
                val amount = if (type == AccountType.EXPENSE) {
                    entries.sumOf { it.debit - it.credit }
                } else {
                    entries.sumOf { it.credit - it.debit }
                }
                IncomeStatementItemDto(acc.name, amount)
            }

            val total = items.sumOf { it.amount }

            return IncomeStatementSectionDto(
                title = type.name.lowercase().replaceFirstChar(Char::uppercase),
                items = items,
                total = total
            )
        }

        val revenue = calc(AccountType.REVENUE)
        val expenses = calc(AccountType.EXPENSE)

        val netIncome = revenue.total - expenses.total

        return IncomeStatementDto(
            revenue = revenue,
            expenses = expenses,
            netIncome = netIncome
        )
        } catch (e: Exception) {
            logger.error("Error generating income statement for company $companyId", e)
            throw e
        }
    }

    override fun generateCashFlow(companyId: UUID): CashFlowStatementDto {
        // MVP: net cash change = change in cash account(s)

        val accounts = accountRepo.findAll()
        val cashAccounts = accounts.filter { it.code.startsWith("100") } // simple rule

        val totalCash = cashAccounts.sumOf { acc ->
            val entries = ledgerRepo.findByAccountId(acc.id)
            entries.sumOf { it.debit - it.credit }
        }

        val operating = CashFlowSectionDto("Operating", totalCash)
        val investing = CashFlowSectionDto("Investing", BigDecimal.ZERO)
        val financing = CashFlowSectionDto("Financing", BigDecimal.ZERO)

        val netCashFlow = operating.amount

        return CashFlowStatementDto(
            operating = operating,
            investing = investing,
            financing = financing,
            netCashFlow = netCashFlow
        )
    }
}
