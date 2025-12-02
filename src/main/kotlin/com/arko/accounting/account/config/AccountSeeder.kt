package com.arko.accounting.config

import com.arko.accounting.account.domain.Account
import com.arko.accounting.account.domain.AccountType
import com.arko.accounting.account.repository.AccountRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class AccountSeeder(
    private val repo: AccountRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (repo.count() > 0) return

        val defaults = listOf(
            Account(code = "1000", name = "Cash", type = AccountType.ASSET),
            Account(code = "1100", name = "Accounts Receivable", type = AccountType.ASSET),
            Account(code = "2000", name = "Accounts Payable", type = AccountType.LIABILITY),
            Account(code = "3000", name = "Owner's Equity", type = AccountType.EQUITY),
            Account(code = "4000", name = "Sales", type = AccountType.REVENUE),
            Account(code = "5000", name = "Office Supplies", type = AccountType.EXPENSE)
        )

        repo.saveAll(defaults)
    }
}
