package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class AuthUserSetExtractor implements ResultSetExtractor<AuthUserEntity> {

  public static final AuthUserSetExtractor INSTANCE = new AuthUserSetExtractor();

  @Override
  public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
    UUID userId = null;
    while (rs.next()) {
      userId = rs.getObject("id", UUID.class);
      AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
        AuthUserEntity result = new AuthUserEntity();
        try {
          result.setId(rs.getObject("id", UUID.class));
          result.setUsername(rs.getString("username"));
          result.setPassword(rs.getString("password"));
          result.setEnabled(rs.getBoolean("enabled"));
          result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
        return result;
      });
      AuthAuthorityEntity authority = new AuthAuthorityEntity();
      authority.setId(rs.getObject("a_id", UUID.class));
      authority.setAuthority(Authority.valueOf(rs.getString("authority")));
      user.getAuthorities().add(authority);
    }
    return userMap.get(userId);
  }
}
