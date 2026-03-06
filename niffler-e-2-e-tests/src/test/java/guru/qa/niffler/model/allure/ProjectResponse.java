package guru.qa.niffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record ProjectResponse(
    Projects data,
    @JsonProperty("meta_data")
    ProjectMetadata metadata
) {

}
