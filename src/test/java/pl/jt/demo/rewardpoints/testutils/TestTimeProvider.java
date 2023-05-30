package pl.jt.demo.rewardpoints.testutils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.jt.demo.rewardpoints.infra.utils.TimeProvider;

@Component
@Primary
public class TestTimeProvider implements TimeProvider {

  private Clock clock = Clock.systemUTC();

  @Override
  public LocalDateTime currentDateTime() {
    return LocalDateTime.now(clock);
  }

  public void fixed(Instant instant) {
    clock = Clock.fixed(instant, ZoneId.systemDefault());
  }

  public void fixedDefault() {
    clock = Clock.fixed(Instant.ofEpochSecond(1640000000), ZoneId.systemDefault());
  }

  public void reset() {
    clock = Clock.systemUTC();
  }

}
