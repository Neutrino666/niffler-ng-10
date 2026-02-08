package guru.qa.niffler.api.core;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public enum ThreadSafeCookieStore implements CookieStore {
  INSTANCE;

  private final ThreadLocal<CookieStore> cs = ThreadLocal.withInitial(
      ThreadSafeCookieStore::inMemoryCookieStore
  );

  public @Nonnull String value(final String name) {
    return cs.get().getCookies()
        .stream()
        .filter(c -> c.getName().equals(name))
        .findFirst()
        .orElseThrow()
        .getValue();
  }

  @Override
  public void add(final URI uri, final HttpCookie cookie) {
    cs.get().add(uri, cookie);
  }

  @Override
  public @Nonnull List<HttpCookie> get(final URI uri) {
    return cs.get().get(uri);
  }

  @Override
  public @Nonnull List<HttpCookie> getCookies() {
    return cs.get().getCookies();
  }

  @Override
  public @Nonnull List<URI> getURIs() {
    return cs.get().getURIs();
  }

  @Override
  public boolean remove(final URI uri, final HttpCookie cookie) {
    return cs.get().remove(uri, cookie);
  }

  @Override
  public boolean removeAll() {
    return cs.get().removeAll();
  }

  @Nonnull
  public String xsrfCookie() {
    return cs.get().getCookies()
        .stream()
        .filter(c -> c.getName().equals("XSRF-TOKEN"))
        .findFirst().orElseThrow()
        .getValue();
  }

  private static @Nonnull CookieStore inMemoryCookieStore() {
    return new CookieManager(null, CookiePolicy.ACCEPT_ALL).getCookieStore();
  }
}
