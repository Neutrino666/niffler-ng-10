package guru.qa.niffler.helpers;

import com.github.javafaker.Faker;

public class RandomDataUtils {

  private static final Faker faker = new Faker();

  public static String getRandomUserName() {
    return faker.name().username();
  }

  public static String getRandomName() {
    return faker.name().name();
  }

  public static String getRandomSurname() {
    return faker.name().lastName();
  }

  public static String getRandomCategoryName() {
    return faker.commerce().department();
  }

  public static String getRandomSentence() {
    return faker.lorem().sentence();
  }

  public static String getRandomPassword() {
    return faker.internet().password();
  }
}
