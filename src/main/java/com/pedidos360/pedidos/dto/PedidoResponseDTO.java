package com.pedidos360.pedidos.dto;

import com.pedidos360.pedidos.entity.Pedido;

public record PedidoResponseDTO(
    Long id,
    String cliente,
    String producto,
    Integer cantidad,
    String estado
) {

    public static PedidoResponseDTO desdeEntidad(Pedido pedido) {
        return new PedidoResponseDTO(
            pedido.getId(),
            pedido.getCliente(),
            pedido.getProducto(),
            pedido.getCantidad(),
            pedido.getEstado()
        );
    }
}