package guru.qa.niffler.jupiter.extension.allure;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.helpers.AnnotationUtils;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDiff;
import io.qameta.allure.Allure;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

@ParametersAreNonnullByDefault
public final class ScreenShotTestExtension implements
    ParameterResolver,
    TestExecutionExceptionHandler,
    AfterEachCallback {

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
        new ClassPathResource(
            AnnotationUtils.findTestMethodAnnotation(ScreenShotTest.class)
                .orElseThrow()
                .value()
        )
            .getInputStream()
    );
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    if (throwable.getMessage().contains("Screen comparison failure")) {
      addAttachment();
    }
    throw throwable;
  }

  public static void storeSet(Type type, BufferedImage image) {
    context().getStore(NAMESPACE)
        .put(type, image);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    AnnotationUtils.findTestMethodAnnotation(ScreenShotTest.class)
        .ifPresent(anno -> {
              if (anno.rewriteExpected()) {
                updateResourceFile(anno.value());
              }
            }
        );
  }

  private void addAttachment() throws Throwable {
    ScreenDiff screenDiff = new ScreenDiff(
        "data:image/png;base64," + Base64.getEncoder()
            .encodeToString(
                imageToBytes(
                    storeGet(Type.EXPECTED)
                )
            ),
        "data:image/png;base64," + Base64.getEncoder()
            .encodeToString(
                imageToBytes(
                    storeGet(Type.ACTUAL)
                )
            ),
        "data:image/png;base64," + Base64.getEncoder()
            .encodeToString(
                imageToBytes(
                    storeGet(Type.DIFF)
                )
            )
    );
    Allure.addAttachment(
        "Screenshot diff",
        "application/vnd.allure.image.diff",
        objectMapper.writeValueAsString(screenDiff)
    );
  }

  private void updateResourceFile(String path) {
    File file = new File("src/test/resources/" + path);
    BufferedImage actual = storeGet(Type.ACTUAL);
    try {
      ImageIO.write(actual, "png", file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private BufferedImage storeGet(Type type) {
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
