package guru.qa.niffler.service.spend;

import guru.qa.niffler.model.SpendJson;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SpendClient {

  @Nullable
  SpendJson create(SpendJson spend);

  @Nullable
  SpendJson update(SpendJson spend);

  @Nonnull
  List<SpendJson> findAllByUsername(String username);

  void remove(SpendJson spend);
}
