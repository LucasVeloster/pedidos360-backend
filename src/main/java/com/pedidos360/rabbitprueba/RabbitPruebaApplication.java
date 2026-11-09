package com.pedidos360.rabbitprueba;

import com.pedidos360.pedidos.config.RabbitMQConfig;
import com.pedidos360.pedidos.config.RabbitMQColasConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration(excludeName = {
    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
    "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
@Import({
    RabbitMQConfig.class,
    RabbitMQColasConfig.class
})
public class RabbitPruebaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitPruebaApplication.class, args);
    }

    @Bean
    public CommandLineRunner declararColas(
            ConnectionFactory connectionFactory,
            ApplicationContext context) {

        return args -> {

            RabbitAdmin admin = new RabbitAdmin(connectionFactory);

            for (Exchange exchange :
                    context.getBeansOfType(Exchange.class).values()) {
                admin.declareExchange(exchange);
                System.out.println("EXCHANGE: " + exchange.getName());
            }

            for (Queue queue :
                    context.getBeansOfType(Queue.class).values()) {
                admin.declareQueue(queue);
                System.out.println("COLA: " + queue.getName());
            }

            for (Binding binding :
                    context.getBeansOfType(Binding.class).values()) {
                admin.declareBinding(binding);
            }

            System.out.println("COLAS Y BINDINGS DECLARADOS CORRECTAMENTE");
        };
    }
}