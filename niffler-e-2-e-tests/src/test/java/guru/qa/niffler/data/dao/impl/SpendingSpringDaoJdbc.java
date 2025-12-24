package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
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

public class SpendingSpringDaoJdbc implements SpendDao {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public SpendEntity create(@Nonnull SpendEntity spend) {
    KeyHolder kh = new GeneratedKeyHolder();
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)"
              + "VALUES(?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

      ps.executeUpdate();
      return ps;
    }, kh);
    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
  }

  @Nonnull
  @Override
  public SpendEntity update(@Nonnull SpendEntity spend) {
    String sql = """
        UPDATE "user"
        SET username = ?,
            spend_date = ?,
            currency = ?,
            amount = ?,
            description = ?,
            category_id = ?
        WHERE id = ?
        """;
    getJdbcTemplate().update(
        sql,
        spend.getUsername(),
        spend.getSpendDate(),
        spend.getCurrency().name(),
        spend.getDescription(),
        spend.getCategory().getId(),
        spend.getId()
    );
    return spend;
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(@Nonnull UUID id) {
    return Optional.ofNullable(
        getJdbcTemplate().queryForObject(
            getSelectByWhereIs("id"),
            SpendEntityRowMapper.INSTANCE,
            id
        )
    );
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAllByUsername(@Nonnull String username) {
    return getJdbcTemplate()
        .query(
            getSelectByWhereIs("username"),
            SpendEntityRowMapper.INSTANCE,
            username
        );
  }

  @Nonnull
  @Override
  public List<SpendEntity> findAll() {
    return getJdbcTemplate()
        .query(
            "SELECT "
                + "s.id, s.amount, s.currency, s.description, s.spend_date, s.username, "
                + "c.id AS c_id, c.username AS c_username, c.archived AS c_archived, c.name AS c_name "
                + "FROM spend AS s "
                + "JOIN category AS c "
                + "ON s.category_id = c.id ",
            SpendEntityRowMapper.INSTANCE
        );
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
    return Optional.ofNullable(
        getJdbcTemplate().queryForObject(
            sql,
            SpendEntityRowMapper.INSTANCE,
            username,
            description
        )
    );
  }

  @Override
  public void remove(@Nonnull SpendEntity spend) {
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "DELETE FROM spend WHERE id = ?"
      );
      ps.setObject(1, spend.getId());
      return ps;
    });
  }

  @Nonnull
  private JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
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
