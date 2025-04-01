package com.ostafon.todoapp.service;

import com.ostafon.todoapp.model.Task;
import com.ostafon.todoapp.repo.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    final
    TaskRepository repo;

    private final SimpMessagingTemplate messagingTemplate;

    public TaskService(SimpMessagingTemplate messagingTemplate, TaskRepository repo) {
        this.messagingTemplate = messagingTemplate;
        this.repo = repo;
    }

    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = repo.findAll();
        return tasks.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    public ResponseEntity<Task> getTaskById(int id) {
        return repo.findById(id)
                .map(task -> new ResponseEntity<>(task, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Task> createTask(Task task) {
        if (task == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Task savedTask = repo.save(task);
        broadcastTasks();
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> updateTask(Integer id, Task task) {
        return repo.findById(id).map(existingTask -> {
            existingTask.setDescription(task.getDescription());
            existingTask.setCompleted(task.isCompleted());
            repo.save(existingTask);
            broadcastTasks();
            return new ResponseEntity<>(HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<?> deleteTask(Integer id) {
        if (repo.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
        broadcastTasks();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void broadcastTasks() {
        messagingTemplate.convertAndSend("/topic/tasks", repo.findAll());
    }
}
