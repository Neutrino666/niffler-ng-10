package guru.qa.niffler.model.allure;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record ScreenDiff(String expected,
                         String actual,
                         String diff) {

}
