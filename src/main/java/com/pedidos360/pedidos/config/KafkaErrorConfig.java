package com.pedidos360.pedidos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaErrorConfig.class);

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {

        return new DefaultErrorHandler(
            (registro, error) -> {
                log.error(
                    "Mensaje Kafka no procesado. Topic={}, particion={}, offset={}, mensaje={}",
                    registro.topic(),
                    registro.partition(),
                    registro.offset(),
                    registro.value(),
                    error
                );
            },
            new FixedBackOff(1000L, 2L)
        );
    }
}