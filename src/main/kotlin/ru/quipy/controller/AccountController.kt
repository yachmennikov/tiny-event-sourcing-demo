package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.AccountAggregate
import ru.quipy.api.AccountCreatedEvent
import ru.quipy.api.BankAccountCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.domain.Event
import ru.quipy.logic.AccountAggregateState
import ru.quipy.logic.BankAccount
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    val accountEsService: EventSourcingService<UUID, AccountAggregate, AccountAggregateState>
) {

    @PostMapping("/{holderId}")
    fun createAccount(@PathVariable holderId: UUID): AccountCreatedEvent {
        return accountEsService.create { it.createNewAccount(holderId = holderId) }
    }

    @GetMapping("/{accountId}")
    fun getAccount(@PathVariable accountId: UUID): AccountAggregateState? {
        return accountEsService.getState(accountId)
    }

    @PostMapping("/{accountId}/bankAccount")
    fun createBankAccount(@PathVariable accountId: UUID): BankAccountCreatedEvent {
        return accountEsService.update(accountId) { it.createNewBankAccount() }
    }

    @GetMapping("/{accountId}/bankAccount/{bankAccountId}")
    fun getBankAccount(@PathVariable accountId: UUID, @PathVariable bankAccountId: UUID): BankAccount? {
        return accountEsService.getState(accountId)?.bankAccounts?.get(bankAccountId)
    }

    @PostMapping("/deposit")
    fun makeDeposit(
        @RequestParam accountId: UUID,
        @RequestParam bankAccountId: UUID,
        @RequestParam amount: BigDecimal,
    ): Event<AccountAggregate> {
        return accountEsService.update(accountId) { it.deposit(bankAccountId, amount) }
    }

    @PostMapping("/withdraw")
    fun makeWithdraw(
        @RequestParam accountId: UUID,
        @RequestParam bankAccountId: UUID,
        @RequestParam amount: BigDecimal,
    ): Event<AccountAggregate> {
        return accountEsService.update(accountId) { it.withdraw(bankAccountId, amount) }
    }

}