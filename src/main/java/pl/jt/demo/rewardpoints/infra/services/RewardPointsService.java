package pl.jt.demo.rewardpoints.infra.services;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.jt.demo.rewardpoints.domain.RewardPoints;
import pl.jt.demo.rewardpoints.domain.Transaction;
import pl.jt.demo.rewardpoints.infra.configuration.RewardConfiguration;
import pl.jt.demo.rewardpoints.infra.repositories.TransactionRepository;
import pl.jt.demo.rewardpoints.infra.utils.TimeProvider;

@Service
@Slf4j
public class RewardPointsService {

  private final TimeProvider timeProvider;
  private final RewardConfiguration rewardConfiguration;
  private final TransactionRepository transactionRepository;
  private final SortedMap<Integer, Integer> rewardThresholdsDescending;

  public RewardPointsService(RewardConfiguration rewardConfiguration, TransactionRepository transactionRepository, TimeProvider timeProvider) {
    this.rewardConfiguration = rewardConfiguration;
    this.transactionRepository = transactionRepository;
    this.timeProvider = timeProvider;
    this.rewardThresholdsDescending = rewardConfiguration.getThresholds().descendingMap();
  }

  public RewardPoints calculateRewardPointsForUser(String userId) {
    LocalDateTime calculationStartDate = timeProvider.currentDateTime().minus(rewardConfiguration.getCalculationMonthAmount(), ChronoUnit.MONTHS);
    List<Transaction> transactionList =
        transactionRepository.findAllByUserIdAndCreatedAtAfterOrderByCreatedAt(userId, calculationStartDate)
            .stream()
            .map(Transaction::fromEntity)
            .toList();
    Map<Integer, Integer> monthlyPoints = new HashMap<>();

    transactionList.forEach(transaction -> {
      Integer month = transaction.createdAt().get(ChronoField.MONTH_OF_YEAR);
      Integer transactionPoints = calculateTransactionPoints(transaction);

      if (monthlyPoints.containsKey(month)) {
        Integer pointsForMonth = monthlyPoints.get(month);
        monthlyPoints.put(month, pointsForMonth + transactionPoints);
      } else {
        monthlyPoints.put(month, transactionPoints);
      }
    });

    TreeMap<Month, Integer> pointsMonthlySorted = new TreeMap<>(monthlyPoints.entrySet().stream()
        .collect(Collectors.toMap(entry -> Month.of(entry.getKey()), Map.Entry::getValue)));

    Integer sumOfPoints = monthlyPoints.values().stream()
        .reduce(0, Integer::sum);

    log.info("Calculated points for userId {}. Amount of points: {}", userId, sumOfPoints);
    return new RewardPoints(userId, pointsMonthlySorted, sumOfPoints);
  }

  private Integer calculateTransactionPoints(Transaction transaction) {
    int tempAmount = transaction.amount().setScale(0, RoundingMode.FLOOR).intValue();
    int totalPoints = 0;

    for (int threshold : rewardThresholdsDescending.keySet()) {
      int diff = tempAmount - threshold;
      if (diff > 0) {
        totalPoints += diff * rewardThresholdsDescending.get(threshold);
        tempAmount -= diff;
      }
    }

    return totalPoints;
  }
}
