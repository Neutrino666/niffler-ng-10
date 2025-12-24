package guru.qa.niffler.data.repository.impl.spring.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class UdUserRepositorySpringJdbc implements UserdataUserRepository {

  private final static Config CFG = Config.getInstance();
  UserDao userDao = new UdUserDaoSpringJdbc();

  @Nonnull
  @Override
  public UserEntity create(@Nonnull UserEntity user) {
    KeyHolder kh = new GeneratedKeyHolder();
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"user\" (currency, firstname, full_name, photo, photo_small, surname, username)"
              + "VALUES(?, ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getCurrency().name());
      ps.setString(2, user.getFirstname());
      ps.setString(3, user.getFullname());
      ps.setBytes(4, user.getPhoto());
      ps.setBytes(5, user.getPhotoSmall());
      ps.setString(6, user.getSurname());
      ps.setString(7, user.getUsername());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    user.setId(generatedKey);
    return user;
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findById(@Nonnull UUID id) {
    return Optional.ofNullable(
        getJdbcTemplate().query(
            getSelectByWhereIs("id"),
            UserdataSetExtractor.INSTANCE,
            id
        )
    );
  }

  @Nonnull
  @Override
  public Optional<UserEntity> findByUsername(@Nonnull String username) {
    return Optional.ofNullable(
        getJdbcTemplate().query(
            getSelectByWhereIs("username"),
            UserdataSetExtractor.INSTANCE,
            username
        )
    );
  }

  @Nonnull
  @Override
  public UserEntity update(@Nonnull UserEntity user) {
    return userDao.update(user);
  }

  @Override
  public void sendInvitation(@Nonnull UserEntity requester, UserEntity addressee) {
    addFriendshipRow(requester, addressee, FriendshipStatus.PENDING);
  }

  @Override
  public void addFriend(@Nonnull UserEntity requester, UserEntity addressee) {
    addFriendshipRow(addressee, requester, FriendshipStatus.ACCEPTED);
    addFriendshipRow(requester, addressee, FriendshipStatus.ACCEPTED);
  }

  @Override
  public void remove(@Nonnull UserEntity user) {
    getJdbcTemplate().update(con -> {
      PreparedStatement friendshipPs = con.prepareStatement(
          "DELETE FROM friendship "
              + "WHERE addressee_id = ? "
              + "OR requester_id = ?"
      );
      PreparedStatement userdataPs = con.prepareStatement(
          "DELETE FROM \"user\" WHERE id = ?"
      );
      friendshipPs.setObject(1, user.getId());
      friendshipPs.setObject(2, user.getId());
      friendshipPs.executeUpdate();
      userdataPs.setObject(1, user.getId());
      return userdataPs;
    });
  }


  private void addFriendshipRow(
      @Nonnull UserEntity requester,
      @Nonnull UserEntity addressee,
      @Nonnull FriendshipStatus status
  ) {
    String sql = "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
        "VALUES (?, ?, ?, ?) " +
        "ON CONFLICT (requester_id, addressee_id) " +
        "DO UPDATE SET status = ? ";
    getJdbcTemplate().update(con -> {
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setObject(1, requester.getId());
      ps.setObject(2, addressee.getId());
      ps.setString(3, status.name());
      ps.setDate(4, Date.valueOf(LocalDate.now()));
      ps.setString(5, status.name());
      return ps;
    });
  }

  @Nonnull
  private JdbcTemplate getJdbcTemplate() {
    return new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
  }

  private @Nonnull String getSelectByWhereIs(@Nonnull String key) {
    return "SELECT * FROM \"user\" AS u "
        + "LEFT JOIN friendship AS f "
        + "ON u.id = f.addressee_id "
        + "OR u.id = f.requester_id "
        + "WHERE %s = ?".formatted(key);
  }
}
