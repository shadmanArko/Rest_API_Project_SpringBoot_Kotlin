package com.arko.accounting.account.service

import com.arko.accounting.account.domain.Account
import com.arko.accounting.account.dto.*
import com.arko.accounting.account.repository.AccountRepository
import com.arko.accounting.exceptions.NotFoundException
import com.arko.accounting.exceptions.ValidationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AccountServiceImpl(
    private val repo: AccountRepository
) : AccountService {

    @Transactional
    override fun createAccount(req: AccountCreateRequest): AccountResponse {

        if (repo.existsByCode(req.code)) {
            throw ValidationException("Account code already exists.")
        }

        if (!req.isParent && req.parentId == null) {
            throw ValidationException("Non-parent accounts must have a parentId.")
        }

        val account = repo.save(
            Account(
                code = req.code,
                name = req.name,
                type = req.type,
                isParent = req.isParent,
                parentId = req.parentId,
                description = req.description
            )
        )

        return account.toResponse()
    }

    override fun getAllAccounts(): List<AccountResponse> =
        repo.findAll().map { it.toResponse() }

    override fun getAccount(id: UUID): AccountResponse =
        repo.findById(id).orElseThrow { NotFoundException("Account not found") }
            .toResponse()

    @Transactional
    override fun updateAccount(id: UUID, req: AccountUpdateRequest): AccountResponse {
        val acc = repo.findById(id).orElseThrow { NotFoundException("Account not found") }

        val updated = acc.copy(
            name = req.name,
            isActive = req.isActive,
            description = req.description
        )

        return repo.save(updated).toResponse()
    }

    @Transactional
    override fun deleteAccount(id: UUID) {
        val acc = repo.findById(id).orElseThrow { NotFoundException("Account not found") }

        // Prevent deleting if child accounts exist
        if (repo.existsByParentId(id)) {
            throw ValidationException("Cannot delete account with child accounts.")
        }

        repo.delete(acc)
    }

    private fun Account.toResponse() = AccountResponse(
        id = id,
        code = code,
        name = name,
        type = type,
        isParent = isParent,
        parentId = parentId,
        isActive = isActive,
        description = description
    )
}
