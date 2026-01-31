package guru.qa.niffler.helpers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class OAuth2Utils {

  public static String generateCodeVerifier() {
    SecureRandom sr = new SecureRandom();
    byte[] code = new byte[32];
    sr.nextBytes(code);
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(code);
  }

  public static String generateCodeChallenge(String codeVerifier){
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hash = md.digest(
          codeVerifier.getBytes(StandardCharsets.US_ASCII)
      );
      return Base64.getUrlEncoder()
          .withoutPadding()
          .encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
