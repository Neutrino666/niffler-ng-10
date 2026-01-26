package guru.qa.niffler.helpers;

import guru.qa.niffler.jupiter.extension.allure.ScreenShotTestExtension;
import guru.qa.niffler.jupiter.extension.allure.ScreenShotTestExtension.Type;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

@ParametersAreNonnullByDefault
public final class ScreenDiffResult implements BooleanSupplier {

  private final BufferedImage expected;
  private final BufferedImage actual;
  private final ImageDiff diff;
  private final boolean hasDiff;
  @Getter
  private static final Integer ALLOWED_DIFF_PIXELS = 300;

  public ScreenDiffResult(BufferedImage expected, BufferedImage actual) {
    this.expected = expected;
    this.actual = actual;
    this.diff = new ImageDiffer().makeDiff(expected, actual);
    this.hasDiff = diff.getDiffSize() > ALLOWED_DIFF_PIXELS;
  }

  @Override
  public boolean getAsBoolean() {
    if (hasDiff) {
      ScreenShotTestExtension.storeSet(Type.EXPECTED, expected);
      ScreenShotTestExtension.storeSet(Type.ACTUAL, actual);
      ScreenShotTestExtension.storeSet(Type.DIFF, diff.getMarkedImage());
    }
    return hasDiff;
  }
}
