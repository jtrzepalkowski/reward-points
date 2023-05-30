package pl.jt.demo.rewardpoints.domain;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import pl.jt.demo.rewardpoints.infra.repositories.entities.TransactionEntity;

public record Transaction (long id,
                           ZonedDateTime createdAt,
                           BigDecimal amount) {

  public static Transaction fromEntity(TransactionEntity transactionEntity) {
    return new Transaction(transactionEntity.getId(), ZonedDateTime.of(transactionEntity.getCreatedAt(),
        ZoneId.systemDefault()), transactionEntity.getAmount());
  }
}
