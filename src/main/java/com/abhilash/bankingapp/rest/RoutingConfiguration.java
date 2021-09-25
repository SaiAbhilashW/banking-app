package com.abhilash.bankingapp.rest;

import com.abhilash.bankingapp.model.Response;
import com.abhilash.bankingapp.model.Statistics;
import com.abhilash.bankingapp.model.Transaction;
import org.springdoc.core.fn.builders.apiresponse.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RoutingConfiguration {
    @Bean
    public RouterFunction<ServerResponse> resourcePlanRoutes(Handler handler) {
        return route().
                GET("/v1/statistics", accept(APPLICATION_JSON), handler::getStatistics,
                        ops -> ops.operationId("Get Statistics Endpoint")
                                .response(Builder.responseBuilder().responseCode("200").implementation(Statistics.class).description("SUCCESS"))
                ).build()
                .and(route().
                POST("/v1/transactions", accept(APPLICATION_JSON), handler::makeTransaction,
                        ops -> ops.operationId("Resource Plan Store Manager View")
                                .requestBody(requestBodyBuilder().implementation(Transaction.class).description("Transaction request"))
                                .response(Builder.responseBuilder().responseCode("201").implementation(Response.class).description("SUCCESS"))
                                .response(Builder.responseBuilder().responseCode("204").implementation(Response.class).description("NO_CONTENT"))
                                .response(Builder.responseBuilder().responseCode("400").implementation(Response.class).description("BAD_REQUEST"))
                                .response(Builder.responseBuilder().responseCode("422").implementation(Response.class).description("UNPROCESSABLE_ENTITY"))
                ).build())
                .and(route().
                        DELETE("/v1/transactions", accept(APPLICATION_JSON), handler::deleteAllTransactions,
                                ops -> ops.operationId("Resource Plan Sign off")
                                        .response(Builder.responseBuilder().responseCode("204").implementation(Response.class).description("SUCCESS"))
                        ).build());
    }
}
