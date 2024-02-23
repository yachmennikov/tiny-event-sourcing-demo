package ru.quipy.logic

import ru.quipy.api.AccountAggregate
import ru.quipy.api.AccountCreatedEvent
import ru.quipy.api.BankAccountCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

const val BANK_ACCOUNTS_COUNT_LIMIT = 5
const val BANK_ACCOUNT_LIMIT: Int = 10_000_000
const val BANK_ACCOUNTS_TOTAL_LIMIT: Int = 25_000_000

class AccountAggregateState : AggregateState<UUID, AccountAggregate> {

    var bankAccounts: MutableMap<UUID, BankAccount> = mutableMapOf()
    private lateinit var accountId: UUID
    private lateinit var holderId: UUID

    override fun getId() = accountId

    fun createNewAccount(id: UUID = UUID.randomUUID(), holderId: UUID): AccountCreatedEvent {
        return AccountCreatedEvent(id, holderId)
    }

    fun createNewBankAccount(accountId: UUID, bankAccountId: UUID = UUID.randomUUID()): BankAccountCreatedEvent {
        if (bankAccounts.size >= BANK_ACCOUNTS_COUNT_LIMIT)
            throw IllegalStateException("Account $accountId already has ${bankAccounts.size} bank accounts")

        return BankAccountCreatedEvent(accountId, bankAccountId)
    }

     // aggregate state functions
    @StateTransitionFunc
    fun newAccountCreatedApply(event: AccountCreatedEvent) {
        accountId = event.accountId
        holderId = event.userId
    }

    @StateTransitionFunc
    fun newBankAccountCreatedApply(event: BankAccountCreatedEvent) {
        bankAccounts[event.bankAccountId] = BankAccount(event.bankAccountId)
    }
}