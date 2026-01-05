package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class CategorySpringDaoJdbc implements CategoryDao {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public CategoryEntity create(@Nonnull CategoryEntity category) {
    KeyHolder kh = new GeneratedKeyHolder();
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
          "INSERT INTO category (username, name, archived)"
              + "VALUES(?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, category.getUsername());
      ps.setString(2, category.getName());
      ps.setBoolean(3, category.isArchived());
      return ps;
    }, kh);
    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    category.setId(generatedKey);
    return category;
  }

  @Nonnull
  @Override
  public CategoryEntity update(@Nonnull CategoryEntity category) {
    String sql = """
        UPDATE category
        SET name = ?, username =?, archived =?
        WHERE id = ?
        """;
    getJdbcTemplate().update(
        sql,
        category.getName(),
        category.getUsername(),
        category.isArchived(),
        category.getId()
    );
    return category;
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findById(@Nonnull UUID id) {
    return Optional.ofNullable(
        getJdbcTemplate().queryForObject(
            getSelectByWhereIs("id"),
            CategoryEntityRowMapper.INSTANCE,
            id
        )
    );
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findByUsernameAndName(@Nonnull String username,
      @Nonnull String categoryName) {
    return Optional.ofNullable(
        getJdbcTemplate().queryForObject(
            "SELECT * FROM category WHERE username = ? AND name = ?",
            CategoryEntityRowMapper.INSTANCE,
            username,
            categoryName
        )
    );
  }

  @Nonnull
  @Override
  public List<CategoryEntity> findAllByUsername(@Nonnull String username) {
    return getJdbcTemplate()
        .query(
            getSelectByWhereIs("username"),
            CategoryEntityRowMapper.INSTANCE,
            username
        );
  }

  @Nonnull
  @Override
  public List<CategoryEntity> findAll() {
    return getJdbcTemplate()
        .query(
            "SELECT * FROM category",
            CategoryEntityRowMapper.INSTANCE
        );
  }

  @Override
  public void remove(@Nonnull CategoryEntity category) {
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
          "DELETE FROM category WHERE id = ?"
      );
      ps.setObject(1, category.getId());
      return ps;
    });
  }

  @Nonnull
  private JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
  }

  @Nonnull
  private String getSelectByWhereIs(@Nonnull String key) {
    return "SELECT * FROM category WHERE %s = ?".formatted(key);
  }
}
