package guru.qa.niffler.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataSources {

  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  public static @Nonnull DataSource dataSource(String jdbcUrl) {
    return dataSources.computeIfAbsent(
        jdbcUrl,
        key -> {
          AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
          final String uniqueId = StringUtils.substringAfter(jdbcUrl, "5432/");
          dsBean.setUniqueResourceName(uniqueId);
          dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
          Properties props = new Properties();
          props.put("URL", jdbcUrl);
          props.put("user", "postgres");
          props.put("password", "secret");
          dsBean.setXaProperties(props);
          dsBean.setMaxPoolSize(3);
          dsBean.setMaxPoolSize(10);
          try {
            InitialContext context = new InitialContext();
            context.bind("java:comp/env/jdbc/" + uniqueId, dsBean);
          } catch (NamingException e) {
            throw new RuntimeException(e);
          }
          return dsBean;
        }
    );
  }
}
