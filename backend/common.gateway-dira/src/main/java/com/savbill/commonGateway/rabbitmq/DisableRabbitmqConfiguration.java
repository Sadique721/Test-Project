//package com.savbill.commonGateway.rabbitmq;
//
////import org.springframework.amqp.AmqpException;
////import org.springframework.amqp.core.*;
////import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
////import org.springframework.amqp.rabbit.connection.Connection;
////import org.springframework.amqp.rabbit.connection.ConnectionFactory;
////import org.springframework.amqp.rabbit.connection.ConnectionListener;
////import org.springframework.amqp.support.converter.MessageConversionException;
////import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.util.backoff.BackOff;
//import org.springframework.util.backoff.FixedBackOff;
//
//@Configuration
//@Profile("!rabbitmq")
//public class DisableRabbitmqConfiguration {
//    @Bean
//    Queue deadLetterQueue() {
//        return null;
//    }
//
//    @Bean
//    DirectExchange deadLetterExchange() {
//        return null;
//    }
//
//    @Bean
//    Binding DLQbinding() {
//        return null;
//    }
//
//    @Bean
//    public DirectExchange savbillExchange() {
//        return  null;
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new MessageConverter() {
//            @Override
//            public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
//                return null;
//            }
//
//            @Override
//            public Object fromMessage(Message message) throws MessageConversionException {
//                return null;
//            }
//        };
//    }
//
//    @Bean
//    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//        return new AmqpTemplate() {
//            @Override
//            public void send(Message message) throws AmqpException {
//
//            }
//
//            @Override
//            public void send(String routingKey, Message message) throws AmqpException {
//
//            }
//
//            @Override
//            public void send(String exchange, String routingKey, Message message) throws AmqpException {
//
//            }
//
//            @Override
//            public void convertAndSend(Object message) throws AmqpException {
//
//            }
//
//            @Override
//            public void convertAndSend(String routingKey, Object message) throws AmqpException {
//
//            }
//
//            @Override
//            public void convertAndSend(String exchange, String routingKey, Object message) throws AmqpException {
//
//            }
//
//            @Override
//            public void convertAndSend(Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
//
//            }
//
//            @Override
//            public void convertAndSend(String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
//
//            }
//
//            @Override
//            public void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
//
//            }
//
//            @Override
//            public Message receive() throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Message receive(String queueName) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Message receive(long timeoutMillis) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Message receive(String queueName, long timeoutMillis) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object receiveAndConvert() throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object receiveAndConvert(String queueName) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object receiveAndConvert(long timeoutMillis) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object receiveAndConvert(String queueName, long timeoutMillis) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T receiveAndConvert(ParameterizedTypeReference<T> type) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T receiveAndConvert(String queueName, ParameterizedTypeReference<T> type) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T receiveAndConvert(long timeoutMillis, ParameterizedTypeReference<T> type) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T receiveAndConvert(String queueName, long timeoutMillis, ParameterizedTypeReference<T> type) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback) throws AmqpException {
//                return false;
//            }
//
//            @Override
//            public <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback) throws AmqpException {
//                return false;
//            }
//
//            @Override
//            public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback, String replyExchange, String replyRoutingKey) throws AmqpException {
//                return false;
//            }
//
//            @Override
//            public <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback, String replyExchange, String replyRoutingKey) throws AmqpException {
//                return false;
//            }
//
//            @Override
//            public <R, S> boolean receiveAndReply(ReceiveAndReplyCallback<R, S> callback, ReplyToAddressCallback<S> replyToAddressCallback) throws AmqpException {
//                return false;
//            }
//
//            @Override
//            public <R, S> boolean receiveAndReply(String queueName, ReceiveAndReplyCallback<R, S> callback, ReplyToAddressCallback<S> replyToAddressCallback) throws AmqpException {
//                return false;
//            }
//
//            @Override
//            public Message sendAndReceive(Message message) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Message sendAndReceive(String routingKey, Message message) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Message sendAndReceive(String exchange, String routingKey, Message message) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object convertSendAndReceive(Object message) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object convertSendAndReceive(String routingKey, Object message) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object convertSendAndReceive(String exchange, String routingKey, Object message) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object convertSendAndReceive(Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object convertSendAndReceive(String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public Object convertSendAndReceive(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T convertSendAndReceiveAsType(Object message, ParameterizedTypeReference<T> responseType) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T convertSendAndReceiveAsType(String routingKey, Object message, ParameterizedTypeReference<T> responseType) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T convertSendAndReceiveAsType(String exchange, String routingKey, Object message, ParameterizedTypeReference<T> responseType) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T convertSendAndReceiveAsType(Object message, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> responseType) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T convertSendAndReceiveAsType(String routingKey, Object message, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> responseType) throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public <T> T convertSendAndReceiveAsType(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor, ParameterizedTypeReference<T> responseType) throws AmqpException {
//                return null;
//            }
//        };
//    }
//
//    @Bean
//    public ConnectionFactory connectionFactory(){
//        return  new ConnectionFactory() {
//            @Override
//            public Connection createConnection() throws AmqpException {
//                return null;
//            }
//
//            @Override
//            public String getHost() {
//                return null;
//            }
//
//            @Override
//            public int getPort() {
//                return 0;
//            }
//
//            @Override
//            public String getVirtualHost() {
//                return null;
//            }
//
//            @Override
//            public String getUsername() {
//                return null;
//            }
//
//            @Override
//            public void addConnectionListener(ConnectionListener listener) {
//
//            }
//
//            @Override
//            public boolean removeConnectionListener(ConnectionListener listener) {
//                return true;
//            }
//
//            @Override
//            public void clearConnectionListeners() {
//
//            }
//        };
//    }
//
//    @Bean(name = "rabbitListenerContainerFactory")
//    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
//            SimpleRabbitListenerContainerFactoryConfigurer configurer,
//            ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        configurer.configure(factory, connectionFactory);
//        BackOff recoveryBackOff = new FixedBackOff(5000, 1);
//        factory.setRecoveryBackOff(recoveryBackOff);
//        return factory;
//    }
//}
