package guru.qa.niffler.data.repository.impl.jdbc.user;

import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

  private final static Config CFG = Config.getInstance();
  private final AuthUserDao authUserDao = new AuthUserDaoJdbc();

  @Nonnull
  @Override
  public AuthUserEntity create(@Nonnull AuthUserEntity user) {
    try (PreparedStatement userPs = getConnection().prepareStatement(
        "INSERT INTO \"user\" "
            + "(username, password, enabled, "
            + "account_non_expired, account_non_locked, credentials_non_expired)"
            + "VALUES(?, ?, ?, ?, ?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS);
        PreparedStatement authorityPs = getConnection().prepareStatement(
            "INSERT INTO authority (user_id, authority)"
                + "VALUES(?, ?)"
        )) {
      userPs.setString(1, user.getUsername());
      userPs.setString(2, user.getPassword());
      userPs.setBoolean(3, user.getEnabled());
      userPs.setBoolean(4, user.getAccountNonExpired());
      userPs.setBoolean(5, user.getAccountNonLocked());
      userPs.setBoolean(6, user.getCredentialsNonExpired());

      userPs.executeUpdate();

      try (ResultSet rs = userPs.getGeneratedKeys()) {
        if (rs.next()) {
          user.setId(rs.getObject("id", UUID.class));
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
      }

      for (AuthAuthorityEntity authority : user.getAuthorities()) {
        authorityPs.setObject(1, user.getId());
        authorityPs.setString(2, authority.getAuthority().name());
        authorityPs.addBatch();
        authorityPs.clearParameters();
      }
      authorityPs.executeBatch();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public AuthUserEntity update(@Nonnull AuthUserEntity user) {
    return authUserDao.update(user);
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("id"))
    ) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        AuthUserEntity user = null;
        List<AuthAuthorityEntity> authorityEntities = new ArrayList<>();
        while (rs.next()) {
          if (user == null) {
            user = AuthUserEntityRowMapper.INSTANCE.mapRow(rs, 1);
          }
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setUser(user);
          ae.setAuthority(Authority.valueOf(rs.getString("authority")));
          ae.setId(rs.getObject("id", UUID.class));
          authorityEntities.add(ae);
        }
        if (user == null) {
          return Optional.empty();
        } else {
          user.setAuthorities(authorityEntities);
          return Optional.of(user);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("username"))
    ) {
      ps.setObject(1, username);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        AuthUserEntity user = null;
        List<AuthAuthorityEntity> authorityEntities = new ArrayList<>();
        while (rs.next()) {
          if (user == null) {
            user = AuthUserEntityRowMapper.INSTANCE.mapRow(rs, 1);
          }
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setUser(user);
          ae.setAuthority(Authority.valueOf(rs.getString("authority")));
          ae.setId(rs.getObject("id", UUID.class));
          authorityEntities.add(ae);
        }
        if (user == null) {
          return Optional.empty();
        } else {
          user.setAuthorities(authorityEntities);
          return Optional.of(user);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(@Nonnull AuthUserEntity user) {
    try (PreparedStatement authorityPs = getConnection().prepareStatement(
        "DELETE FROM authority WHERE user_id = ?");
        PreparedStatement userPs = getConnection().prepareStatement(
            "DELETE FROM \"user\" WHERE id = ?"
        )) {
      authorityPs.setObject(1, user.getId());
      authorityPs.execute();
      userPs.setObject(1, user.getId());
      userPs.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Connection getConnection() {
    return holder(CFG.authJdbcUrl()).connection();
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
