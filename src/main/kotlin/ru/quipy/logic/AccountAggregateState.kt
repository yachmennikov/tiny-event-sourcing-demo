package ru.quipy.logic

import ru.quipy.api.AccountAggregate
import ru.quipy.api.AccountCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

const val BANK_ACCOUNT_LIMIT: Int = 10_000_000
const val BANK_ACCOUNTS_TOTAL_LIMIT: Int = 25_000_000

class AccountAggregateState : AggregateState<UUID, AccountAggregate> {

    private lateinit var accountId: UUID
    private lateinit var holderId: UUID
    override fun getId() = accountId

    fun createNewAccount(id: UUID = UUID.randomUUID(), holderId: UUID): AccountCreatedEvent {
        return AccountCreatedEvent(id, holderId)
    }

    @StateTransitionFunc
    fun createNewBankAccount(event: AccountCreatedEvent) {
        accountId = event.accountId
        holderId = event.userId
    }
}