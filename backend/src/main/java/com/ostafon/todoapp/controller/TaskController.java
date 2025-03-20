package com.ostafon.todoapp.controller;

import com.ostafon.todoapp.model.Task;
import com.ostafon.todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskService taskService;

    @GetMapping()
    public ResponseEntity<List<Task>> getTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody Task task) {
        return taskService.updateTask(id,task);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteTask(@PathVariable int id) {
        return taskService.deleteTask(id);
    }


}
