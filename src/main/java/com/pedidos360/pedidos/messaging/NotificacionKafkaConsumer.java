package com.pedidos360.pedidos.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificacionKafkaConsumer {

    @KafkaListener(
        topics = "pedidos.notificaciones",
        groupId = "pedidos360-notificaciones-grupo"
    )
    public void recibirNotificacion(String mensaje) {

        System.out.println("================================");
        System.out.println("NOTIFICACION RECIBIDA DESDE KAFKA");
        System.out.println("Mensaje: " + mensaje);
        System.out.println("================================");

    }
}