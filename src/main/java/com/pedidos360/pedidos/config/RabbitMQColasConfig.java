package com.pedidos360.pedidos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQColasConfig {

    @Bean
    public Queue procesamientoQueue() {
        return QueueBuilder.durable("pedidos.procesamiento.queue")
                .quorum()
                .deadLetterExchange("pedidos.dlx")
                .deadLetterRoutingKey("pedido.procesamiento.fallido")
                .build();
    }

    @Bean
    public Queue procesamientoDLQ() {
        return QueueBuilder.durable("pedidos.procesamiento.dlq")
                .quorum()
                .build();
    }

    @Bean
    public Queue auditoriaQueue() {
        return QueueBuilder.durable("pedidos.auditoria.queue")
                .quorum()
                .deadLetterExchange("pedidos.dlx")
                .deadLetterRoutingKey("pedido.auditoria.fallido")
                .build();
    }

    @Bean
    public Queue auditoriaDLQ() {
        return QueueBuilder.durable("pedidos.auditoria.dlq")
                .quorum()
                .build();
    }

    @Bean
    public Binding procesamientoBinding(
            DirectExchange pedidosExchange) {
        return BindingBuilder.bind(procesamientoQueue())
                .to(pedidosExchange)
                .with("pedido.creado");
    }

    @Bean
    public Binding auditoriaBinding(
            DirectExchange pedidosExchange) {
        return BindingBuilder.bind(auditoriaQueue())
                .to(pedidosExchange)
                .with("pedido.creado");
    }

    @Bean
    public Binding procesamientoDLQBinding(
            DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(procesamientoDLQ())
                .to(deadLetterExchange)
                .with("pedido.procesamiento.fallido");
    }

    @Bean
    public Binding auditoriaDLQBinding(
            DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(auditoriaDLQ())
                .to(deadLetterExchange)
                .with("pedido.auditoria.fallido");
    }
}