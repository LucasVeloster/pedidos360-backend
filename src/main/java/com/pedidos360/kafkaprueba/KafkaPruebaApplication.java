package com.pedidos360.kafkaprueba;

import com.pedidos360.pedidos.messaging.PedidoKafkaConsumer;
import com.pedidos360.pedidos.messaging.NotificacionKafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootConfiguration
@EnableAutoConfiguration(excludeName = {
    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
    "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
@EnableKafka
@Import({
    PedidoKafkaConsumer.class,
    NotificacionKafkaConsumer.class
})
public class KafkaPruebaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaPruebaApplication.class, args);
    }
}