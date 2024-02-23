package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.AccountAggregate
import ru.quipy.api.AccountCreatedEvent
import ru.quipy.logic.AccountAggregateState
import ru.quipy.core.EventSourcingService

import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    val accountEsService: EventSourcingService<UUID, AccountAggregate, AccountAggregateState>
) {

    @GetMapping("/{holderId}")
    fun getAccount(@PathVariable holderId: UUID) : AccountAggregateState? {
        return accountEsService.getState(holderId)
    }

    @PostMapping("/{holderId}")
    fun createAccount(@PathVariable holderId: UUID) : AccountCreatedEvent {
        return accountEsService.create { it.createNewAccount(holderId = holderId) }
    }
}