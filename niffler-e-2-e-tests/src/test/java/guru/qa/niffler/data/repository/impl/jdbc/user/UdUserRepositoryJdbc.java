package guru.qa.niffler.data.repository.impl.jdbc.user;

import static guru.qa.niffler.data.tpl.Connections.holder;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UdUserRepositoryJdbc implements UserdataUserRepository {

  private final static Config CFG = Config.getInstance();
  private final UserDao userDao = new UdUserDaoJdbc();

  @Override
  public @Nonnull UserEntity create(UserEntity user) {
    try (PreparedStatement userPs = getConnection().prepareStatement(
        "INSERT INTO \"user\" (currency, firstname, full_name, photo, photo_small, surname, username)"
            + "VALUES(?, ?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS)) {
      userPs.setString(1, user.getCurrency().name());
      userPs.setString(2, user.getFirstname());
      userPs.setString(3, user.getFullname());
      userPs.setBytes(4, user.getPhoto());
      userPs.setBytes(5, user.getPhotoSmall());
      userPs.setString(6, user.getSurname());
      userPs.setString(7, user.getUsername());

      userPs.executeUpdate();

      try (ResultSet rs = userPs.getGeneratedKeys()) {
        if (rs.next()) {
          return UdUserEntityRowMapper.INSTANCE.mapRow(rs, 1);
        } else {
          throw new SQLException("Can't find id in ResultSet");
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nonnull Optional<UserEntity> findById(UUID id) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("id")
    )) {
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
  public @Nonnull Optional<UserEntity> findByUsername(String username) {
    try (PreparedStatement ps = getConnection().prepareStatement(
        getSelectByWhereIs("username")
    )) {
      ps.setObject(1, username);
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
  public UserEntity update(UserEntity user) {
    return userDao.update(user);
  }

  @Override
  public void sendInvitation(UserEntity requester, UserEntity addressee) {
    addFriendshipRow(requester, addressee, FriendshipStatus.PENDING);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    addFriendshipRow(addressee, requester, FriendshipStatus.ACCEPTED);
    addFriendshipRow(requester, addressee, FriendshipStatus.ACCEPTED);
  }

  @Override
  public void remove(UserEntity user) {
    try (PreparedStatement userPs = getConnection().prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?"
    );
        PreparedStatement friendshipPs = getConnection().prepareStatement(
            "DELETE FROM friendship "
                + "WHERE addressee_id = ? "
                + "OR requester_id = ?"
        )
    ) {
      friendshipPs.setObject(1, user.getId());
      friendshipPs.setObject(2, user.getId());
      friendshipPs.executeUpdate();

      userPs.setObject(1, user.getId());
      userPs.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  private UserEntity collectEntityWithFriends(ResultSet rs) throws SQLException {
    Map<UUID, UserEntity> userMap = new ConcurrentHashMap<>();
    UUID userId = null;

    while (rs.next()) {
      userId = rs.getObject("id", UUID.class);
      UserEntity user = userMap.computeIfAbsent(userId, id -> collectEntity(rs));
      FriendshipEntity friendship = new FriendshipEntity();
      UUID requesterId = rs.getObject("requester_id", UUID.class);
      UUID addresseeId = rs.getObject("addressee_id", UUID.class);
      UserEntity requester = new UserEntity();
      UserEntity addressee = new UserEntity();
      String status = rs.getString("status");

      if (requesterId != null) {
        requester.setId(requesterId);
        friendship.setRequester(requester);
      }
      if (addresseeId != null) {
        addressee.setId(addresseeId);
        friendship.setAddressee(addressee);
      }
      if (status != null) {
        friendship.setStatus(FriendshipStatus.valueOf(status));
      }

      if (requesterId != null && requesterId.equals(user.getId())) {
        user.getFriendshipAddressees().add(friendship);
      } else if (addresseeId != null && addresseeId.equals(user.getId())) {
        user.getFriendshipRequests().add(friendship);
      }
    }
    return userMap.get(userId);
  }

  @Nonnull
  private UserEntity collectEntity(ResultSet rs) {
    UserEntity result = new UserEntity();
    try {
      result.setId(rs.getObject("id", UUID.class));
      result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
      result.setFirstname(rs.getString("firstname"));
      result.setFullname(rs.getString("full_name"));
      result.setPhoto(rs.getBytes("photo"));
      result.setPhotoSmall(rs.getBytes("photo_small"));
      result.setSurname(rs.getString("surname"));
      result.setUsername(rs.getString("username"));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  private void addFriendshipRow(UserEntity requester, UserEntity addressee, FriendshipStatus status
  ) {
    String sql = "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
        "VALUES (?, ?, ?, ?) " +
        "ON CONFLICT (requester_id, addressee_id) " +
        "DO UPDATE SET status = ? ";
    try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
      ps.setObject(1, requester.getId());
      ps.setObject(2, addressee.getId());
      ps.setString(3, status.name());
      ps.setDate(4, Date.valueOf(LocalDate.now()));
      ps.setString(5, status.name());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private @Nonnull String getSelectByWhereIs(String key) {
    return "SELECT * FROM \"user\" AS u "
        + "LEFT JOIN friendship AS f "
        + "ON u.id = f.addressee_id "
        + "OR u.id = f.requester_id "
        + "WHERE %s = ?".formatted(key);
  }

  private @Nonnull Connection getConnection() {
    return holder(CFG.userdataJdbcUrl()).connection();
  }
}
