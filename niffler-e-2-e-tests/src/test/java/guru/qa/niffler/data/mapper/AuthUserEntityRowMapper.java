package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

  public static final AuthUserEntityRowMapper INSTANCE = new AuthUserEntityRowMapper();

  @Override
  public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    AuthUserEntity result = new AuthUserEntity();
    result.setId(rs.getObject("id", UUID.class));
    result.setUsername(rs.getString("username"));
    result.setPassword(rs.getString("password"));
    result.setEnabled(rs.getBoolean("enabled"));
    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    if (rs.getObject("a_id") != null) {
      AuthAuthorityEntity ae = new AuthAuthorityEntity();
      ae.setUser(new AuthUserEntity());
      ae.setId(rs.getObject("a_id", UUID.class));
      ae.setAuthority(Authority.valueOf(rs.getString("authority")));
      result.getAuthorities().add(ae);
    }
    return result;
  }
}
