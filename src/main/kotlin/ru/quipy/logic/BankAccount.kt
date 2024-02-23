package ru.quipy.logic

import java.math.BigDecimal
import java.util.*

data class BankAccount(
    val id: UUID,
    internal var balance: BigDecimal = BigDecimal.ZERO,
    // internal var pendingTransactions: MutableMap<UUID, PendingTransaction> = mutableMapOf()
)