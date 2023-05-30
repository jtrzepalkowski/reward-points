package pl.jt.demo.rewardpoints.domain;


import java.time.Month;
import java.util.TreeMap;

public record RewardPoints(String userId,
                           TreeMap<Month, Integer> pointsByMonth,
                           Integer sumOfAllPoints) {
}
