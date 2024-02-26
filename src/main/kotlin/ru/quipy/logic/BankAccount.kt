package ru.quipy.logic

import java.math.BigDecimal
import java.util.*

data class BankAccount(
    val id: UUID,
    internal var balance: BigDecimal = BigDecimal.ZERO,

    ) {
    private var transferWithdraws: MutableMap<UUID, TransferWithdraw> = mutableMapOf()

    fun deposit(amount: BigDecimal) {
        this.balance = this.balance.add(amount)
    }

    fun withdraw(amount: BigDecimal) {
        this.balance = this.balance.subtract(amount)
    }

    fun addTransferWithdraw(transferId: UUID, amount: BigDecimal) {
        transferWithdraws[transferId] = TransferWithdraw(transferId, amount)
    }

    fun removeTransferWithdraw(transferId: UUID) {
        transferWithdraws.remove(transferId)
    }

    fun checkTransferWithdraw(transferId: UUID, amount: BigDecimal): Boolean {
        val withdraw = transferWithdraws[transferId]

        return withdraw != null && withdraw.amount == amount
    }

}