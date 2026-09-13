package com.pedidos360.pedidos.controller;

import java.util.List;

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
import com.pedidos360.pedidos.repository.PedidoRepository;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Pedido obtenerPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Pedido crear(@RequestBody Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        Pedido existente = pedidoRepository.findById(id).orElse(null);

        if (existente == null) {
            return null;
        }

        existente.setCliente(pedido.getCliente());
        existente.setProducto(pedido.getProducto());
        existente.setCantidad(pedido.getCantidad());
        existente.setEstado(pedido.getEstado());

        return pedidoRepository.save(existente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
    }
}