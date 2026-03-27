package com.diminik.data.messaging

import com.diminik.core.config.RabbitMqSettings
import com.diminik.domain.model.OrderEvent
import com.diminik.domain.ports.EmailSender
import com.diminik.domain.ports.OrderEventPublisher
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Delivery
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class RabbitMqClient(
    settings: RabbitMqSettings,
) : AutoCloseable {
    private val connectionFactory = ConnectionFactory().apply {
        host = settings.host
        port = settings.port
        username = settings.username
        password = settings.password
    }

    private val connection: Connection = connectionFactory.newConnection("kotlinithub")
    private val publisherChannel: Channel = connection.createChannel().apply {
        queueDeclare(settings.queueName, true, false, false, null)
    }

    private val queueName: String = settings.queueName

    fun publish(message: String) {
        publisherChannel.basicPublish(
            "",
            queueName,
            null,
            message.toByteArray(StandardCharsets.UTF_8),
        )
    }

    fun startConsumer(onMessage: (String) -> Unit): Channel {
        val consumerChannel = connection.createChannel().apply {
            queueDeclare(queueName, true, false, false, null)
            basicQos(1)
        }

        consumerChannel.basicConsume(
            queueName,
            false,
            { _, delivery: Delivery ->
                val payload = String(delivery.body, StandardCharsets.UTF_8)
                onMessage(payload)
                consumerChannel.basicAck(delivery.envelope.deliveryTag, false)
            },
            { _ -> },
        )

        return consumerChannel
    }

    override fun close() {
        if (publisherChannel.isOpen) {
            publisherChannel.close()
        }
        if (connection.isOpen) {
            connection.close()
        }
    }
}

class RabbitMqOrderEventPublisher(
    private val rabbitMqClient: RabbitMqClient,
    private val json: Json,
) : OrderEventPublisher {
    override suspend fun publish(event: OrderEvent) {
        rabbitMqClient.publish(json.encodeToString(event))
    }
}

class RabbitMqOrderEventWorker(
    private val rabbitMqClient: RabbitMqClient,
    private val emailSender: EmailSender,
    private val json: Json,
) : AutoCloseable {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var consumerChannel: Channel? = null

    fun start() {
        if (consumerChannel != null) {
            return
        }

        consumerChannel = rabbitMqClient.startConsumer { rawMessage ->
            val event = json.decodeFromString<OrderEvent>(rawMessage)
            logger.info("Worker received event {} for order {}", event.type, event.orderId)
            runBlocking {
                emailSender.sendOrderNotification(event)
            }
        }
    }

    override fun close() {
        consumerChannel?.takeIf { it.isOpen }?.close()
    }
}

class LoggingEmailSender : EmailSender {
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun sendOrderNotification(event: OrderEvent) {
        logger.info("Fake email sent for order {} with event {}", event.orderId, event.type)
    }
}
