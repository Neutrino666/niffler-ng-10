package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UdUserEntityRowMapper implements RowMapper<UserEntity> {

  public static final UdUserEntityRowMapper instance = new UdUserEntityRowMapper();

  @Override
  public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    UserEntity result = new UserEntity();
    result.setId(rs.getObject("id", UUID.class));
    result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    result.setFirstname(rs.getString("firstname"));
    result.setFullname(rs.getString("full_name"));
    result.setPhoto(rs.getBytes("photo"));
    result.setPhotoSmall(rs.getBytes("photo_small"));
    result.setSurname(rs.getString("surname"));
    result.setUsername(rs.getString("username"));
    return result;
  }
}
