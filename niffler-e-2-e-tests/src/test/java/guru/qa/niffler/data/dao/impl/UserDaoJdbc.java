package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public class UserDaoJdbc implements UserDao {

  private final Config CFG = Config.getInstance();

  @Override
  public @Nonnull UserEntity create(@Nonnull UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO \"user\" (currency, firstname, full_name, photo, photo_small, surname, username)"
              + "VALUES(?, ?, ?, ?, ?, ?, ?)"
      )) {
        ps.setString(1, user.getCurrency().name());
        ps.setString(2, user.getFirstname());
        ps.setString(3, user.getFullname());
        ps.setBytes(4, user.getPhoto());
        ps.setBytes(5, user.getPhotoSmall());
        ps.setString(6, user.getSurname());
        ps.setString(7, user.getUsername());

        ps.executeUpdate();

        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            return collectEntityFrom(rs);
          } else {
            throw new SQLException("Can't find id in ResultSet");
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          getSelectByWhereIs("id")
      )) {
        ps.setObject(1, id);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          return rs.next()
              ? Optional.of(collectEntityFrom(rs))
              : Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          getSelectByWhereIs("username")
      )) {
        ps.setObject(1, username);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          return rs.next()
              ? Optional.of(collectEntityFrom(rs))
              : Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(@Nonnull UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM \"user\" WHERE id = ?"
      )) {
        ps.setObject(1, user.getId());
        ps.execute();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nonnull UserEntity collectEntityFrom(@Nonnull ResultSet rs) throws SQLException {
    UserEntity ue = new UserEntity();
    ue.setId(rs.getObject("id", UUID.class));
    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    ue.setFirstname(rs.getString("firstname"));
    ue.setFullname(rs.getString("full_name"));
    ue.setPhoto(rs.getBytes("photo"));
    ue.setPhotoSmall(rs.getBytes("photo_small"));
    ue.setSurname(rs.getString("surname"));
    ue.setUsername(rs.getString("username"));
    return ue;
  }

  private @Nonnull String getSelectByWhereIs(@Nonnull String key) {
    return "SELECT * FROM \"user\" WHERE %s = ?".formatted(key);
  }
}
