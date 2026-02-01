package guru.qa.niffler.jupiter.extension;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@ParametersAreNonnullByDefault
public final class TestMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

  private static final ThreadLocal<ExtensionContext> ctxStore = new ThreadLocal<>();

  @Nonnull
  public static ExtensionContext context() {
    return ctxStore.get();
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    ctxStore.set(context);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    ctxStore.remove();
  }
}
