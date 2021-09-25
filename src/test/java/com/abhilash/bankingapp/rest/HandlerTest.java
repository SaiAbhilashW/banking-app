package com.abhilash.bankingapp.rest;

import com.abhilash.bankingapp.model.Response;
import com.abhilash.bankingapp.model.Statistics;
import com.abhilash.bankingapp.model.Transaction;
import com.abhilash.bankingapp.service.BankingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

class HandlerTest {
    private final BankingService service = mock(BankingService.class);

    private final Handler handler = new Handler(service);

    @Test
    @DisplayName("Compute and return statistics")
    void should_compute_and_return_statistics(){
        ServerRequest request = Mockito.mock(ServerRequest.class);
        var statistics = Statistics.builder()
                .sum(BigDecimal.valueOf(1600))
                .min(BigDecimal.valueOf(100))
                .max(BigDecimal.valueOf(1000))
                .count(3L)
                .avg(BigDecimal.valueOf(533))
                .build();
        when(service.computeStatistics()).thenReturn(Mono.just(statistics));
        var handlerResponse  = handler.getStatistics(request);
        StepVerifier.create(handlerResponse).expectNextMatches(
                serverResponse -> serverResponse.statusCode() == OK)
                .verifyComplete();
        verify(service).computeStatistics();
    }

    @Test
    @DisplayName("Delete all transactions")
    void should_delete_all_transactions(){
        ServerRequest request = Mockito.mock(ServerRequest.class);
        when(service.deleteAllTransactions()).thenReturn(Mono.just(Response.builder().message("Deleted all transactions").build()));
        var handlerResponse  = handler.deleteAllTransactions(request);
        StepVerifier.create(handlerResponse).expectNextMatches(
                        serverResponse -> serverResponse.statusCode() == NO_CONTENT)
                .verifyComplete();
        verify(service).deleteAllTransactions();
    }

    @Test
    @DisplayName("Create a transaction when request is ok")
    void should_make_a_transaction(){
        var transactionRequest = Transaction.builder().amount(BigDecimal.valueOf(1000)).timestamp(OffsetDateTime.now().minusSeconds(5)).build();
        ServerRequest request = MockServerRequest.builder().body(Mono.just(transactionRequest));
        when(service.makeTransactions(transactionRequest)).thenReturn(Mono.just(Response.builder().message("Transaction successful").build()));
        var handlerResponse  = handler.makeTransaction(request);
        StepVerifier.create(handlerResponse).expectNextMatches(
                        serverResponse -> serverResponse.statusCode() == CREATED)
                .verifyComplete();
        verify(service).makeTransactions(transactionRequest);
    }

    @Test
    @DisplayName("Return with an error when transaction date in future")
    void should_return_422_error_when_transaction_date_is_in_future(){
        var transactionRequest = Transaction.builder().amount(BigDecimal.valueOf(1000)).timestamp(OffsetDateTime.now().plusSeconds(10)).build();
        ServerRequest request = MockServerRequest.builder().body(Mono.just(transactionRequest));
        var handlerResponse  = handler.makeTransaction(request);
        StepVerifier.create(handlerResponse).expectNextMatches(
                        serverResponse -> serverResponse.statusCode() == UNPROCESSABLE_ENTITY)
                .verifyComplete();
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Return with an error when transaction date is more than 60secs in past")
    void should_return_204_error_when_transaction_date_is_in_future(){
        var transactionRequest = Transaction.builder().amount(BigDecimal.valueOf(1000)).timestamp(OffsetDateTime.now().minusSeconds(120)).build();
        ServerRequest request = MockServerRequest.builder().body(Mono.just(transactionRequest));
        var handlerResponse  = handler.makeTransaction(request);
        StepVerifier.create(handlerResponse).expectNextMatches(
                        serverResponse -> serverResponse.statusCode() == NO_CONTENT)
                .verifyComplete();
        verifyNoInteractions(service);
    }

}
