package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.utils.formatNumber
import java.math.BigDecimal
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

    fun deposit(toBankAccountId: UUID, amount: BigDecimal): BankAccountDepositEvent {
        val bankAccount = (bankAccounts[toBankAccountId]
            ?: throw IllegalArgumentException("No such account to transfer to: $toBankAccountId"))

        if (bankAccount.balance + amount > BigDecimal(BANK_ACCOUNT_LIMIT))
            throw IllegalStateException(
                "You can't store more than ${formatNumber(BANK_ACCOUNT_LIMIT)} on account ${bankAccount.id}"
            )

        if (bankAccounts.values.sumOf { it.balance } + amount > BigDecimal(BANK_ACCOUNTS_TOTAL_LIMIT))
            throw IllegalStateException("You can't store more than ${formatNumber(BANK_ACCOUNTS_TOTAL_LIMIT)} in total")

        return BankAccountDepositEvent(
            accountId = accountId,
            bankAccountId = toBankAccountId,
            amount = amount
        )
    }

    fun withdraw(fromBankAccountId: UUID, amount: BigDecimal): BankAccountWithdrawalEvent {
        val fromBankAccount = bankAccounts[fromBankAccountId]
            ?: throw IllegalArgumentException("No such account to withdraw from: $fromBankAccountId")

        if (amount > fromBankAccount.balance) {
            throw IllegalArgumentException("Cannot withdraw $amount. Not enough money: ${fromBankAccount.balance}")
        }

        return BankAccountWithdrawalEvent(
            accountId = accountId,
            bankAccountId = fromBankAccountId,
            amount = amount
        )
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

    @StateTransitionFunc
    fun depositApply(event: BankAccountDepositEvent) {
        this.bankAccounts[event.bankAccountId]!!.deposit(event.amount)
    }

    @StateTransitionFunc
    fun withdrawApply(event: BankAccountWithdrawalEvent) {
        bankAccounts[event.bankAccountId]!!.withdraw(event.amount)
    }
}