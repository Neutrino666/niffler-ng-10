package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

  private final static Config CFG = Config.getInstance();

  @Override
  public void create(@Nonnull AuthAuthorityEntity... authorities) {
    getJdbcTemplate().batchUpdate(
        "INSERT INTO authority (user_id, authority) "
            + "VALUES(?, ?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, authorities[i].getUser().getId());
            ps.setString(2, authorities[i].getAuthority().name());
          }

          @Override
          public int getBatchSize() {
            return authorities.length;
          }
        }
    );
  }

  @Nonnull
  @Override
  public List<AuthAuthorityEntity> findAllByUserId(@Nonnull UUID userId) {
    return getJdbcTemplate().query(
        getSelectByWhereIs("user_id"),
        AuthAuthorityEntityRowMapper.INSTANCE,
        userId
    );
  }

  @Override
  public void delete(@Nonnull AuthAuthorityEntity authority) {
    getJdbcTemplate().update("DELETE FROM authority WHERE id = ?", authority.getId());
  }

  @Nonnull
  private JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
  }

  private @Nonnull String getSelectByWhereIs(@Nonnull String key) {
    return "SELECT "
        + "a.id AS a_id, a.user_id AS a_id, a.authority AS a_authority, "
        + "u.id, u.username, u.password, u.enabled, "
        + "u.account_non_expired, u.account_non_locked, u.credentials_non_expired "
        + "FROM \"user\" AS u "
        + "JOIN authority AS a "
        + "ON u.id = a.user_id "
        + "WHERE a.%s = ?".formatted(key);
  }
}
