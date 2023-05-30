package pl.jt.demo.rewardpoints.infra.requests;

public record TransactionRequest(
    String userId,
    String createdAt,
    Double amount) {
}
