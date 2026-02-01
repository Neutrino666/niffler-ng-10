package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
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
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class CategoryDaoJdbc implements CategoryDao {

  private final static Config CFG = Config.getInstance();

  @Override
  public @Nonnull CategoryEntity create(CategoryEntity category) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        """
            INSERT INTO category (username, name, archived)
            VALUES(?, ?, ?)
            """,
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, category.getUsername());
      ps.setString(2, category.getName());
      ps.setBoolean(3, category.isArchived());

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return collectEntity(rs);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public CategoryEntity update(CategoryEntity category) {
    String sql = """
        UPDATE category
        SET name = ?, username =?, archived =?
        WHERE id = ?
        """;
    try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
      ps.setString(1, category.getName());
      ps.setString(2, category.getUsername());
      ps.setBoolean(3, category.isArchived());
      ps.setObject(4, category.getId());

      ps.executeUpdate();
      return category;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<CategoryEntity> findById(UUID id) {
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
  public @Nonnull Optional<CategoryEntity> findByUsernameAndName(
      String username,
      String categoryName) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "SELECT * FROM category WHERE username = ? AND name = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, categoryName);

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
  public @Nonnull List<CategoryEntity> findAllByUsername(String username) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("username")
    )) {
      ps.setString(1, username);

      ps.execute();
      List<CategoryEntity> ceList = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          ceList.add(collectEntity(rs));
        }
        return ceList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public List<CategoryEntity> findAll() {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "SELECT * FROM category"
    )) {
      ps.execute();
      List<CategoryEntity> ceList = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          ceList.add(collectEntity(rs));
        }
        return ceList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(CategoryEntity category) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "DELETE FROM category WHERE id = ?"
    )) {
      ps.setObject(1, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static @Nonnull CategoryEntity collectEntity(ResultSet rs) throws SQLException {
    CategoryEntity ce = new CategoryEntity();
    ce.setId(rs.getObject("id", UUID.class));
    ce.setUsername(rs.getString("username"));
    ce.setName(rs.getString("name"));
    ce.setArchived(rs.getBoolean("archived"));
    return ce;
  }

  private @Nonnull String getSelectByWhereIs(String key) {
    return "SELECT * FROM category WHERE %s = ?".formatted(key);
  }

  private Connection getConnection() {
    return holder(CFG.spendJdbcUrl()).connection();
  }
}
