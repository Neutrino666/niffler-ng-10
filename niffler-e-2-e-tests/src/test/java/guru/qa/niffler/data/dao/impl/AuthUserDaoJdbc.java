package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc implements AuthUserDao {

  private final static Config CFG = Config.getInstance();

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "INSERT INTO \"user\" "
            + "(username, password, enabled, "
            + "account_non_expired, account_non_locked, credentials_non_expired)"
            + "VALUES(?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return AuthUserEntityRowMapper.INSTANCE.mapRow(rs, 1);
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
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "SELECT * FROM \"user\" WHERE id = ?")
    ) {
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

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "SELECT "
            + "u.*, "
            + "a.id AS a_id, a.user_id, a.authority "
            + "FROM \"user\" AS u "
            + "JOIN authority AS a "
            + "ON u.id = a.user_id "
            + "WHERE username = ?")
    ) {
      ps.setObject(1, username);
      ps.execute();
      Optional<AuthUserEntity> result = Optional.empty();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          AuthUserEntity user = collectEntity(rs);
          if (result.isEmpty()) {
            result = Optional.of(user);
          } else {
            result.ifPresent(authUserEntity -> authUserEntity.getAuthorities()
                .addAll(user.getAuthorities()));
          }
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
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
    try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      ps.setObject(7, user.getId());
      ps.executeUpdate();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(AuthUserEntity user) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nonnull AuthUserEntity collectEntity(ResultSet rs)
      throws SQLException {
    AuthUserEntity user = new AuthUserEntity();
    user.setId(rs.getObject("id", UUID.class));
    user.setUsername(rs.getString("username"));
    user.setPassword(rs.getString("password"));
    user.setEnabled(rs.getBoolean("enabled"));
    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    user.setAuthorities(new ArrayList<>());
    if (rs.getObject("a_id") != null) {
      AuthAuthorityEntity ae = new AuthAuthorityEntity();
      ae.setUser(new AuthUserEntity());
      ae.setId(rs.getObject("a_id", UUID.class));
      ae.setAuthority(Authority.valueOf(rs.getString("authority")));
      user.getAuthorities().add(ae);
    }
    return user;
  }

  @Nonnull
  private Connection getConnection() {
    return holder(CFG.authJdbcUrl()).connection();
  }
}
