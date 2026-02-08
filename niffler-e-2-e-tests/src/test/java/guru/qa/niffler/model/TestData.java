package guru.qa.niffler.model;

import java.util.List;

public record TestData(String password,
                       List<UserJson> incomeInvitation,
                       List<UserJson> outcomeInvitation,
                       List<UserJson> friends,
                       List<CategoryJson> categories,
                       List<SpendJson> spendings
) {

}
