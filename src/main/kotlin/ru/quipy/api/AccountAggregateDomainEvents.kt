package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.math.BigDecimal
import java.util.UUID

const val ACCOUNT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val BANK_ACCOUNT_CREATED_EVENT = "BANK_ACCOUNT_CREATED_EVENT"
const val BANK_ACCOUNT_DEPOSIT_EVENT = "BANK_ACCOUNT_DEPOSIT_EVENT"

@DomainEvent(name = ACCOUNT_CREATED_EVENT)
data class AccountCreatedEvent(
    val accountId: UUID,
    val userId: UUID,
) : Event<AccountAggregate>(
    name = ACCOUNT_CREATED_EVENT,
)

@DomainEvent(name = BANK_ACCOUNT_CREATED_EVENT)
data class BankAccountCreatedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_CREATED_EVENT,
)

@DomainEvent(name = BANK_ACCOUNT_DEPOSIT_EVENT)
data class BankAccountDepositEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val amount: BigDecimal,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_DEPOSIT_EVENT,
)