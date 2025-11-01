package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

public class AuthUserDaoJdbc implements AuthUserDao {

  private final Connection connection;

  public AuthUserDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @NotNull
  @Override
  public AuthUserEntity create(@NotNull AuthUserEntity user) {
    try (PreparedStatement ps = connection.prepareStatement(
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
          return collectEntity(rs);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  public Optional<AuthUserEntity> findById(@NotNull UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
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

  @Override
  public void delete(@NotNull AuthUserEntity user) {
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nonnull AuthUserEntity collectEntity(@Nonnull ResultSet rs)
      throws SQLException {
    AuthUserEntity user = new AuthUserEntity();
    user.setId(rs.getObject("id", UUID.class));
    user.setUsername(rs.getString("username"));
    user.setPassword(rs.getString("password"));
    user.setEnabled(rs.getBoolean("enabled"));
    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    user.setAuthorities(List.of());
    return user;
  }
}
