package pl.jt.demo.rewardpoints.infra.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.jt.demo.rewardpoints.config.BaseIntegrationTest;
import pl.jt.demo.rewardpoints.infra.repositories.entities.TransactionEntity;

public class TransactionEntityRepositoryTest extends BaseIntegrationTest {

  @Test
  public void shouldSaveAndReadDataFromDB() {
    var userId = UUID.randomUUID().toString();
    TransactionEntity transactionEntity = new TransactionEntity();
    transactionEntity.setAmount(BigDecimal.valueOf(10.0));
    transactionEntity.setCreatedAt(LocalDateTime.now());
    transactionEntity.setUserId(userId);

    TransactionEntity transactionEntity2 = new TransactionEntity();
    transactionEntity2.setAmount(BigDecimal.valueOf(10.0));
    transactionEntity2.setCreatedAt(LocalDateTime.now());
    transactionEntity2.setUserId(userId);

    TransactionEntity transactionEntity3 = new TransactionEntity();
    transactionEntity3.setAmount(BigDecimal.valueOf(10.0));
    transactionEntity3.setCreatedAt(LocalDateTime.now());
    transactionEntity3.setUserId(UUID.randomUUID().toString());

    transactionRepository.saveAllAndFlush(List.of(transactionEntity, transactionEntity2, transactionEntity3));

    List<TransactionEntity> transactionEntitiesWithLaterDate =
        transactionRepository.findAllByUserIdAndCreatedAtAfterOrderByCreatedAt(userId, LocalDateTime.now().plus(1, ChronoUnit.DAYS));
    List<TransactionEntity> transactionEntitiesWithBeforeDate =
        transactionRepository.findAllByUserIdAndCreatedAtAfterOrderByCreatedAt(userId, LocalDateTime.now().minus(1, ChronoUnit.DAYS));

    List<TransactionEntity> all = transactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

    assertEquals(0, transactionEntitiesWithLaterDate.size());
    assertEquals(2, transactionEntitiesWithBeforeDate.size());
    assertTrue(transactionEntitiesWithBeforeDate.stream().map(TransactionEntity::getUserId).allMatch(id -> id.equals(userId)));
  }
}
