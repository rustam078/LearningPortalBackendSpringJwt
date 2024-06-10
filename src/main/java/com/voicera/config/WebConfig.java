package com.voicera.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		   registry.addMapping("/**")
		   .allowedOrigins("*")
//           .allowedOrigins("http://localhost:3000", "http://192.168.1.18:3000","http://localhost:3001", "http://192.168.1.18:3001")
           .allowedMethods("*")
           .allowedHeaders("*");
	}
	
}