package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CategoryJson;
import java.io.Serializable;
import java.util.UUID;
import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class CategoryEntity implements Serializable {

  private UUID id;
  private String name;
  private String username;
  private boolean archived;

  public static @Nonnull CategoryEntity fromJson(@Nonnull CategoryJson json) {
    CategoryEntity ce = new CategoryEntity();
    ce.setId(json.id());
    ce.setName(json.name());
    ce.setUsername(json.username());
    ce.setArchived(json.archived());
    return ce;
  }
}
