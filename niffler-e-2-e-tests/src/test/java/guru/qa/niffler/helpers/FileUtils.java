package guru.qa.niffler.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class FileUtils {

  public static List<Map<String, String>> getAllureResults() {
    String allureResultsPath = "build/allure-results";
    File directory = new File(allureResultsPath);
    File[] files = directory.listFiles();
    List<Map<String, String>> results = new ArrayList<>();
    assert files != null;
    for (File file : files) {
      if (file.isFile()) {
        try {
          BufferedReader br = new BufferedReader(new FileReader(file));
          StringBuilder stringBuilder = new StringBuilder();
          br.lines().toList().forEach(stringBuilder::append);
          String content = stringBuilder.toString().trim();
          String base64Content = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
          results.add(
              Map.of(
                  "file_name", file.getName(),
                  "content_base64", base64Content
              )
          );
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return results;
  }
}
