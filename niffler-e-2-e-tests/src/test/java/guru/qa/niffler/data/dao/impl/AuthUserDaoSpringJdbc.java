package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@ParametersAreNonnullByDefault
public class AuthUserDaoSpringJdbc implements AuthUserDao {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    String sql = "INSERT INTO \"user\" "
        + "(username, password, enabled, "
        + "account_non_expired, account_non_locked, credentials_non_expired)"
        + "VALUES(?, ?, ?, ?, ?, ?)";
    KeyHolder kh = new GeneratedKeyHolder();
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = con.prepareStatement(
          sql,
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    user.setId(generatedKey);
    return user;
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    return Optional.ofNullable(
        getJdbcTemplate().queryForObject(
            "SELECT * FROM \"user\" WHERE id = ?",
            AuthUserEntityRowMapper.INSTANCE,
            id
        )
    );
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    String sql = "SELECT "
        + "u.*, "
        + "a.id AS a_id, a.user_id, a.authority "
        + "FROM \"user\" AS u "
        + "JOIN authority AS a "
        + "ON u.id = a.user_id "
        + "WHERE username = ?";
    return Optional.ofNullable(
        getJdbcTemplate().queryForObject(
            sql,
            AuthUserEntityRowMapper.INSTANCE,
            username
        )
    );
  }

  @Nonnull
  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    String sql = """
        UPDATE "user"
        SET username = ?,
            password = ?,
            enabled = ?,
            account_non_expired = ?,
            account_non_locked = ?,
            credentials_non_expired = ?
        WHERE id = ?
        """;
    getJdbcTemplate().update(
        sql,
        user.getUsername(),
        user.getPassword(),
        user.getEnabled(),
        user.getAccountNonExpired(),
        user.getAccountNonLocked(),
        user.getCredentialsNonExpired(),
        user.getId()
    );
    return user;
  }

  @Override
  public void delete(AuthUserEntity user) {
    getJdbcTemplate().update("DELETE FROM \"user\" WHERE id = ?", user.getId());
  }

  @Nonnull
  private JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
  }
}
