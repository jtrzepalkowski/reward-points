package pl.jt.demo.rewardpoints.infra.controllers;

import java.util.List;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jt.demo.rewardpoints.domain.RewardPoints;
import pl.jt.demo.rewardpoints.domain.Transaction;
import pl.jt.demo.rewardpoints.infra.exceptions.NotFoundException;
import pl.jt.demo.rewardpoints.infra.requests.TransactionRequest;
import pl.jt.demo.rewardpoints.infra.services.RewardPointsService;
import pl.jt.demo.rewardpoints.infra.services.TransactionService;

@RestController
@RequestMapping("/transactions")
@Tag(name = "transactions", description = "Transactions and reward points API")
public class UserTransactionRewardPointsController {

  private final TransactionService transactionService;
  private final RewardPointsService rewardPointsService;

  UserTransactionRewardPointsController(TransactionService transactionService, RewardPointsService rewardPointsService) {
    this.transactionService = transactionService;
    this.rewardPointsService = rewardPointsService;
  }

  @PostMapping("/transaction")
  public ResponseEntity<Void> createTransaction(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "request to create transaction",
                                                    content = @Content(
                                                        schema = @Schema(implementation = TransactionRequest.class),
                                                        examples = {
                                                            @ExampleObject(
                                                                name = "An example request to create transaction.",
                                                                value = "{ \"userId\" : \"28418824-6055-46c5-9319-1dee08ef05d8\", \"createdAt\": \"2021-05-23 00:12:11\", \"amount\": 10.0 }",
                                                                summary = "create transaction request")})) @RequestBody TransactionRequest request) {
    transactionService.create(
        request.createdAt(),
        request.amount(),
        request.userId());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/transaction/{id}")
  public ResponseEntity<Void> updateTransaction(@Parameter(description = "transaction id", example = "1") @PathVariable("id") Long transactionId,
                                                @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "request to update transaction",
                                                    content = @Content(
                                                        schema = @Schema(implementation = TransactionRequest.class),
                                                        examples = {
                                                            @ExampleObject(
                                                                name = "An example request to update transaction.",
                                                                value = "{ \"userId\" : \"28418824-6055-46c5-9319-1dee08ef05d8\", \"createdAt\": \"2021-05-23 00:12:11\", \"amount\": 10.0 }",
                                                                summary = "update transaction request")})) @RequestBody TransactionRequest request)
      throws NotFoundException {
    transactionService.update(
        transactionId,
        request.createdAt(),
        request.amount(),
        request.userId());

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @DeleteMapping("/transaction/{id}")
  public ResponseEntity<Void> deleteTransaction(@Parameter(description = "transaction id", example = "1") @PathVariable("id") Long transactionId)
      throws NotFoundException {
    transactionService.remove(transactionId);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<List<Transaction>> getUserTransactions(
      @Parameter(description = "user id", example = "28418824-6055-46c5-9319-1dee08ef05d8") @PathVariable("id") String userId) {
    List<Transaction> allTransactionsForUser = transactionService.retrieveTransactionsForUser(userId);

    return ResponseEntity.status(HttpStatus.OK).body(allTransactionsForUser);
  }

  @GetMapping("/reward-points/user/{id}")
  public ResponseEntity<RewardPoints> getUserRewardPoints(
      @Parameter(description = "user id", example = "28418824-6055-46c5-9319-1dee08ef05d8") @PathVariable("id") String userId) {
    RewardPoints rewardPoints = rewardPointsService.calculateRewardPointsForUser(userId);

    return ResponseEntity.status(HttpStatus.OK).body(rewardPoints);
  }

}
