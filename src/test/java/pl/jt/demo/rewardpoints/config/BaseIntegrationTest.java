package pl.jt.demo.rewardpoints.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.jt.demo.rewardpoints.infra.repositories.TransactionRepository;
import pl.jt.demo.rewardpoints.testutils.TestTimeProvider;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ExtendWith(SpringExtension.class)
public class BaseIntegrationTest {

  @Autowired
  protected TestTimeProvider timeProvider;

  @Autowired
  protected TransactionRepository transactionRepository;

  @AfterEach
  void resetTime() {
    timeProvider.reset();
  }

}
