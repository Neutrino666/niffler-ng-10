package guru.qa.niffler.api.rest.core;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

@ParametersAreNonnullByDefault
public class CodeInterceptor implements Interceptor {

  @Override
  @Nonnull
  public Response intercept(Chain chain) throws IOException {
    Response response = chain.proceed(chain.request());
    if (response.isRedirect()) {
      String location = Objects.requireNonNull(
          response.header("Location")
      );
      if (location.contains("code=")) {
        ApiLoginExtension.setCode(
            StringUtils.substringAfter(
                location, "code="
            )
        );
      }
    }
    return response;
  }
}
