package daggerok.jdbc;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.Map;

import static java.lang.String.format;

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
}
