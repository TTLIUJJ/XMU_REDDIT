package com.ackerman.reddit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
//
//@SpringBootApplication
//public class RedditApplication extends SpringBootServletInitializer{
//
//	public static void main(String[] args) {
//		SpringApplication.run(RedditApplication.class, args);
//	}
//
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(RedditApplication.class);
//	}
//}

@SpringBootApplication
public class RedditApplication{
	public static void main(String []args){
		SpringApplication.run(RedditApplication.class, args);
	}
}