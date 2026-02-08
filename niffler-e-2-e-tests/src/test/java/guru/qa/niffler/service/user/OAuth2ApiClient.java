package guru.qa.niffler.service.user;

import static guru.qa.niffler.helpers.OAuth2Utils.generateCodeChallenge;
import static guru.qa.niffler.helpers.OAuth2Utils.generateCodeVerifier;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.user.OAuth2Api;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.SneakyThrows;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public class OAuth2ApiClient extends RestClient {

  private final static String RESPONSE_TYPE = "code";
  private final static String CLIENT_ID = "client";
  private final static String SCOPE = "openid";
  private final static String REDIRECT_URI = CFG.frontUrl() + "authorized";
  private final static String GRANT_TYPE = "authorization_code";
  private final static String SHA = "S256";

  private final OAuth2Api oAuth2Api;

  public OAuth2ApiClient() {
    super(CFG.authUrl(), true, new CodeInterceptor());
    oAuth2Api = create(OAuth2Api.class);
  }

  @SneakyThrows
  @Step("REST API login OAuth2")
  public String login(String username, String password) {
    String codeVerifier = generateCodeVerifier();
    String codeChallenge = generateCodeChallenge(codeVerifier);
    oAuth2Api.authorize(
        RESPONSE_TYPE,
        CLIENT_ID,
        SCOPE,
        REDIRECT_URI,
        codeChallenge,
        SHA
    ).execute();

    oAuth2Api.login(
        username,
        password,
        ThreadSafeCookieStore.INSTANCE.xsrfCookie()
    ).execute();
    Response<JsonNode> tokenResponse = oAuth2Api.token(
        ApiLoginExtension.getCode(),
        REDIRECT_URI,
        codeVerifier,
        GRANT_TYPE,
        CLIENT_ID
    ).execute();
    return tokenResponse.body().get("id_token").asText();
  }
}
