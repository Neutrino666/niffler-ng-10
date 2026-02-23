package guru.qa.niffler.api.rest;

import com.fasterxml.jackson.databind.JsonNode;
import javax.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface GithubApi {

  @GET("/repos/Neutrino666/niffler-ng-10/issues/{issue_number}")
  @Headers({
      "Accept: application/vnd.github+json",
      "X-GitHub-Api-Version: 2022-11-28"
  })
  @Nonnull
  Call<JsonNode> issue(@Header("Authorization") String bearerToken,
      @Path("issue_number") String issue_number);
}
