package com.zuul.zuulservice;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class ResponseFilter {
    @Bean
    public GlobalFilter postGlobalFilter(){
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                HttpHeaders headers = exchange.getRequest().getHeaders();
                String correlationId=getCorrelationId(headers);
                ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().
                        header("tmx-correlation-id", correlationId).build();
                 exchange.mutate().request(serverHttpRequest).build();
                System.out.println("Addding the correlation-id into outbound headers "+correlationId);
                //exchange.getRequest().getHeaders().add("tmx-correlation-id",correlationId);
                System.out.println("Completing the outBound Request "+exchange.getRequest().getURI());
            }));
        };
    }

    private String getCorrelationId(HttpHeaders httpHeaders) {
        List<String> stringList = httpHeaders.get("tmx-correlation-id");
        return stringList.stream().findFirst().get();
    }
}
