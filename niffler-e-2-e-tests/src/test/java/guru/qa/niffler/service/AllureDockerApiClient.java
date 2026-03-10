package guru.qa.niffler.service;

import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import guru.qa.niffler.api.rest.AllureDockerApi;
import guru.qa.niffler.helpers.FileUtils;
import guru.qa.niffler.model.allure.Project;
import guru.qa.niffler.model.allure.ProjectResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.assertj.core.api.Assertions;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public final class AllureDockerApiClient extends RestClient {

  private final AllureDockerApi allureDockerApi;
  private static final String PROJECT_NAME = "niffler";
  private static final String ALLURE_DOCKER_API;
  private static final String EXECUTION_TYPE;
  private static final String HEAD_COMMIT_MESSAGE;
  private static final String BUILD_URL;

  private static String getEnvVariable(String key, String defaultValue) {
    return Optional.ofNullable(System.getenv(key))
        .orElse(defaultValue);
  }

  static {
    ALLURE_DOCKER_API = getEnvVariable("ALLURE_DOCKER_API", "http://127.0.0.1:5050");
    EXECUTION_TYPE = getEnvVariable("EXECUTION_TYPE", "not_found_execution_type");
    HEAD_COMMIT_MESSAGE = getEnvVariable("HEAD_COMMIT_MESSAGE", "not_found_commit_message");
    BUILD_URL = getEnvVariable("BUILD_URL", "http://127.0.0.1:5252");
  }

  public AllureDockerApiClient() {
    super(ALLURE_DOCKER_API, Level.NONE);
    allureDockerApi = create(AllureDockerApi.class);
  }

  public void sendResults() {
    if (!getProjects().contains(PROJECT_NAME)) {
      createProject();
    }
    final Response<Void> response;
    try {
      List<Map<String, String>> files = FileUtils.getAllureResults();
      response = allureDockerApi.sendResults(PROJECT_NAME, "false", Map.of("results", files)).execute();
    } catch (IOException e) {
      throw new RuntimeException("Error while executing request", e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
  }

  public Set<String> getProjects() {
    final ProjectResponse projectResponse;
    try {
      projectResponse = allureDockerApi.projects().execute().body();
    } catch (IOException e) {
      throw new RuntimeException("Error while executing request", e);
    }
    return projectResponse.data().projects().keySet();
  }

  public void createProject() {
    final Response<Void> response;
    try {
      response = allureDockerApi.createProject(new Project(PROJECT_NAME))
          .execute();
    } catch (IOException e) {
      throw new RuntimeException("Error while executing request", e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_CREATED);
  }

  public void generateReport() {
    final Response<Void> response;
    try {
      response = allureDockerApi.generateReport(
              PROJECT_NAME,
              HEAD_COMMIT_MESSAGE,
              BUILD_URL,
              EXECUTION_TYPE
          )
          .execute();
    } catch (IOException e) {
      throw new RuntimeException("Error while executing request", e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
  }
}
