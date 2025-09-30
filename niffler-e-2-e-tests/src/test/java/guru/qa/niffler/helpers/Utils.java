package guru.qa.niffler.helpers;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.NonNull;

public class Utils {

  /**
   * Generate random string
   *
   * @param length min: 1 max: 100
   * @return String matching reqEx [a-zA-Z]{length}
   */
  public static String getRandomString(@NonNull final Integer length) {
      if (length <= 0 || length > 100) {
          throw new IllegalArgumentException("length should be from 1 to 100");
      }
    char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    Random random = new Random();
    return IntStream.range(0, length)
        .parallel()
        .mapToObj(i -> chars[random.nextInt(chars.length)])
        .map(Objects::toString)
        .collect(Collectors.joining());
  }
}
