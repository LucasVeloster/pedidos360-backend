package com.pedidos360.kafkaprueba;

import com.pedidos360.pedidos.entity.Pedido;
import com.pedidos360.pedidos.messaging.PedidoKafkaConsumer;
import com.pedidos360.pedidos.messaging.NotificacionKafkaConsumer;
import com.pedidos360.pedidos.messaging.PedidoKafkaProducer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootConfiguration
@EnableAutoConfiguration(excludeName = {
    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
    "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
@EnableKafka
@Import({
    PedidoKafkaConsumer.class,
    NotificacionKafkaConsumer.class,
    PedidoKafkaProducer.class
})
public class KafkaPruebaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaPruebaApplication.class, args);
    }

    @Bean
    @Profile("kafka-producer-test")
    public CommandLineRunner probarProductor(PedidoKafkaProducer productor) {
        return args -> {
            Pedido pedido = new Pedido(
                999L,
                "Prueba Java DUOC",
                "Notebook",
                2,
                "CREADO"
            );

            productor.enviarPedidoCreado(pedido);
            productor.enviarNotificacion(pedido);

            System.out.println("PRUEBA DEL PRODUCTOR JAVA INICIADA");
        };
    }
}