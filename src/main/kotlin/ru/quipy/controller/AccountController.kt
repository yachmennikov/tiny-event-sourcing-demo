package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.AccountAggregate
import ru.quipy.api.AccountCreatedEvent
import ru.quipy.api.BankAccountCreatedEvent
import ru.quipy.logic.AccountAggregateState
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.BankAccount

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
}