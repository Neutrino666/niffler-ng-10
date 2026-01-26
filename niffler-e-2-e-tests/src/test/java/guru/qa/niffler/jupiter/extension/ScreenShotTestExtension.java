package guru.qa.niffler.jupiter.extension;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDiff;
import io.qameta.allure.Allure;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

@ParametersAreNonnullByDefault
public class ScreenShotTestExtension implements
    ParameterResolver,
    TestExecutionExceptionHandler {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      ScreenShotTestExtension.class);

  public static final ObjectMapper objectMapper = new ObjectMapper();

  @Getter
  @RequiredArgsConstructor
  public enum Type {
    EXPECTED("expected"),
    ACTUAL("actual"),
    DIFF("diff");

    @Nonnull
    private final String value;
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return AnnotationSupport
        .isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class)
        && parameterContext.getParameter().getType()
        .isAssignableFrom(BufferedImage.class);
  }

  @Override
  @SneakyThrows
  public @Nonnull BufferedImage resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return ImageIO.read(
        new ClassPathResource("img/expected-stat.png")
            .getInputStream()
    );
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    ScreenDiff screenDiff = new ScreenDiff(
        "data:image/png;base64," + Base64.getEncoder()
            .encodeToString(
                imageToBytes(
                    get(Type.EXPECTED)
                )
            ),
        "data:image/png;base64," + Base64.getEncoder()
            .encodeToString(
                imageToBytes(
                    get(Type.ACTUAL)
                )
            ),
        "data:image/png;base64," + Base64.getEncoder()
            .encodeToString(
                imageToBytes(
                    get(Type.DIFF)
                )
            )
    );
    Allure.addAttachment(
        "Screenshot diff",
        "application/vnd.allure.image.diff",
        objectMapper.writeValueAsString(screenDiff)
    );
    throw throwable;
  }

  public static void set(Type type, BufferedImage image) {
    context().getStore(NAMESPACE)
        .put(type, image);
  }

  public BufferedImage get(Type type) {
    return context().getStore(NAMESPACE)
        .get(type, BufferedImage.class);
  }

  private static byte[] imageToBytes(BufferedImage image) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
