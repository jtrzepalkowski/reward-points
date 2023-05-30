package pl.jt.demo.rewardpoints.infra.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import pl.jt.demo.rewardpoints.config.BaseIntegrationTest;
import pl.jt.demo.rewardpoints.infra.repositories.entities.TransactionEntity;
import pl.jt.demo.rewardpoints.infra.utils.TimeProvider;

public class RewardPointsServiceTest extends BaseIntegrationTest {

  @Autowired
  private RewardPointsService rewardPointsService;

  private static final String user1Id = UUID.randomUUID().toString();
  private static final String user2Id = UUID.randomUUID().toString();

  @AfterEach
  private void cleanUp() {
    transactionRepository.deleteAll();
  }

  @ParameterizedTest
  @MethodSource("provideCalculationTestData")
  public void shouldProperlyCalculatePoints(List<TransactionEntity> transactions, String userId, Integer expectedPointAmount, Long fixedTime) {
    timeProvider.fixed(Instant.ofEpochSecond(fixedTime));
    transactionRepository.saveAll(transactions);

    assertEquals(expectedPointAmount, rewardPointsService.calculateRewardPointsForUser(userId).sumOfAllPoints());
  }

  private static Stream<Arguments> provideCalculationTestData() {
    return Stream.of(
      Arguments.of(
          List.of(
              createTransaction(150.20, "2021-11-04 00:00:00", user1Id),
              createTransaction(100.00, "2021-11-04 00:08:00", user1Id),
              createTransaction(14.00, "2021-10-23 00:08:00", user1Id),
              createTransaction(80.00, "2021-12-08 00:08:00", user1Id)),
          user1Id, 230, TimeProvider.createInstantFromString("2021-12-20 00:02:03").toEpochSecond(ZoneOffset.UTC)),
        Arguments.of(
            List.of(
                createTransaction(150.20, "2021-11-04 00:00:00", user1Id),
                createTransaction(100.00, "2021-11-04 00:08:00", user1Id),
                createTransaction(14.00, "2021-10-23 00:08:00", user1Id),
                createTransaction(80.00, "2021-12-08 00:08:00", user1Id)),
            user1Id, 30, TimeProvider.createInstantFromString("2022-02-20 00:02:03").toEpochSecond(ZoneOffset.UTC)),
        Arguments.of(
            List.of(
                createTransaction(150.20, "2021-11-04 00:00:00", user2Id),
                createTransaction(100.00, "2021-11-04 00:08:00", user1Id),
                createTransaction(14.00, "2021-10-23 00:08:00", user1Id),
                createTransaction(80.00, "2021-12-08 00:08:00", user1Id)),
            user1Id, 80, TimeProvider.createInstantFromString("2021-12-20 00:02:03").toEpochSecond(ZoneOffset.UTC)),
        Arguments.of(
            List.of(
                createTransaction(150.20, "2021-11-04 00:00:00", user1Id),
                createTransaction(100.00, "2021-11-04 00:08:00", user1Id),
                createTransaction(14.00, "2021-10-23 00:08:00", user1Id),
                createTransaction(80.00, "2021-12-08 00:08:00", user1Id)),
            user1Id, 0, TimeProvider.createInstantFromString("2022-05-20 00:02:03").toEpochSecond(ZoneOffset.UTC)),
        Arguments.of(List.of(), user1Id, 0, TimeProvider.createInstantFromString("2021-12-20 00:02:03").toEpochSecond(ZoneOffset.UTC))
        );
  }

  private static TransactionEntity createTransaction(Double amount, String date, String userId) {
    return TransactionEntity.builder()
        .amount(BigDecimal.valueOf(amount))
        .createdAt(TimeProvider.createInstantFromString(date))
        .userId(userId)
        .build();
  }
}
