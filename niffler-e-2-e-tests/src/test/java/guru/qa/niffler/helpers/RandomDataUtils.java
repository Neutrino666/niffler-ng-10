package guru.qa.niffler.helpers;

import com.github.javafaker.Faker;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RandomDataUtils {

  private static final Faker faker = new Faker();

  @Nonnull
  public static String getRandomUserName() {
    return faker.name().username();
  }

  @Nonnull
  public static String getRandomName() {
    return faker.name().name();
  }

  @Nonnull
  public static String getRandomSurname() {
    return faker.name().lastName();
  }

  @Nonnull
  public static String getRandomCategoryName() {
    return faker.commerce().department();
  }

  @Nonnull
  public static String getRandomSentence() {
    return faker.lorem().sentence();
  }

  @Nonnull
  public static String getRandomPassword() {
    return faker.internet().password();
  }
}
