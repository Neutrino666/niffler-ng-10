package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.SpendEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

  public static final SpendEntityRowMapper INSTANCE = new SpendEntityRowMapper();

  @Override
  public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    return SpendDaoJdbc.collectEntity(rs).get();
  }
}
