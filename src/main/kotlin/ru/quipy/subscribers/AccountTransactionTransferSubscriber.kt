package ru.quipy.subscribers

import org.springframework.stereotype.Component
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TransferAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

@Component
class AccountTransactionTransferSubscriber(
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val transferEsService: EventSourcingService<UUID, TransferAggregate, TransferAggregateState>,
) {

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(
            AccountAggregate::class,
            "transfers::account-transaction-processing-subscriber",
        ) {
            `when`(TransferDepositPerformedEvent::class) { event ->
                transferEsService.update(event.transferId) {
                    it.succeeded()
                }
            }
            `when`(TransferWithdrawRejectedEvent::class) { event ->
                transferEsService.update(event.transferId) {
                    it.failed()
                }
            }
            `when`(TransferDepositRejectedEvent::class) { event ->
                transferEsService.update(event.transferId) {
                    it.failed()
                }
            }
        }
    }
}