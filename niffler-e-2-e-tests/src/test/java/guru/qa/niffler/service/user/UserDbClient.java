package guru.qa.niffler.service.user;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserDbClient {

  private final static Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
  private final UserDao udUserDao = new UdUserDaoSpringJdbc();

  private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  public UserJson create(UserJson user) {
    return xaTxTemplate.execute(() -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("admin"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);

      AuthUserEntity createdAuthUser = authUserDao.create(authUser);

      AuthAuthorityEntity[] authorityEntities = Stream.of(Authority.values()).map(
          e -> {
            AuthAuthorityEntity ae = new AuthAuthorityEntity();
            ae.setUser(createdAuthUser);
            ae.setAuthority(e);
            return ae;
          }
      ).toArray(AuthAuthorityEntity[]::new);

      authAuthorityDao.create(authorityEntities);
      return UserJson.fromEntity(udUserDao.create(UserEntity.fromJson(user)));
    });
  }

  public List<AuthAuthorityEntity> findAllAuthority(UUID userUuid) {
    return authAuthorityDao.findAllByUserId(userUuid);
  }
}
