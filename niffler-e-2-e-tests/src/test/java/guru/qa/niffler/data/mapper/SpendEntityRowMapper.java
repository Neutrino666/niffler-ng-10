package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

  public static final SpendEntityRowMapper INSTANCE = new SpendEntityRowMapper();

  @Override
  public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    CategoryEntity categoryEntity = new CategoryEntity();
    if (rs.getObject("c_id") != null) {
      categoryEntity.setId(rs.getObject("c_id", UUID.class));
      categoryEntity.setName(rs.getString("c_name"));
      categoryEntity.setUsername(rs.getString("c_username"));
      categoryEntity.setArchived(rs.getBoolean("c_archived"));
    }
    SpendEntity spend = new SpendEntity();
    spend.setId(rs.getObject("id", UUID.class));
    spend.setSpendDate(rs.getDate("spend_date"));
    spend.setCategory(categoryEntity);
    spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    spend.setAmount(rs.getDouble("amount"));
    spend.setDescription(rs.getString("description"));
    spend.setUsername(rs.getString("username"));
    return spend;
  }
}
