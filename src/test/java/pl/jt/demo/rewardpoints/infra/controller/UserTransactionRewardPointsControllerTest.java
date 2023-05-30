package pl.jt.demo.rewardpoints.infra.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.jt.demo.rewardpoints.config.BaseIntegrationTest;
import pl.jt.demo.rewardpoints.domain.RewardPoints;
import pl.jt.demo.rewardpoints.domain.Transaction;
import pl.jt.demo.rewardpoints.infra.repositories.entities.TransactionEntity;
import pl.jt.demo.rewardpoints.infra.requests.TransactionRequest;
import pl.jt.demo.rewardpoints.infra.utils.TimeProvider;


@AutoConfigureMockMvc
public class UserTransactionRewardPointsControllerTest extends BaseIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @AfterEach
  private void cleanUp() {
    transactionRepository.deleteAll();
  }

  private Random random = new Random();

  @Test
  public void createTransactionReturns201WhenProperRequestSent() {
    var url = "http://localhost:" + port + "/transactions/transaction";
    var userId = UUID.randomUUID().toString();
    var amount = random.nextDouble(1, 100);
    var date = "2021-05-23 00:12:11";

    TransactionRequest request = new TransactionRequest(userId, date, amount);
    ResponseEntity<Void> response = testRestTemplate.postForEntity(url, request, Void.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.CREATED, statusCode);
    assertFalse(transactionRepository.findAll().isEmpty());
  }

  @Test
  public void createTransactionReturns400WhenInvalidDateSentInRequest() {
    var url = "http://localhost:" + port + "/transactions/transaction";
    var userId = UUID.randomUUID().toString();
    var amount =  random.nextDouble(1, 100);
    var date = "20210523 00:12";

    TransactionRequest request = new TransactionRequest(userId, date, amount);
    ResponseEntity<Void> response = testRestTemplate.postForEntity(url, request, Void.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
    assertTrue(transactionRepository.findAll().isEmpty());
  }

  @Test
  public void createTransactionReturns400WhenInvalidAmountSentInRequest() {
    var url = "http://localhost:" + port + "/transactions/transaction";
    var userId = UUID.randomUUID().toString();
    var amount = -10.43;
    var date = "2021-05-23 00:12:11";

    TransactionRequest request = new TransactionRequest(userId, date, amount);
    ResponseEntity<Void> response = testRestTemplate.postForEntity(url, request, Void.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
    assertTrue(transactionRepository.findAll().isEmpty());
  }

  @Test
  public void updateTransactionReturns202WhenProperRequestSent() {

    transactionRepository.save(TransactionEntity.builder()
        .createdAt(LocalDateTime.now())
        .userId(UUID.randomUUID().toString())
        .amount(BigDecimal.ONE)
        .build());

    Long id = transactionRepository.findAll().get(0).getId();
    
    var url = "http://localhost:" + port + "/transactions/transaction/" + id;
    var userId = UUID.randomUUID().toString();
    var amount = random.nextDouble(1, 100);
    var date = "2021-05-23 00:12:11";

    TransactionRequest request = new TransactionRequest(userId, date, amount);
    ResponseEntity<Void> response = testRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request), Void.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.ACCEPTED, statusCode);
    assertFalse(transactionRepository.findAll().isEmpty());
    assertEquals(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP), transactionRepository.findAll().get(0).getAmount());
  }

  @Test
  public void updateTransactionReturns404WhenInexistentIdSent() {
    var url = "http://localhost:" + port + "/transactions/transaction/1";
    var userId = UUID.randomUUID().toString();
    var amount = random.nextDouble(1, 100);
    var date = "2021-05-23 00:12:11";

    TransactionRequest request = new TransactionRequest(userId, date, amount);
    ResponseEntity<Void> response = testRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request), Void.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);
    assertTrue(transactionRepository.findAll().isEmpty());
  }

  @Test
  public void deleteTransactionReturns200WhenProperRequestSent() {
    transactionRepository.save(TransactionEntity.builder()
        .createdAt(LocalDateTime.now())
        .id(1L)
        .userId(UUID.randomUUID().toString())
        .amount(BigDecimal.ONE)
        .build());
    
    var url = "http://localhost:" + port + "/transactions/transaction/1";

    ResponseEntity<Void> response = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);
    assertTrue(transactionRepository.findAll().isEmpty());
  }

  @Test
  public void deleteTransactionReturns404WhenInexistentIdSent() {

    transactionRepository.save(TransactionEntity.builder()
        .createdAt(LocalDateTime.now())
        .id(2L)
        .userId(UUID.randomUUID().toString())
        .amount(BigDecimal.ONE)
        .build());

    var url = "http://localhost:" + port + "/transactions/transaction/1";
    ResponseEntity<Void> response = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.NOT_FOUND, statusCode);
    assertFalse(transactionRepository.findAll().isEmpty());
  }

  @Test
  public void getRewardPointsReturns200WhenProperRequestSent() {
    timeProvider.fixedDefault();
    var url = "http://localhost:" + port + "/transactions/reward-points/user/{id}";
    var userId = UUID.randomUUID().toString();

    transactionRepository.save(TransactionEntity.builder()
        .createdAt(LocalDateTime.from(TimeProvider.getTimeFormatter().parse("2021-11-05 08:10:12")))
        .userId(userId)
        .amount(BigDecimal.valueOf(60))
        .build());

    ResponseEntity<RewardPoints> response = testRestTemplate.getForEntity(url, RewardPoints.class, userId);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    RewardPoints rewardPoints = response.getBody();
    assertNotNull(rewardPoints);
    assertEquals(10, rewardPoints.sumOfAllPoints());
    assertTrue(rewardPoints.pointsByMonth().containsKey(Month.NOVEMBER));
    assertEquals(10, rewardPoints.pointsByMonth().get(Month.NOVEMBER));
    assertEquals(userId.toString(), rewardPoints.userId());
  }

  @Test
  public void getTransactionsReturns200WhenProperRequestSent() {
    var url = "http://localhost:" + port + "/transactions/user/{id}";
    var userId = UUID.randomUUID().toString();

    transactionRepository.save(TransactionEntity.builder()
        .createdAt(LocalDateTime.now())
        .userId(userId)
        .amount(BigDecimal.valueOf(60))
        .build());

    ResponseEntity<List> response = testRestTemplate.getForEntity(url, List.class, userId);

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(HttpStatus.OK, statusCode);

    List<Transaction> transactionList = response.getBody();
    assertNotNull(transactionList);
    assertFalse(transactionList.isEmpty());
  }

}