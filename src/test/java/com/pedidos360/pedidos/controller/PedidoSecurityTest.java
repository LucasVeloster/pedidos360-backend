package com.pedidos360.pedidos.controller;

import com.pedidos360.pedidos.config.SecurityConfig;
import com.pedidos360.pedidos.entity.Pedido;
import com.pedidos360.pedidos.messaging.PedidoKafkaProducer;
import com.pedidos360.pedidos.messaging.PedidoProducer;
import com.pedidos360.pedidos.repository.PedidoRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@Import(SecurityConfig.class)
class PedidoSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoRepository repository;

    @MockitoBean
    private PedidoProducer rabbit;

    @MockitoBean
    private PedidoKafkaProducer kafka;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static final String PEDIDO = """
        {
          "cliente": "Cliente Seguridad",
          "producto": "Notebook",
          "cantidad": 2,
          "estado": "CREADO"
        }
        """;

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void soloLecturaNoPuedeCrearPedido403() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                .with(jwt().authorities(
                    new SimpleGrantedAuthority("SCOPE_read")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(PEDIDO))
            .andExpect(status().isForbidden());

        verify(repository, never()).save(any(Pedido.class));
    }

    @Test
    void conPermisoWritePuedeCrearPedido201() throws Exception {
        when(repository.save(any(Pedido.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

        mockMvc.perform(post("/api/pedidos")
                .with(jwt().authorities(
                    new SimpleGrantedAuthority("SCOPE_write")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(PEDIDO))
            .andExpect(status().isCreated());

        verify(repository).save(any(Pedido.class));
    }
}