package guru.qa.niffler.jupiter.extension.allure;

import guru.qa.niffler.jupiter.extension.SuiteExtension;
import guru.qa.niffler.service.AllureDockerApiClient;

public class AllureReportExtension implements SuiteExtension {

  @Override
  public void afterSuite() {
    if ("docker".equals(System.getProperty("test.env"))) {
      AllureDockerApiClient allureDockerApiClient = new AllureDockerApiClient();
      allureDockerApiClient.sendResults();
      allureDockerApiClient.generateReport();
    }
  }
}
