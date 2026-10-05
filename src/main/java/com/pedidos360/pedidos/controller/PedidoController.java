package com.pedidos360.pedidos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedidos360.pedidos.entity.Pedido;
import com.pedidos360.pedidos.messaging.PedidoProducer;
import com.pedidos360.pedidos.repository.PedidoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;

    public PedidoController(PedidoRepository pedidoRepository, PedidoProducer pedidoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoProducer = pedidoProducer;
    }

    // GET - listar todos los pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodos() {

        List<Pedido> pedidos = pedidoRepository.findAll();

        return ResponseEntity.ok(pedidos);
    }

    // GET - buscar pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(
            @PathVariable Long id) {

        Optional<Pedido> pedido =
                pedidoRepository.findById(id);

        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido.get());
        }

        return ResponseEntity.notFound().build();
    }

    // POST - crear pedido
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @Valid @RequestBody Pedido pedido) {

        Pedido nuevoPedido =
                pedidoRepository.save(pedido);
        pedidoProducer.enviarPedidoCreado(nuevoPedido);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoPedido);
    }

    // PUT - actualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizarPedido(
            @PathVariable Long id,
            @Valid @RequestBody Pedido datosPedido) {

        Optional<Pedido> pedidoExistente =
                pedidoRepository.findById(id);

        if (pedidoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = pedidoExistente.get();

        pedido.setCliente(datosPedido.getCliente());
        pedido.setProducto(datosPedido.getProducto());
        pedido.setCantidad(datosPedido.getCantidad());
        pedido.setEstado(datosPedido.getEstado());

        Pedido pedidoActualizado =
                pedidoRepository.save(pedido);

        return ResponseEntity.ok(pedidoActualizado);
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