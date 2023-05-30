package pl.jt.demo.rewardpoints.infra.utils;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SystemTimeProvider implements TimeProvider{

  @Override
  public LocalDateTime currentDateTime() {
    return LocalDateTime.now();
  }
}
