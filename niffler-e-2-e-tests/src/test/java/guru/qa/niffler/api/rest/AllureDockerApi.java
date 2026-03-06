package guru.qa.niffler.api.rest;

import guru.qa.niffler.model.allure.Project;
import guru.qa.niffler.model.allure.ProjectResponse;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AllureDockerApi {

  @GET("projects")
  @Nonnull
  Call<ProjectResponse> projects();

  @GET("generate-report")
  @Nonnull
  Call<Void> generateReport(
      @Query("project_id") String projectId,
      @Query("execution_name") String executionName,
      @Query("execution_from") String executionFrom,
      @Query("execution_type") String executionType
  );

  @POST("projects")
  @Nonnull
  Call<Void> createProject(@Body Project project);

  @POST("send-results")
  @Headers("Content-Type: application/json")
  @Nonnull
  Call<Void> sendResults(
      @Query("project_id") String projectId,
      @Query("force_project_creation") String forceProjectCreation,
      @Body Map<String, List<Map<String, String>>> results
  );
}
