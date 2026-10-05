package com.pedidos360.pedidos.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${pedidos360.rabbitmq.exchange}")
    private String exchange;

    @Value("${pedidos360.rabbitmq.routing-key}")
    private String routingKey;

    public PedidoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarPedidoCreado(Object pedido) {
        rabbitTemplate.convertAndSend(exchange, routingKey, pedido);

        System.out.println(
                "Mensaje enviado a RabbitMQ -> Exchange: "
                + exchange
                + " | Routing Key: "
                + routingKey
        );
    }
}

