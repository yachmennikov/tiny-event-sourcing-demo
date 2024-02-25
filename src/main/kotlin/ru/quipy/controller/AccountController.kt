package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.logic.AccountAggregateState
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.BankAccount
import java.math.BigDecimal

import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    val accountEsService: EventSourcingService<UUID, AccountAggregate, AccountAggregateState>
) {

    @GetMapping("/{holderId}")
    fun getAccount(@PathVariable holderId: UUID) : AccountAggregateState? {
        return accountEsService.getState(holderId)
    }

    @PostMapping("/{holderId}")
    fun createAccount(@PathVariable holderId: UUID) : AccountCreatedEvent {
        return accountEsService.create { it.createNewAccount(holderId = holderId) }
    }

    @PostMapping("/{accountId}/bankAccounts")
    fun createBankAccount(@PathVariable accountId: UUID) : BankAccountCreatedEvent {
        return accountEsService.update(accountId) { it.createNewBankAccount(accountId) }
    }

    @GetMapping("/{accountId}/bankAccounts/{bankAccountId}")
    fun getBankAccount(@PathVariable accountId: UUID, @PathVariable bankAccountId: UUID) : BankAccount? {
        return accountEsService.getState(accountId)?.bankAccounts?.get(bankAccountId)
    }

    @PostMapping("/deposit")
    fun deposit(
        @RequestParam accountId: UUID,
        @RequestParam bankAccountId: String,
        @RequestParam amount: BigDecimal
    ) : BankAccountDepositEvent {
        return accountEsService.update(accountId) { it.deposit(accountId, amount) }
    }

    @PostMapping("/withdraw")
    fun withdraw(
        @RequestParam accountId: UUID,
        @RequestParam bankAccountId: String,
        @RequestParam amount: BigDecimal
    ) : BankAccountWithdrawalEvent {
        return accountEsService.update(accountId) { it.withdraw(accountId, amount) }
    }
}