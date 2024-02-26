package ru.quipy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.api.AccountAggregate
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TransferAggregate
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.logic.AccountAggregateState
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TransferAggregateState
import ru.quipy.projections.AnnotationBasedProjectEventsSubscriber
import ru.quipy.streams.AggregateEventStreamManager
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class EventSourcingLibConfiguration {

    private val logger = LoggerFactory.getLogger(EventSourcingLibConfiguration::class.java)

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var projectEventSubscriber: AnnotationBasedProjectEventsSubscriber

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, ProjectAggregate, ProjectAggregateState>()

    @Bean
    fun accountEsService() = eventSourcingServiceFactory.create<UUID, AccountAggregate, AccountAggregateState>()

    @Bean
    fun transferEsService() = eventSourcingServiceFactory.create<UUID, TransferAggregate, TransferAggregateState>()


    @PostConstruct
    fun init() {
        // Demonstrates how to explicitly subscribe the instance of annotation based subscriber to some stream. See the [AggregateSubscriptionsManager]
        subscriptionsManager.subscribe<ProjectAggregate>(projectEventSubscriber)

        // Demonstrates how you can set up the listeners to the event stream
        eventStreamManager.maintenance {
            onRecordHandledSuccessfully { streamName, eventName ->
                logger.info("Stream $streamName successfully processed record of $eventName")
            }

            onBatchRead { streamName, batchSize ->
                logger.info("Stream $streamName read batch size: $batchSize")
            }
        }
    }

}