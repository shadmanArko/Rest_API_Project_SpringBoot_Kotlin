package com.arko.accounting.account.service

import com.arko.accounting.account.dto.AccountCreateRequest
import com.arko.accounting.account.dto.AccountUpdateRequest
import com.arko.accounting.account.dto.AccountResponse
import java.util.UUID

interface AccountService {
    fun createAccount(req: AccountCreateRequest): AccountResponse
    fun getAllAccounts(): List<AccountResponse>
    fun getAccount(id: UUID): AccountResponse
    fun updateAccount(id: UUID, req: AccountUpdateRequest): AccountResponse
    fun deleteAccount(id: UUID)
}
