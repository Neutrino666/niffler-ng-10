package guru.qa.niffler.model.allure;

import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record Projects(
    Map<String, Map<String, String>> projects
) {

}
