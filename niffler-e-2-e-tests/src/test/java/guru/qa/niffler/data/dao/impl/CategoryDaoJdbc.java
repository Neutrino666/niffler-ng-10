package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.CategoryEntity;
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

public class CategoryDaoJdbc implements CategoryDao {

  private final Config CFG = Config.getInstance();

  @Override
  public @Nonnull CategoryEntity create(@Nonnull CategoryEntity category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO category (username, name, archived)"
              + "VALUES(?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, category.getUsername());
        ps.setString(2, category.getName());
        ps.setBoolean(3, category.isArchived());

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            return collectEntityFrom(rs);
          } else {
            throw new SQLException("Can't find id in ResultSet");
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<CategoryEntity> findById(@Nonnull UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          getSelectByWhereIs("id")
      )) {
        ps.setObject(1, id);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          return rs.next()
              ? Optional.of(collectEntityFrom(rs))
              : Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<CategoryEntity> findByUsernameAndName(@Nonnull String username,
      @Nonnull String categoryName) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT * FROM category WHERE username = ? AND name = ?"
      )) {
        ps.setString(1, username);
        ps.setString(2, categoryName);

        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          Optional<CategoryEntity> category = Optional.empty();
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setUsername(rs.getString("username"));
            ce.setName(rs.getString("name"));
            ce.setArchived(rs.getBoolean("archived"));
            category = Optional.of(
                ce
            );
          }
          return category;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull List<CategoryEntity> findAllByUsername(@Nonnull String username) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          getSelectByWhereIs("username")
      )) {
        ps.setString(1, username);

        ps.execute();
        List<CategoryEntity> ceList = new ArrayList<>();
        try (ResultSet rs = ps.getResultSet()) {
          while (rs.next()) {
            ceList.add(collectEntityFrom(rs));
          }
        }
        return ceList;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(@Nonnull CategoryEntity category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM category WHERE id = ?"
      )) {
        ps.setObject(1, category.getId());
        ps.execute();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nonnull CategoryEntity collectEntityFrom(@Nonnull ResultSet rs) throws SQLException {
    CategoryEntity ce = new CategoryEntity();
    ce.setId(rs.getObject("id", UUID.class));
    ce.setUsername(rs.getString("username"));
    ce.setName(rs.getString("name"));
    ce.setArchived(rs.getBoolean("archived"));
    return ce;
  }

  private @Nonnull String getSelectByWhereIs(@Nonnull String key) {
    return "SELECT * FROM category WHERE %s = ?".formatted(key);
  }
}
