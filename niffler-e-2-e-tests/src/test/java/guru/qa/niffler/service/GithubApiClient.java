package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GithubApi;
import guru.qa.niffler.config.Config;
import java.io.IOException;
import java.util.Objects;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GithubApiClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.githubUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final GithubApi githubApi = retrofit.create(GithubApi.class);

  public String issueState(String issueNumber) {
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
