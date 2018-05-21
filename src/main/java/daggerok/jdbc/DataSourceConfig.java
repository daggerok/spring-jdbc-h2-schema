package daggerok.jdbc;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
class DataSourceConfig {

  @Bean
  public ApplicationRunner runner(final JdbcTemplate jdbcTemplate) {
    return args -> jdbcTemplate.queryForList("select * from oauth_client_details")
        .parallelStream()
        .map(Map::entrySet)
        .flatMap(Collection::parallelStream)
        .map(e -> format("%s: %s", e.getKey(), e.getValue()))
        .forEach(System.out::println);
  }

  @Bean
  public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
    final DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(databasePopulator());
    return initializer;
  }

  private DatabasePopulator databasePopulator() {
    final ClassPathResource schema = new ClassPathResource("sql/ddl/schema.sql", DataSourceConfig.class.getClassLoader());
    final ClassPathResource data = new ClassPathResource("sql/dml/data.sql", DataSourceConfig.class.getClassLoader());
    return new ResourceDatabasePopulator(false, true, UTF_8.displayName(), schema, data);
  }
}
