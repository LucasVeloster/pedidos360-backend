package com.pedidos360.pedidos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pedidos360.pedidos.dto.PedidoRequestDTO;
import com.pedidos360.pedidos.dto.PedidoResponseDTO;
import com.pedidos360.pedidos.entity.Pedido;
import com.pedidos360.pedidos.messaging.PedidoProducer;
import com.pedidos360.pedidos.messaging.PedidoKafkaProducer;
import com.pedidos360.pedidos.repository.PedidoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;
    private final PedidoKafkaProducer pedidoKafkaProducer;

    public PedidoController(
            PedidoRepository pedidoRepository,
            PedidoProducer pedidoProducer,
            PedidoKafkaProducer pedidoKafkaProducer) {

        this.pedidoRepository = pedidoRepository;
        this.pedidoProducer = pedidoProducer;
        this.pedidoKafkaProducer = pedidoKafkaProducer;
    }

    // GET - listar pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> obtenerTodos() {

        List<PedidoResponseDTO> pedidos = pedidoRepository.findAll()
                .stream()
                .map(PedidoResponseDTO::desdeEntidad)
                .toList();

        return ResponseEntity.ok(pedidos);
    }

    // GET - buscar pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(
            @PathVariable Long id) {

        return pedidoRepository.findById(id)
                .map(PedidoResponseDTO::desdeEntidad)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - crear pedido
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(
            @Valid @RequestBody PedidoRequestDTO datos) {

        Pedido pedido = new Pedido(
                datos.cliente(),
                datos.producto(),
                datos.cantidad(),
                datos.estado()
        );

        Pedido nuevoPedido = pedidoRepository.save(pedido);

        // Mantener la integración con RabbitMQ y Kafka
        pedidoProducer.enviarPedidoCreado(nuevoPedido);
        pedidoKafkaProducer.enviarPedidoCreado(nuevoPedido);
        pedidoKafkaProducer.enviarNotificacion(nuevoPedido);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PedidoResponseDTO.desdeEntidad(nuevoPedido));
    }

    // PUT - actualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequestDTO datos) {

        Optional<Pedido> pedidoExistente = pedidoRepository.findById(id);

        if (pedidoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = pedidoExistente.get();

        pedido.setCliente(datos.cliente());
        pedido.setProducto(datos.producto());
        pedido.setCantidad(datos.cantidad());
        pedido.setEstado(datos.estado());

        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        return ResponseEntity.ok(
                PedidoResponseDTO.desdeEntidad(pedidoActualizado)
        );
    }

    // DELETE - eliminar pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(
            @PathVariable Long id) {

        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        pedidoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}