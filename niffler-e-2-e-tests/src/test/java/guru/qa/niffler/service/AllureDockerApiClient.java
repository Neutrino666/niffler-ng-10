package guru.qa.niffler.service;

import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.rest.AllureDockerApi;
import guru.qa.niffler.helpers.FileUtils;
import guru.qa.niffler.model.allure.Project;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  static {
    ALLURE_DOCKER_API = System.getenv("ALLURE_DOCKER_API") == null
        ? "not_found_allure_docker_api"
        : System.getenv("ALLURE_DOCKER_API");
    EXECUTION_TYPE = System.getenv("EXECUTION_TYPE") == null
        ? "not_found_execution_type"
        : System.getenv("EXECUTION_TYPE");
    HEAD_COMMIT_MESSAGE = System.getenv("HEAD_COMMIT_MESSAGE") == null
        ? "not_found_commit_message"
        : System.getenv("HEAD_COMMIT_MESSAGE");
    BUILD_URL = System.getenv("BUILD_URL") == null
        ? "not_found_build_url"
        : System.getenv("BUILD_URL");
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
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
  }

  public List<String> getProjects() {
    final JsonNode response;
    try {
      response = allureDockerApi.projects().execute().body();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Iterator<String> stringIterator = response.path("data").path("projects").fieldNames();
    List<String> projects = new ArrayList<>();
    while (stringIterator.hasNext()) {
      projects.add(stringIterator.next());
    }
    return Objects.requireNonNull(projects);
  }

  public void createProject() {
    final Response<Void> response;
    try {
      response = allureDockerApi.createProject(new Project(PROJECT_NAME))
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_CREATED);
  }

  public void generateReport() {
    final Response<JsonNode> response;
    try {
      response = allureDockerApi.generateReport(
              PROJECT_NAME,
              HEAD_COMMIT_MESSAGE,
              BUILD_URL,
              EXECUTION_TYPE
          )
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
  }
}
