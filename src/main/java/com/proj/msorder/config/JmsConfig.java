package com.proj.msorder.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

//    public static final String BREWING_REQUEST_QUEUE = "brewing-request-queue";
//    public static final String INVENTORY_REQUEST_QUEUE = "inventory-request-queue";
//    public static final String INVENTORY_RESPONSE_QUEUE = "inventory-response-queue";
//    public static final String NEW_INVENTORY_QUEUE = "new-inventory-queue";
    public static final String VALIDATE_ORDER_QUEUE = "validate-order-queue";
    public static final String VALIDATE_ORDER_RESPONSE_QUEUE = "validate-order-response-queue";
    public static final String ALLOCATE_ORDER_QUEUE = "allocate-order-queue";
    public static final String ALLOCATE_ORDER_RESPONSE_QUEUE = "allocate-order-response-queue";
    public static final String DEALLOCATE_ORDER_QUEUE = "deallocate-order-queue";



    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper){

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTypeIdPropertyName("_type");
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
