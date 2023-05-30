package pl.jt.demo.rewardpoints.infra.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface TimeProvider {

  LocalDateTime currentDateTime();

  static DateTimeFormatter getTimeFormatter() {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  }

  static LocalDateTime createInstantFromString(String date) {
    return LocalDateTime.from(getTimeFormatter().parse(date));
  }
}
