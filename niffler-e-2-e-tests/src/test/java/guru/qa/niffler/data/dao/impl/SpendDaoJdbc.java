package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpendDaoJdbc implements SpendDao {

  private final Connection connection;

  public SpendDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)"
            + "VALUES(?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, spend.getSpendDate());
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

      ps.executeUpdate();
      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
      }
      spend.setId(generatedKey);
      return spend;

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
        getSelectByWhereIs("id")
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        return rs.next()
            ? collectEntityFrom(rs)
            : Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username) {
    List<SpendEntity> spends = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(
        getSelectByWhereIs("username")
    )) {
      ps.setString(1, username);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          spends.add(collectEntityFrom(rs).orElse(null));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spends;
  }

  @Override
  public void delete(@Nonnull SpendEntity spend) {
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nonnull Optional<SpendEntity> collectEntityFrom(@Nonnull ResultSet rs)
      throws SQLException {
    Optional<CategoryEntity> category = Optional.empty();
    if (rs.getObject("c_id") != null) {
      CategoryEntity categoryEntity = new CategoryEntity();
      categoryEntity.setId(rs.getObject("c_id", UUID.class));
      categoryEntity.setName(rs.getString("c_name"));
      categoryEntity.setUsername(rs.getString("c_username"));
      categoryEntity.setArchived(rs.getBoolean("c_archived"));
      category = Optional.of(categoryEntity);
    }
    SpendEntity spend = new SpendEntity();
    spend.setId(rs.getObject("id", UUID.class));
    spend.setSpendDate(rs.getDate("spend_date"));
    spend.setCategory(category.orElse(null));
    spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    spend.setAmount(rs.getDouble("amount"));
    spend.setDescription(rs.getString("description"));
    spend.setUsername(rs.getString("username"));
    return Optional.of(spend);
  }

  private @Nonnull String getSelectByWhereIs(@Nonnull String key) {
    return "SELECT "
        + "s.id, s.amount, s.currency, s.description, s.spend_date, s.username, "
        + "c.id AS c_id, c.username AS c_username, c.archived AS c_archived, c.name AS c_name "
        + "FROM spend AS s "
        + "JOIN category AS c "
        + "ON s.category_id = c.id "
        + "WHERE s.%s = ?".formatted(key);
  }
}
