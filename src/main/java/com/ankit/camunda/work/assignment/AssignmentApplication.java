package com.ankit.camunda.work.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = "classpath:random-animal.bpmn")
public class AssignmentApplication{

	public static void main(String[] args) {
		SpringApplication.run(AssignmentApplication.class, args);
	}
}
