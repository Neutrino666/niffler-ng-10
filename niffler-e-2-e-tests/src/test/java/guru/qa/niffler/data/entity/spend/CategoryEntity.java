package guru.qa.niffler.data.entity.spend;

import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryEntity implements Serializable {

  private UUID id;
  private String name;
  private String username;
  private boolean archived;
}
