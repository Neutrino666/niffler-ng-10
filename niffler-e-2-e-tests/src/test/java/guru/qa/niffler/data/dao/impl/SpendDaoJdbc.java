package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
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

  private final static Config CFG = Config.getInstance();

  @Override
  public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)"
            + "VALUES(?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
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

  @Nonnull
  @Override
  public SpendEntity update(@Nonnull SpendEntity spend) {
    String sql = """
        UPDATE spend
        SET username = ?, spend_date =?, currency =?, amount =?, description =?, category_id =?
        WHERE id = ?
        """;
    try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6,
          spend.getCategory() != null && spend.getCategory().getId() != null
              ? spend.getCategory().getId()
              : null
      );
      ps.setObject(7, spend.getId());

      ps.executeUpdate();
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("id")
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        return rs.next()
            ? Optional.of(collectEntity(rs))
            : Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username) {
    List<SpendEntity> spends = new ArrayList<>();
    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("username")
    )) {
      ps.setString(1, username);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          spends.add(collectEntity(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spends;
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    List<SpendEntity> spends = new ArrayList<>();
    try (PreparedStatement ps = getConnection().prepareStatement(
        "SELECT "
            + "s.id, s.amount, s.currency, s.description, s.spend_date, s.username, "
            + "c.id AS c_id, c.username AS c_username, c.archived AS c_archived, c.name AS c_name "
            + "FROM spend AS s "
            + "JOIN category AS c "
            + "ON s.category_id = c.id "
    )) {
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          spends.add(collectEntity(rs));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spends;
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(@Nonnull String username,
      @Nonnull String description) {
    String sql = "SELECT "
        + "s.id, s.amount, s.currency, s.description, s.spend_date, s.username, "
        + "c.id AS c_id, c.username AS c_username, c.archived AS c_archived, c.name AS c_name "
        + "FROM spend AS s "
        + "JOIN category AS c "
        + "ON s.category_id = c.id "
        + "WHERE s.username = ? "
        + "AND s.description = ?";
    try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, description);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        return rs.next()
            ? Optional.of(collectEntity(rs))
            : Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(@Nonnull SpendEntity spend) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "DELETE FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static @Nonnull SpendEntity collectEntity(@Nonnull ResultSet rs)
      throws SQLException {
    CategoryEntity categoryEntity = new CategoryEntity();
    if (rs.getObject("c_id") != null) {
      categoryEntity.setId(rs.getObject("c_id", UUID.class));
      categoryEntity.setName(rs.getString("c_name"));
      categoryEntity.setUsername(rs.getString("c_username"));
      categoryEntity.setArchived(rs.getBoolean("c_archived"));
    }
    SpendEntity spend = new SpendEntity();
    spend.setId(rs.getObject("id", UUID.class));
    spend.setSpendDate(rs.getDate("spend_date"));
    spend.setCategory(categoryEntity);
    spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    spend.setAmount(rs.getDouble("amount"));
    spend.setDescription(rs.getString("description"));
    spend.setUsername(rs.getString("username"));
    return spend;
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

  private Connection getConnection() {
    return holder(CFG.spendJdbcUrl()).connection();
  }
}
