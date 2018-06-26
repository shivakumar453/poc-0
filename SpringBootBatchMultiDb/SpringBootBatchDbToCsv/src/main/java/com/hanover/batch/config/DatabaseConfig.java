package com.hanover.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {
	
	public DataSource datasource;
	
	@Bean(name="datasource1")
	@ConfigurationProperties(prefix="spring.datasource1")
	@Primary
	public DataSource dataSource1() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name="datasource2")
	@ConfigurationProperties(prefix="spring.datasource2")
	public DataSource dataSource2() {
		return DataSourceBuilder.create().build();
	}
	
	/*private DataSource datasource;
	
	@Bean(name="springbootdb")
	@Autowired
	public DataSource springbootdb(@Qualifier("datasource1") DataSource datasource1) {
		return this.datasource =  datasource1;
	}
	
	@Bean(name="springbootdb2")
	@Autowired
	public DataSource springbootdb2(@Qualifier("datasource2") DataSource datasource2) {
		return this.datasource =  datasource2;
	}*/

}
