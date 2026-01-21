package guru.qa.niffler.service.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.spend.SpendingRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

  private final static Config CFG = Config.getInstance();

  private final SpendRepository spendRepository = new SpendingRepositoryHibernate();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Nullable
  @Override
  public SpendJson create(SpendJson spend) {
    return xaTxTemplate.execute(() -> SpendJson.fromEntity(
            spendRepository.create(SpendEntity.fromJson(spend))
        )
    );
  }

  @Nullable
  @Override
  public SpendJson update(SpendJson spend) {
    return xaTxTemplate.execute(() -> SpendJson.fromEntity(
            spendRepository.update(SpendEntity.fromJson(spend))
        )
    );
  }

  @Nonnull
  public CategoryJson updateCategory(CategoryJson category) {
    return Objects.requireNonNull(
        xaTxTemplate.execute(() -> CategoryJson.fromEntity(
                spendRepository.updateCategory(CategoryEntity.fromJson(category))
            )
        )
    );
  }

  @Nullable
  public CategoryJson createCategory(CategoryJson category) {
    return xaTxTemplate.execute(() -> CategoryJson.fromEntity(
            spendRepository.createCategory(CategoryEntity.fromJson(category))
        )
    );
  }

  @Nonnull
  public Optional<CategoryJson> findCategoryById(UUID id) {
    return Objects.requireNonNull(
            jdbcTxTemplate.execute(() -> spendRepository.findCategoryById(id))
        )
        .map(CategoryJson::fromEntity);
  }

  @Nonnull
  public Optional<CategoryJson> findCategoryByUsernameAndSpendName(
      String username,
      String name) {
    return Objects.requireNonNull(
            jdbcTxTemplate.execute(
                () -> spendRepository.findCategoryByUsernameAndCategoryName(username, name)
            )
        )
        .map(CategoryJson::fromEntity);
  }

  @Nonnull
  public Optional<SpendJson> findById(UUID id) {
    return Objects.requireNonNull(
            jdbcTxTemplate.execute(() -> spendRepository.findById(id))
        )
        .map(SpendJson::fromEntity);
  }

  @Nonnull
  public List<SpendJson> findAll() {
    return Objects.requireNonNull(
        jdbcTxTemplate.execute(() ->
            spendRepository.findAll()
                .stream()
                .map(SpendJson::fromEntity)
                .toList()
        ));
  }

  @Nonnull
  @Override
  public List<SpendJson> findAllByUsername(String username) {
    return Objects.requireNonNull(
            jdbcTxTemplate.execute(() -> spendRepository.findAllByUsername(username))
        )
        .stream()
        .map(SpendJson::fromEntity)
        .toList();
  }

  @Nonnull
  public Optional<SpendJson> findByUsernameAndSpendDescription(
      String username,
      String description) {
    return Objects.requireNonNull(
            jdbcTxTemplate.execute(
                () -> spendRepository.findByUsernameAndSpendDescription(username, description)
            )
        )
        .map(SpendJson::fromEntity);
  }

  @Override
  public void remove(SpendJson spend) {
    jdbcTxTemplate.execute(() -> {
      spendRepository.remove(SpendEntity.fromJson(spend));
      return null;
    });
  }

  public void removeCategory(CategoryJson category) {
    xaTxTemplate.execute(() -> {
          spendRepository.removeCategory(CategoryEntity.fromJson(category));
          return null;
        }
    );
  }
}
