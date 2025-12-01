package com.arko.accounting.reports.service

import com.arko.accounting.account.domain.AccountType
import com.arko.accounting.account.repository.AccountRepository
import com.arko.accounting.ledger.repository.LedgerEntryRepository
import com.arko.accounting.reports.dto.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class FinancialReportServiceImpl(
    private val accountRepo: AccountRepository,
    private val ledgerRepo: LedgerEntryRepository
) : FinancialReportService {

    override fun generateBalanceSheet(companyId: UUID): BalanceSheetDto {

        // Note: Account entity currently doesn't have companyId, so we fetch all accounts
        val accounts = accountRepo.findAll()

        fun calc(type: AccountType): BalanceSheetSectionDto {
            val sectionAccounts = accounts.filter { it.type == type }

            val items = sectionAccounts.map { acc ->
                val entries = ledgerRepo.findByAccountId(acc.id)
                val balance = entries.sumOf { it.debit - it.credit }
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
        val equity = calc(AccountType.EQUITY)

        val isBalanced = assets.total == liabilities.total + equity.total

        return BalanceSheetDto(
            assets = assets,
            liabilities = liabilities,
            equity = equity,
            isBalanced = isBalanced
        )
    }

    override fun generateIncomeStatement(companyId: UUID): IncomeStatementDto {

        val accounts = accountRepo.findAll()

        fun calc(type: AccountType): IncomeStatementSectionDto {
            val sectionAccounts = accounts.filter { it.type == type }

            val items = sectionAccounts.map { acc ->
                val entries = ledgerRepo.findByAccountId(acc.id)
                val amount = entries.sumOf { it.credit - it.debit } // revenues positive
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
