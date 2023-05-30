package pl.jt.demo.rewardpoints.infra.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import pl.jt.demo.rewardpoints.domain.Transaction;
import pl.jt.demo.rewardpoints.infra.exceptions.NotFoundException;
import pl.jt.demo.rewardpoints.infra.repositories.TransactionRepository;
import pl.jt.demo.rewardpoints.infra.repositories.entities.TransactionEntity;
import pl.jt.demo.rewardpoints.infra.utils.TimeProvider;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;

  TransactionService(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public void create(String transactionTime, Double transactionAmount, String userId) {
    LocalDateTime dateTime = LocalDateTime.from(TimeProvider.getTimeFormatter().parse(transactionTime));
    validateAmount(transactionAmount);

    TransactionEntity transaction = TransactionEntity.builder()
        .userId(userId)
        .amount(BigDecimal.valueOf(transactionAmount))
        .createdAt(dateTime)
        .build();
    transactionRepository.save(transaction);
  }

  public void update(Long transactionId, String transactionTime, Double transactionAmount, String userId) throws NotFoundException {
    TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new NotFoundException("Cannot update - transaction " + transactionId + " not found"));

    LocalDateTime dateTime = LocalDateTime.from(TimeProvider.getTimeFormatter().parse(transactionTime));
    validateAmount(transactionAmount);

    transactionEntity.setAmount(BigDecimal.valueOf(transactionAmount));
    transactionEntity.setUserId(userId);
    transactionEntity.setCreatedAt(dateTime);
    transactionRepository.save(transactionEntity);
  }

  public void remove(Long transactionId) throws NotFoundException {
    TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new NotFoundException("Cannot remove - transaction " + transactionId + " not found"));
    transactionRepository.delete(transactionEntity);
  }

  public List<Transaction> retrieveTransactionsForUser(String userId) {
    List<TransactionEntity> transactions = transactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    return transactions.stream().map(Transaction::fromEntity).toList();
  }

  private static void validateAmount(Double transactionAmount) {
    if (transactionAmount <= 0) {
      throw new IllegalArgumentException("Error: Amount cannot be negative");
    }
  }
}
