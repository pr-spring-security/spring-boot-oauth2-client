package com.simon.cient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class CientApplication implements CommandLineRunner {

	@Autowired
	JdbcTemplate jdbcTemplate;
	public static void main(String[] args) {
		SpringApplication.run(CientApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		int rowCount = this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
		System.out.println(rowCount);
	}
}
