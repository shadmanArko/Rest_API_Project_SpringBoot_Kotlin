package com.arko.accounting.account.controller

import com.arko.accounting.account.dto.AccountCreateRequest
import com.arko.accounting.account.dto.AccountUpdateRequest
import com.arko.accounting.account.dto.AccountResponse
import com.arko.accounting.account.service.AccountService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val service: AccountService
) {

    @PostMapping
    fun create(@RequestBody req: AccountCreateRequest): AccountResponse =
        service.createAccount(req)

    @GetMapping
    fun getAll(): List<AccountResponse> =
        service.getAllAccounts()

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: UUID): AccountResponse =
        service.getAccount(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody req: AccountUpdateRequest): AccountResponse =
        service.updateAccount(id, req)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) =
        service.deleteAccount(id)
}
