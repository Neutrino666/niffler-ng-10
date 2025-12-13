package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.data.mapper.UserdataSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class UdUserRepositorySpringJdbc implements UserdataUserRepository {

  private final static Config CFG = Config.getInstance();

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
        getJdbcTemplate().queryForObject(
            getSelectByWhereIs("id"),
            UdUserEntityRowMapper.INSTANCE,
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

  @Override
  public void delete(@Nonnull UserEntity user) {
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
