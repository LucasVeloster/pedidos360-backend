package com.pedidos360.pedidos.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${pedidos360.rabbitmq.exchange}")
    private String exchange;

    @Value("${pedidos360.rabbitmq.routing-key}")
    private String routingKey;

    public PedidoProducer(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {

        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void enviarPedidoCreado(Object pedido) {

        String mensajeJson = objectMapper.writeValueAsString(pedido);

        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                mensajeJson
        );

        System.out.println(
                "JSON enviado a RabbitMQ -> Exchange: "
                + exchange
                + " | Routing Key: "
                + routingKey
                + " | Mensaje: "
                + mensajeJson
        );
    }
}