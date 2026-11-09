package com.pedidos360.pedidos.messaging;

import com.pedidos360.pedidos.entity.Pedido;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PedidoKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PedidoKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void enviarPedidoCreado(Pedido pedido) {

        Map<String, Object> evento = new LinkedHashMap<>();

        evento.put("tipoEvento", "PEDIDO_CREADO");
        evento.put("pedidoId", pedido.getId());
        evento.put("cliente", pedido.getCliente());
        evento.put("producto", pedido.getProducto());
        evento.put("cantidad", pedido.getCantidad());
        evento.put("estado", pedido.getEstado());

        enviar("pedidos.eventos", pedido.getId(), evento);
    }

    public void enviarNotificacion(Pedido pedido) {

        Map<String, Object> notificacion = new LinkedHashMap<>();

        notificacion.put("tipoEvento", "NOTIFICACION_PEDIDO");
        notificacion.put("pedidoId", pedido.getId());
        notificacion.put("cliente", pedido.getCliente());
        notificacion.put("mensaje", "Tu pedido fue creado correctamente");
        notificacion.put("estado", pedido.getEstado());

        enviar("pedidos.notificaciones", pedido.getId(), notificacion);
    }

    private void enviar(
            String topico,
            Long pedidoId,
            Map<String, Object> datos) {

        String json = objectMapper.writeValueAsString(datos);

        kafkaTemplate.send(
            topico,
            String.valueOf(pedidoId),
            json
        ).whenComplete((resultado, error) -> {
            if (error != null) {
                System.err.println(
                    "Error al enviar evento a " + topico + ": " + error.getMessage()
                );
            } else {
                System.out.println(
                    "Evento confirmado por Kafka en " + topico + ": " + json
                );
            }
        });
    }
}