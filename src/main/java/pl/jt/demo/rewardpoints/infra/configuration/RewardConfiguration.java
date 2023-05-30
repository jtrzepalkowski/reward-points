package pl.jt.demo.rewardpoints.infra.configuration;

import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "reward-config")
@Getter
@Setter
@Component
public class RewardConfiguration {

  private TreeMap<Integer, Integer> thresholds;

  private Integer calculationMonthAmount;

}
