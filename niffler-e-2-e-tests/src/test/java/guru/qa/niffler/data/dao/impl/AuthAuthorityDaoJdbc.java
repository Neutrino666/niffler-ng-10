package guru.qa.niffler.data.dao.impl;

import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

  private final static Config CFG = Config.getInstance();

  @Override
  public void create(@Nonnull AuthAuthorityEntity... authorities) {
    for (AuthAuthorityEntity authority : authorities) {
      try (PreparedStatement ps = getConnection().prepareStatement(
          "INSERT INTO authority (user_id, authority)"
              + "VALUES(?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setObject(1, authority.getUser().getId());
        ps.setString(2, authority.getAuthority().name());

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            AuthAuthorityEntity ae = new AuthAuthorityEntity();
            ae.setAuthority(Authority.valueOf(rs.getString("authority")));
            ae.setId(rs.getObject("id", UUID.class));
            AuthUserEntity au = new AuthUserEntity();
            au.setId(rs.getObject("user_id", UUID.class));
            ae.setUser(au);
          } else {
            throw new SQLException("Can't find id in ResultSet");
          }
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Nonnull
  @Override
  public List<AuthAuthorityEntity> findAllByUserId(@Nonnull UUID userId) {
    List<AuthAuthorityEntity> authorities = new ArrayList<>();

    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("user_id"))
    ) {
      ps.setObject(1, userId);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          authorities.add(collectEntity(rs));
        }
        return authorities;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(@Nonnull AuthAuthorityEntity authority) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        "DELETE FROM authority WHERE id = ?"
    )) {
      ps.setObject(1, authority.getId());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nonnull AuthAuthorityEntity collectEntity(@Nonnull ResultSet rs)
      throws SQLException {
    AuthUserEntity user = new AuthUserEntity();
    if (rs.getObject("id") != null) {
      user.setId(rs.getObject("id", UUID.class));
      user.setUsername(rs.getString("username"));
      user.setPassword(rs.getString("password"));
      user.setEnabled(rs.getBoolean("enabled"));
      user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
      user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
      user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
      user.setAuthorities(List.of());
    }
    AuthAuthorityEntity ae = new AuthAuthorityEntity();
    ae.setId(rs.getObject("a_id", UUID.class));
    ae.setAuthority(Authority.valueOf(rs.getString("a_authority")));
    ae.setUser(user);
    return ae;
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

  private Connection getConnection() {
    return holder(CFG.authJdbcUrl()).connection();
  }
}
