package com.arko.accounting.ledger.service

import com.arko.accounting.account.domain.Account
import com.arko.accounting.account.repository.AccountRepository
import com.arko.accounting.ledger.dto.TrialBalanceDto
import com.arko.accounting.ledger.dto.TrialBalanceRowDto
import com.arko.accounting.ledger.repository.LedgerEntryRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class TrialBalanceServiceImpl(
    private val ledgerRepo: LedgerEntryRepository,
    private val accountRepo: AccountRepository
) : TrialBalanceService {

    override fun generate(companyId: UUID): TrialBalanceDto {

        // Note: Account entity currently doesn't have companyId, so we fetch all accounts
        // In a real multi-tenant app, Account should have companyId
        val accounts = accountRepo.findAll()

        val rows = accounts.map { account ->
            val entries = ledgerRepo.findByAccountId(account.id)

            val totalDebit = entries.sumOf { it.debit }
            val totalCredit = entries.sumOf { it.credit }

            TrialBalanceRowDto(
                accountCode = account.code,
                accountName = account.name,
                debit = totalDebit,
                credit = totalCredit
            )
        }

        val totalDebit = rows.sumOf { it.debit }
        val totalCredit = rows.sumOf { it.credit }

        return TrialBalanceDto(
            rows = rows,
            totalDebit = totalDebit,
            totalCredit = totalCredit,
            isBalanced = (totalDebit.compareTo(totalCredit) == 0)
        )
    }
}
