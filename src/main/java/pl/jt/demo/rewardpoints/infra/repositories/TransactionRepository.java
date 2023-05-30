package pl.jt.demo.rewardpoints.infra.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import pl.jt.demo.rewardpoints.infra.repositories.entities.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

  List<TransactionEntity> findAllByUserIdAndCreatedAtAfterOrderByCreatedAt(@Param("userId") String userId, LocalDateTime startDate);

  List<TransactionEntity> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
}