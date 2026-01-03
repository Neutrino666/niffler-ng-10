package guru.qa.niffler.service.spend;

import guru.qa.niffler.model.SpendJson;
import java.util.List;
import javax.annotation.Nonnull;

public interface SpendClient {

  @Nonnull
  SpendJson create(@Nonnull SpendJson spend);

  @Nonnull
  SpendJson update(@Nonnull SpendJson spend);

  @Nonnull
  List<SpendJson> findAllByUsername(@Nonnull String username);

  void remove(@Nonnull SpendJson spend);
}
