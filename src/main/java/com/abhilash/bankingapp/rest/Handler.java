package com.abhilash.bankingapp.rest;

import com.abhilash.bankingapp.model.Response;
import com.abhilash.bankingapp.model.Statistics;
import com.abhilash.bankingapp.model.Transaction;
import com.abhilash.bankingapp.service.BankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@RequiredArgsConstructor
public class Handler {
    private final BankingService service;
    public Mono<ServerResponse> getStatistics(ServerRequest request) {
        return ServerResponse.status(OK).body(fromPublisher(
                service.computeStatistics(), Statistics.class));
    }

    public Mono<ServerResponse> makeTransaction(ServerRequest serverRequest) {
        Mono<Transaction> saveRequestMono = serverRequest.bodyToMono(Transaction.class);
        return saveRequestMono.flatMap(transaction -> {
            if(transaction.getTimestamp().atZoneSameInstant(ZoneOffset.UTC).isAfter(OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC)))
                return ServerResponse.status(UNPROCESSABLE_ENTITY).body(fromValue(Response.builder().message("Transaction date is in future, please pass a valid transaction date").build()));
            if(transaction.getTimestamp().atZoneSameInstant(ZoneOffset.UTC).isBefore(OffsetDateTime.now().minusSeconds(60).atZoneSameInstant(ZoneOffset.UTC)))
                return ServerResponse.status(NO_CONTENT).body(fromValue(Response.builder().message("Transaction date is in older than accepted value, please pass a valid transaction date").build()));
            return ServerResponse.status(CREATED).body(fromPublisher(
                    service.makeTransactions(transaction), Response.class));
        });
    }

    public Mono<ServerResponse> deleteAllTransactions(ServerRequest serverRequest) {
        return ServerResponse.status(NO_CONTENT).body(fromPublisher(
                service.deleteAllTransactions(), Response.class));
    }
}
