package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class UdUserDaoSpringJdbc implements UdUserDao {

  private final DataSource dataSource;

  public UdUserDaoSpringJdbc(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Nonnull
  @Override
  public UserEntity create(@Nonnull UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
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
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM \"user\" WHERE id = ?",
            UdUserEntityRowMapper.INSTANCE,
            id
        )
    );
  }
}
