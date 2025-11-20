package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.tpl.Connections.holder;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public AuthUserEntity create(@Nonnull AuthUserEntity user) {
    KeyHolder kh = new GeneratedKeyHolder();
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"user\" "
              + "(username, password, enabled, "
              + "account_non_expired, account_non_locked, credentials_non_expired)"
              + "VALUES(?, ?, ?, ?, ?, ?)",
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
  public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
    return Optional.ofNullable(
        getJdbcTemplate().queryForObject(
            "SELECT * FROM \"user\" WHERE id = ?",
            AuthUserEntityRowMapper.instance,
            id
        )
    );
  }

  @Override
  public void delete(@Nonnull AuthUserEntity user) {
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
          "DELETE FROM authority WHERE id = ?"
      );
      ps.setObject(1, user.getId());
      return ps;
    });
  }

  @Nonnull
  private JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
  }
}
