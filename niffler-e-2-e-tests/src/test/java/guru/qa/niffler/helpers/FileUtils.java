package guru.qa.niffler.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileUtils {

  private static final Base64.Encoder encoder = Base64.getEncoder();

  public static List<Map<String, String>> getAllureResults() {
    String allureResultsPath = "./niffler-e-2-e-tests/build/allure-results";
    List<Map<String, String>> results = new ArrayList<>();

    try (Stream<Path> paths = Files.walk(Path.of(allureResultsPath)).filter(Files::isRegularFile)) {
      for (Path path : paths.toList()) {
        try (InputStream is = Files.newInputStream(path)) {
          results.add(
              Map.of(
                  "file_name", path.getFileName().toString(),
                  "content_base64", encoder.encodeToString(
                      is.readAllBytes()
                  )
              )
          );
        } catch (IOException e) {
          throw new RuntimeException("Error reading file: " + path.getFileName().toString(), e);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Directory is empty or does not exist: " + allureResultsPath, e);
    }
    return results;
  }
}
