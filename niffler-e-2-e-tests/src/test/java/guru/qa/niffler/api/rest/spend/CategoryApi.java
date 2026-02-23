package guru.qa.niffler.api.rest.spend;

import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import javax.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CategoryApi {

  @GET("/internal/categories/all")
  @Nonnull
  Call<List<CategoryJson>> getCategories(@Query("username") String username,
      @Query("excludeArchived") boolean excludeArchived);

  @POST("/internal/categories/add")
  @Nonnull
  Call<CategoryJson> createCategory(@Body CategoryJson category);

  @PATCH("/internal/categories/update")
  @Nonnull
  Call<CategoryJson> updateCategory(@Body CategoryJson category);
}
