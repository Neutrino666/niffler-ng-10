package guru.qa.niffler.test;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

public class ChainedTxManagerTest {

  private final static Config CFG = Config.getInstance();
  private final PasswordEncoder PE = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  private final TransactionTemplate TX_TEMPLATE = new TransactionTemplate(
      new ChainedTransactionManager(
          new JdbcTransactionManager(
              DataSources.dataSource(CFG.authJdbcUrl())
          ),
          new JdbcTransactionManager(
              DataSources.dataSource(CFG.userdataJdbcUrl())
          )
      )
  );

  @Test
  void springJdbcRollbackSuccess() {
    TX_TEMPLATE.execute(status -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername("springJdbcUser");
      authUser.setPassword(PE.encode("success"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);

      AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc().create(authUser);

      AuthAuthorityEntity[] authorityEntities = Stream.of(Authority.values()).map(
          e -> {
            AuthAuthorityEntity ae = new AuthAuthorityEntity();
            ae.setUser(createdAuthUser);
            ae.setAuthority(e);
            return ae;
          }
      ).toArray(AuthAuthorityEntity[]::new);
      new AuthAuthorityDaoSpringJdbc().create(authorityEntities);
      return new UdUserDaoSpringJdbc().create(new UserEntity());
    });
  }

  @Test
  void jdbcRollbackFail() {
    TX_TEMPLATE.execute(status -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername("jdbcFailRollback");
      authUser.setPassword(PE.encode("failPwd"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);

      AuthUserEntity createdAuthUser = new AuthUserDaoJdbc().create(authUser);

      AuthAuthorityEntity[] authorityEntities = Stream.of(Authority.values()).map(
          e -> {
            AuthAuthorityEntity ae = new AuthAuthorityEntity();
            ae.setUser(createdAuthUser);
            ae.setAuthority(e);
            return ae;
          }
      ).toArray(AuthAuthorityEntity[]::new);
      new AuthAuthorityDaoJdbc().create(authorityEntities);
      return new UdUserDaoJdbc().create(new UserEntity());
    });
  }
}
