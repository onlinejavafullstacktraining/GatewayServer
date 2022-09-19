package com.zuul.zuulservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {
    Logger logger= LoggerFactory.getLogger(TrackingFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        if(isCorrelationIdPresent(httpHeaders))
            logger.info("tmx-correlation-id found in TrackingFilter: {}",getCorrelationId(httpHeaders));
        else{
            String correlationId=generateCorrelationId();
            exchange =setCorrelationId(exchange, correlationId);
            logger.info("tmx-correlation-id generated in TrackingFilter: {}",correlationId);
        }
        return chain.filter(exchange);
    }

    private ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().
                header("tmx-correlation-id", correlationId).build();
        return exchange.mutate().request(serverHttpRequest).build();
    }

    private String generateCorrelationId() {
        return  UUID.randomUUID().toString();
    }

    private String getCorrelationId(HttpHeaders httpHeaders) {
        List<String> stringList = httpHeaders.get("tmx-correlation-id");
        return stringList.stream().findFirst().get();
    }

    private boolean isCorrelationIdPresent(HttpHeaders httpHeaders) {
        if(Objects.nonNull(httpHeaders.get("tmx-correlation-id"))){
            List<String> stringList = httpHeaders.get("tmx-correlation-id");
            return stringList.stream().findFirst().isPresent();
        }
        return false;
    }
}
