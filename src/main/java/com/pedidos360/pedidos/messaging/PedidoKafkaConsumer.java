package com.pedidos360.pedidos.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PedidoKafkaConsumer {

    @KafkaListener(
        topics = "pedidos.eventos",
        groupId = "pedidos360-grupo"
    )
    public void recibirPedido(String mensaje) {

        System.out.println("================================");
        System.out.println("EVENTO RECIBIDO DESDE KAFKA");
        System.out.println("Mensaje: " + mensaje);
        System.out.println("================================");

    }
}