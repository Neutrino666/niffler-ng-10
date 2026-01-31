package guru.qa.niffler.service.user;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.user.OAuth2Api;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.assertj.core.api.Assertions;
import retrofit2.Response;

@ParametersAreNonnullByDefault
public class OAuth2ApiClient extends RestClient {

  private final static String RESPONSE_TYPE = "code";
  private final static String CLIENT_ID = "client";
  private final static String SCOPE = "openid";
  private final static String REDIRECT_URI = CFG.frontUrl() + "authorized";
  private final static String GRANT_TYPE ="authorization_code";
  private final static String SHA ="S256";

  private final OAuth2Api OAuth2Api;

  public OAuth2ApiClient() {
    super(CFG.authUrl());
    OAuth2Api = create(OAuth2Api.class);
  }

  @Step("REST API authorized")
  public void preRequest(String codeChallenge) throws IOException {
    Response<Void> response = OAuth2Api.authorize(
        RESPONSE_TYPE,
        CLIENT_ID,
        SCOPE,
        REDIRECT_URI,
        codeChallenge,
        SHA
    ).execute();
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
  }

  @Step("REST API login")
  public String login(String username, String password) throws IOException {
    Response<Void> response = OAuth2Api
        .login(username, password, ThreadSafeCookieStore.INSTANCE.xsrfCookie())
        .execute();
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
    return response.raw().request().url().toString().split("code=")[1];
  }

  @Step("REST API token")
  public String token(String code, String codeVerifier) throws IOException {
    Response<JsonNode> response = OAuth2Api
        .token(code,
            REDIRECT_URI,
            codeVerifier,
            GRANT_TYPE,
            CLIENT_ID)
        .execute();
    Assertions.assertThat(response.code())
        .isEqualTo(SC_OK);
    return response.body().path("id_token").asText();
  }
}
