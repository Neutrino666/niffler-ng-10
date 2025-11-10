package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

  public static final CategoryEntityRowMapper INSTANCE = new CategoryEntityRowMapper();

  @Override
  public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    return CategoryDaoJdbc.collectEntity(rs);
  }
}
