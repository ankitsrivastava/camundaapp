package com.ankit.camunda.work.assignment.task;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private ZeebeClient zeebeClient;

    @PostMapping("/complete/{jobKey}")
    public ResponseEntity<String> completeUserTask(@PathVariable long jobKey, @RequestBody Map<String, Object> variables) {
        CompleteJobResponse response = zeebeClient.newCompleteCommand(jobKey)
                .variables(variables)
                .send()
                .join();

        return ResponseEntity.ok("Job completed with key: " + response.toString());
    }
}
