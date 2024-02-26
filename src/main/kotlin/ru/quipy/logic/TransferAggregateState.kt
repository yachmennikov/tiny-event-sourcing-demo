package ru.quipy.logic

import ru.quipy.api.TransferAggregate
import ru.quipy.api.TransferFailedEvent
import ru.quipy.api.TransferInitiatedEvent
import ru.quipy.api.TransferSucceededEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.math.BigDecimal
import java.util.UUID

class TransferAggregateState : AggregateState<UUID, TransferAggregate> {
    private lateinit var transferId: UUID
    lateinit var sourceAccountId: UUID
    lateinit var sourceBankAccountId: UUID
    lateinit var destinationAccountId: UUID
    lateinit var destinationBankAccountId: UUID
    lateinit var amount: BigDecimal
    var state: TransferState = TransferState.PENDING

    override fun getId(): UUID = transferId

    fun initiateNewTransfer(
        sourceAccountId: UUID,
        sourceBankAccountId: UUID,
        destinationAccountId: UUID,
        destinationBankAccountId: UUID,
        amount: BigDecimal,
    ): TransferInitiatedEvent = TransferInitiatedEvent(
        transferId = UUID.randomUUID(),
        sourceAccountId = sourceAccountId,
        sourceBankAccountId = sourceBankAccountId,
        destinationAccountId = destinationAccountId,
        destinationBankAccountId = destinationBankAccountId,
        amount = amount,
    )

    fun succeeded(): TransferSucceededEvent = TransferSucceededEvent(transferId)

    fun failed(): TransferFailedEvent = TransferFailedEvent(transferId)


    @StateTransitionFunc
    fun initiateNewTransfer(event: TransferInitiatedEvent) {
        transferId = event.transferId
        sourceAccountId = event.sourceAccountId
        sourceBankAccountId = event.sourceBankAccountId
        destinationAccountId = event.destinationAccountId
        destinationBankAccountId = event.destinationBankAccountId
        amount = event.amount
    }

    @StateTransitionFunc
    fun fail(event: TransferFailedEvent) {
        state = TransferState.FAILED
    }

    @StateTransitionFunc
    fun succeed(event: TransferSucceededEvent) {
        state = TransferState.SUCCEEDED
    }

    enum class TransferState {
        PENDING,
        SUCCEEDED,
        FAILED,
    }
}