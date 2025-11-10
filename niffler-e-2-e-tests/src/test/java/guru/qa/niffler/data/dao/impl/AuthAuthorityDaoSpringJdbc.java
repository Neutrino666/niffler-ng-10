package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

  private final DataSource dataSource;

  public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Nonnull
  @Override
  public AuthAuthorityEntity create(@Nonnull AuthAuthorityEntity authorities) {
    throw new RuntimeException("Not implemented");
  }

  @Nonnull
  public AuthAuthorityEntity create(@Nonnull AuthAuthorityEntity... authority) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.batchUpdate(
        "INSERT INTO authority (user_id, authority) VALUES(?, ?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, authority[i].getUser().getId());
            ps.setString(2, authority[i].getAuthority().name());
          }

          @Override
          public int getBatchSize() {
            return authority.length;
          }
        }
    );
    return null;
  }

  @Nonnull
  @Override
  public List<AuthAuthorityEntity> findAllByUserId(@Nonnull UUID category) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void delete(@Nonnull AuthAuthorityEntity category) {
    throw new RuntimeException("Not implemented");
  }
}
