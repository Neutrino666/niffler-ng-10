package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.rest.GithubApi;
import io.qameta.allure.Step;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class GithubApiClient extends RestClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private final GithubApi githubApi;

  public GithubApiClient() {
    super(CFG.githubUrl());
    githubApi = create(GithubApi.class);
  }

  @Nonnull
  @Step("REST API Получение информации о статусе дефекта")
  public String issueState(final String issueNumber) {
    final JsonNode response;
    try {
      response = githubApi.issue("Bearer " + System.getenv(GH_TOKEN_ENV), issueNumber)
          .execute()
          .body();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    return Objects.requireNonNull(response.get("state").asText());
  }
}
