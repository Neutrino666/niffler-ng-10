package guru.qa.niffler.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GithubToken")
public class CheckGithubTokenTest {

  @Test
  @DisplayName("Exist token")
  void gitHubTokenExistInDocker() {
    Assertions.assertThat(System.getenv("GITHUB_TOKEN"))
        .isNotEmpty()
        .hasSizeGreaterThan(1);
  }
}
