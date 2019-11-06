package com.plugin.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.plugin.rest","com.plugin.services","com.plugin.entities"})
public class PluginWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(PluginWebApplication.class, args);
	}

}
