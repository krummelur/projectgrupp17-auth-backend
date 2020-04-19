package com.fredriksonsound.iot_backoffice_auth;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean
    public DataSource getDataSource() {
        var env = new Environment();
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://" + env.SQL_HOST + "/" + env.SQL_DB + "?reconnect=true");
        dataSourceBuilder.username(env.SQL_USER);
        dataSourceBuilder.password(env.SQL_PASS);
        return dataSourceBuilder.build();
    }
}
