package guru.qa.niffler.api.spend;

import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CategoryApi {

  @GET("/internal/categories/all")
  Call<List<CategoryJson>> getCategories(@Query("username") String username,
      @Query("excludeArchived") boolean excludeArchived);

  @POST("/internal/categories/add")
  Call<CategoryJson> createCategory(@Body CategoryJson category);

  @PATCH("/internal/categories/update")
  Call<CategoryJson> updateCategory(@Body CategoryJson category);
}
