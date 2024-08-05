package com.ankit.camunda.work.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import io.camunda.zeebe.client.ZeebeClient;

import java.util.HashMap;

@RestController
public class AssignmentProcessController {

  @Autowired
  private ZeebeClient client;

  @GetMapping("/animal")
  public ResponseEntity<AnimalSelectionResponse> selectAnimal(ServerWebExchange exchange) {
    selectAnimal();
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  public void selectAnimal() {
    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("name", "Ankit");
    variables.put("animalType", "Cat");

    client.newCreateInstanceCommand() 
        .bpmnProcessId("random-animal") 
        .latestVersion() 
        .variables(variables) 
        .send().join();

  }

  public static class AnimalSelectionResponse {
  }
}