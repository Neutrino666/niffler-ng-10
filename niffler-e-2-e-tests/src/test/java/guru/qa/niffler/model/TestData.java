package guru.qa.niffler.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public record TestData(String password,
                       List<UserJson> incomeInvitation,
                       List<UserJson> outcomeInvitation,
                       List<UserJson> friends,
                       List<CategoryJson> categories,
                       List<SpendJson> spendings
) {

  public TestData(@Nonnull String password) {
    this(
        password,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
    );
  }
}
