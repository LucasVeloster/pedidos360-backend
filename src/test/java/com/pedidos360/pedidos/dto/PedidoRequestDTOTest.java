package com.pedidos360.pedidos.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoRequestDTOTest {

    @Test
    void cantidadCeroDebeSerInvalida() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            PedidoRequestDTO pedido = new PedidoRequestDTO(
                "Cliente Prueba", "Notebook", 0, "CREADO"
            );

            assertFalse(validator.validate(pedido).isEmpty());
        }
    }

    @Test
    void pedidoValidoNoDebeTenerErrores() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            PedidoRequestDTO pedido = new PedidoRequestDTO(
                "Cliente Prueba", "Notebook", 2, "CREADO"
            );

            assertTrue(validator.validate(pedido).isEmpty());
        }
    }

    @Test
    void clienteVacioDebeSerInvalido() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            PedidoRequestDTO pedido = new PedidoRequestDTO(
                "", "Notebook", 2, "CREADO"
            );

            assertFalse(validator.validate(pedido).isEmpty());
        }
    }
}