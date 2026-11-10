package com.pedidos360.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PedidoRequestDTO(

    @NotBlank(message = "El cliente es obligatorio")
    String cliente,

    @NotBlank(message = "El producto es obligatorio")
    String producto,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    Integer cantidad,

    @NotBlank(message = "El estado es obligatorio")
    String estado

) {}