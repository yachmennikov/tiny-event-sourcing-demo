package ru.quipy.subscribers

import org.springframework.stereotype.Component
import ru.quipy.api.TransferAggregate
import ru.quipy.api.TransferFailedEvent
import ru.quipy.api.TransferInitiatedEvent
import ru.quipy.api.TransferSucceededEvent
import ru.quipy.projections.TransferProjection
import ru.quipy.projections.TransferProjectionService
import ru.quipy.projections.TransferState
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Component
class TransferProjectionSubscriber(
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val transferProjectionService: TransferProjectionService,
) {

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(
            TransferAggregate::class,
            "transfer-projections::transfer-processing-subscriber",
        ) {
            `when`(TransferInitiatedEvent::class) { event ->
                transferProjectionService.save(
                    TransferProjection(
                        transferId = event.transferId,
                        sourceAccountId = event.sourceAccountId,
                        sourceBankAccountId = event.sourceBankAccountId,
                        destinationAccountId = event.destinationAccountId,
                        destinationBankAccountId = event.destinationBankAccountId,
                        amount = event.amount,
                        state = TransferState.PENDING,
                    )
                )
            }
            `when`(TransferSucceededEvent::class) { event ->
                transferProjectionService.updateStateByTransferId(event.transferId, TransferState.SUCCEEDED)
            }
            `when`(TransferFailedEvent::class) { event ->
                transferProjectionService.updateStateByTransferId(event.transferId, TransferState.FAILED)
            }
        }
    }
}