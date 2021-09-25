package com.abhilash.bankingapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.abhilash.bankingapp.model.Response;
import com.abhilash.bankingapp.model.Statistics;
import com.abhilash.bankingapp.model.Transaction;

import reactor.core.publisher.Mono;

public class BankingService {
    private List<Transaction> transactionList;

    public BankingService(){
        transactionList =  Stream.of(
                Transaction.builder().amount(BigDecimal.valueOf(1000)).timestamp(OffsetDateTime.now()).build(),
                Transaction.builder().amount(BigDecimal.valueOf(500)).timestamp(OffsetDateTime.now().minusSeconds(5)).build(),
                Transaction.builder().amount(BigDecimal.valueOf(100)).timestamp(OffsetDateTime.now().minusSeconds(10)).build(),
                Transaction.builder().amount(BigDecimal.valueOf(100000)).timestamp(OffsetDateTime.now().minusSeconds(120)).build(), //won't be considered as not in time range
                Transaction.builder().amount(BigDecimal.valueOf(100000)).timestamp(OffsetDateTime.now().plusSeconds(120)).build()  //won't be considered as not in time range
        ).collect(Collectors.toList());
    }

    public Mono<Statistics> computeStatistics(){
        var statistics = Statistics.builder()
                .sum(BigDecimal.ZERO)
                .count(0)
                .min(BigDecimal.ZERO)
                .max(BigDecimal.ZERO)
                .build();

         transactionList.stream()
                .filter(transaction -> transaction.getTimestamp().isAfter(OffsetDateTime.now().minusSeconds(60)) && transaction.getTimestamp().isBefore(OffsetDateTime.now()))
                .map(transaction -> {
                    var sum = statistics.getSum();
                    var amount  = transaction.getAmount();
                    statistics.setSum(sum.add(amount));
                    if(statistics.getMax().compareTo(amount) < 0) statistics.setMax(amount);
                    if(statistics.getMin().compareTo(BigDecimal.ZERO) == 0 || statistics.getMin().compareTo(amount) > 0) statistics.setMin(amount);
                    long count = statistics.getCount();
                    statistics.setCount(count+1);
                    return amount;
                }).collect(Collectors.toList());
        var sum = statistics.getSum();
        var count = statistics.getCount();
        statistics.setAvg(count == 0 ? BigDecimal.ZERO : sum.divide(new BigDecimal(count), RoundingMode.DOWN));
        return Mono.just(statistics);
    }

    public Mono<Response> makeTransactions(Transaction transaction) {
        transactionList.add(transaction);
        return Mono.just(Response.builder().message("Transaction successful").build());
    }

    public Mono<Response> deleteAllTransactions() {
        transactionList.clear();
        return Mono.just(Response.builder().message("Deleted all transactions").build());
    }

}
