package com.pedidos360.pedidos.controller;

import com.pedidos360.pedidos.entity.Pedido;
import com.pedidos360.pedidos.messaging.PedidoKafkaProducer;
import com.pedidos360.pedidos.messaging.PedidoProducer;
import com.pedidos360.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PedidoControllerTest {

    private MockMvc mockMvc;
    private PedidoRepository repository;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void configurar() {
        repository = mock(PedidoRepository.class);

        PedidoProducer rabbit = mock(PedidoProducer.class);
        PedidoKafkaProducer kafka = mock(PedidoKafkaProducer.class);

        PedidoController controller =
                new PedidoController(repository, rabbit, kafka);

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void cerrar() {
        validator.close();
    }

    @Test
    void crearPedidoValidoDevuelve201() throws Exception {
        when(repository.save(any(Pedido.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cliente": "Cliente Prueba",
                      "producto": "Notebook",
                      "cantidad": 2,
                      "estado": "CREADO"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cliente").value("Cliente Prueba"))
                .andExpect(jsonPath("$.cantidad").value(2));

        verify(repository).save(any(Pedido.class));
    }

    @Test
    void cantidadCeroDevuelve400() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cliente": "Cliente Prueba",
                      "producto": "Notebook",
                      "cantidad": 0,
                      "estado": "CREADO"
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(repository, never()).save(any(Pedido.class));
    }

    @Test
    void obtenerPedidosVaciosDevuelve200() throws Exception {
        when(repository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}