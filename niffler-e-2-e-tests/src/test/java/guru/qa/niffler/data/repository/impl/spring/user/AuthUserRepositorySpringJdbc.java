package guru.qa.niffler.data.repository.impl.spring.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public AuthUserEntity create(@Nonnull AuthUserEntity user) {
    KeyHolder kh = new GeneratedKeyHolder();
    getJdbcTemplate().update(con -> {
      PreparedStatement userPs = con.prepareStatement(
          "INSERT INTO \"user\" "
              + "(username, password, enabled, "
              + "account_non_expired, account_non_locked, credentials_non_expired)"
              + "VALUES(?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      );
      userPs.setString(1, user.getUsername());
      userPs.setString(2, user.getPassword());
      userPs.setBoolean(3, user.getEnabled());
      userPs.setBoolean(4, user.getAccountNonExpired());
      userPs.setBoolean(5, user.getAccountNonLocked());
      userPs.setBoolean(6, user.getCredentialsNonExpired());

      return userPs;
    }, kh);
    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    user.setId(generatedKey);

    List<AuthAuthorityEntity> authorities = user.getAuthorities();
    getJdbcTemplate().batchUpdate(
        "INSERT INTO authority (user_id, authority) "
            + "VALUES(?, ?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, authorities.get(i).getUser().getId());
            ps.setString(2, authorities.get(i).getAuthority().name());
          }

          @Override
          public int getBatchSize() {
            return authorities.size();
          }
        }
    );
    return user;
  }

  @Nonnull
  @Override
  public AuthUserEntity update(@Nonnull AuthUserEntity u) {
    String sql = """
        UPDATE "user"
        SET id = ?,
            username = ?,
            password = ?,
            enabled = ?,
            account_non_expired = ?,
            account_non_locked = ?,
            credentials_non_expired = ?
        WHERE id = ?
        """;
    getJdbcTemplate().update(
        sql,
        u.getId(),
        u.getUsername(),
        u.getPassword(),
        u.getEnabled(),
        u.getAccountNonExpired(),
        u.getAccountNonLocked(),
        u.getCredentialsNonExpired(),
        u.getId()
    );
    return u;
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
    return Optional.ofNullable(
        getJdbcTemplate().query(
            getSelectByWhereIs("id"),
            AuthUserSetExtractor.INSTANCE,
            id
        ));
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
    return Optional.ofNullable(
        getJdbcTemplate().query(
            getSelectByWhereIs("username"),
            AuthUserSetExtractor.INSTANCE,
            username
        )
    );
  }

  @Override
  public void remove(@Nonnull AuthUserEntity user) {
    getJdbcTemplate().update("DELETE FROM authority WHERE user_id = ?", user.getId());
    getJdbcTemplate().update("DELETE FROM \"user\" WHERE id = ?", user.getId());
  }

  @Nonnull
  private JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
  }

  private @Nonnull String getSelectByWhereIs(@Nonnull String key) {
    return "SELECT "
        + "u.*, "
        + "a.id AS a_id, a.user_id, a.authority "
        + "FROM \"user\" AS u "
        + "JOIN authority AS a "
        + "ON u.id = a.user_id "
        + "WHERE %s = ?".formatted(key);
  }
}
