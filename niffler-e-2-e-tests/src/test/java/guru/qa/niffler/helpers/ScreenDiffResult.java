package guru.qa.niffler.helpers;

import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension.Type;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

public class ScreenDiffResult implements BooleanSupplier {

  private final BufferedImage expected;
  private final BufferedImage actual;
  private final ImageDiff diff;
  private final boolean hasDiff;

  public ScreenDiffResult(BufferedImage expected, BufferedImage actual) {
    this.expected = expected;
    this.actual = actual;
    this.diff = new ImageDiffer().makeDiff(expected, actual);
    this.hasDiff = diff.hasDiff();
  }

  @Override
  public boolean getAsBoolean() {
    if (hasDiff) {
      ScreenShotTestExtension.set(Type.EXPECTED, expected);
      ScreenShotTestExtension.set(Type.ACTUAL, actual);
      ScreenShotTestExtension.set(Type.DIFF, diff.getMarkedImage());
    }
    return hasDiff;
  }
}
