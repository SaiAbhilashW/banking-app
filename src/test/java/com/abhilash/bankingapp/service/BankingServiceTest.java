package com.abhilash.bankingapp.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.abhilash.bankingapp.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import reactor.test.StepVerifier;

class BankingServiceTest {
    private final BankingService service = new BankingService();

    @Test
    @DisplayName("Compute statistics from transaction list")
    void should_compute_and_return_statistics(){
        var sum = BigDecimal.valueOf(1600);
        var min = BigDecimal.valueOf(100);
        var max = BigDecimal.valueOf(1000);
        var count = 3L;
        var avg = BigDecimal.valueOf(533);
        var statisticsMono = service.computeStatistics();
        StepVerifier.create(statisticsMono).expectNextMatches(statistics ->
            statistics.getMin().compareTo(min) == 0
                    && statistics.getSum().compareTo(sum) == 0
                    && statistics.getCount() == count && statistics.getMax().compareTo(max) == 0
                    && statistics.getAvg().compareTo(avg) == 0
        ).verifyComplete();
    }

    @Test
    @DisplayName("Create a transaction")
    void should_create_a_transaction(){
        var responseMono= service.makeTransactions(Transaction.builder().amount(BigDecimal.valueOf(10)).timestamp(OffsetDateTime.now()).build());
        StepVerifier.create(responseMono).expectNextMatches(response -> response.getMessage().equals("Transaction successful"));
    }

    @Test
    @DisplayName("Delete all transactions")
    void should_delete_all_transactions(){
        var responseMono= service.deleteAllTransactions();
        StepVerifier.create(responseMono).expectNextMatches(response -> response.getMessage().equals("Deleted all transactions"));
    }
}
