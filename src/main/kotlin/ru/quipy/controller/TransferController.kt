package ru.quipy.controller

import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.TransferAggregate
import ru.quipy.api.TransferInitiatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TransferAggregateState
import ru.quipy.projections.BankAccountCacheRepository
import ru.quipy.projections.TransferProjection
import ru.quipy.projections.TransferProjectionService
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/transfers")
class TransferController(
    private val transferEsService: EventSourcingService<UUID, TransferAggregate, TransferAggregateState>,
    private val transferProjectionService: TransferProjectionService,
    private val bankAccountCacheRepository: BankAccountCacheRepository,
) {

    @PostMapping
    fun initiateTransfer(
        @RequestParam sourceBankAccountId: UUID,
        @RequestParam destinationBankAccountId: UUID,
        @RequestParam amount: BigDecimal,
    ): TransferInitiatedEvent {
        val srcBankAccount = bankAccountCacheRepository.findByIdOrNull(sourceBankAccountId)
            ?: throw IllegalArgumentException("Cannot create transaction. There is no source bank account: $sourceBankAccountId")

        val dstBankAccount = bankAccountCacheRepository.findByIdOrNull(destinationBankAccountId)
            ?: throw IllegalArgumentException("Cannot create transaction. There is no destination bank account: $destinationBankAccountId")

        val sourceAccountId = srcBankAccount.accountId
        val destinationAccountId = dstBankAccount.accountId

        return transferEsService.create {
            it.initiateNewTransfer(
                sourceAccountId = sourceAccountId,
                sourceBankAccountId = sourceBankAccountId,
                destinationAccountId = destinationAccountId,
                destinationBankAccountId = destinationBankAccountId,
                amount = amount,
            )
        }
    }

    @GetMapping("/{transferId}")
    fun getTransfer(@PathVariable transferId: UUID): TransferProjection =
        transferProjectionService.findByTransferId(transferId)
            ?: throw IllegalArgumentException("Transfer $transferId is not found")

}