package guru.qa.niffler.jupiter.extension.allure;

import guru.qa.niffler.jupiter.extension.SuiteExtension;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@ParametersAreNonnullByDefault
public final class AllureBackendLogsExtension implements SuiteExtension {

  public static final String caseName = "Niffler backend logs";

  @Getter
  @RequiredArgsConstructor
  private enum Service {
    AUTH("auth"),
    CURRENCY("currency"),
    GATEWAY("gateway"),
    SPEND("spend"),
    USERDATA("userdata"),
    ;

    @Nonnull
    private final String value;
  }

  @Override
  public void afterSuite() {
    final AllureLifecycle allureLifecycle = Allure.getLifecycle();
    final String caseId = UUID.randomUUID().toString();
    allureLifecycle.scheduleTestCase(
        new TestResult().setUuid(caseId)
            .setName(caseName)
    );
    allureLifecycle.startTestCase(caseId);

    addAllLogs(allureLifecycle);

    allureLifecycle.stopTestCase(caseId);
    allureLifecycle.writeTestCase(caseId);
  }

  @SneakyThrows
  private void addAllLogs(AllureLifecycle lifecycle) {
    for (Service service : Service.values()) {
      String value = service.getValue();
      lifecycle.addAttachment(
          "Niffler-%s log".formatted(value),
          "text/html",
          ".log",
          Files.newInputStream(
              Path.of("./logs/niffler-%s/app.log".formatted(value))
          )
      );
    }
  }
}
