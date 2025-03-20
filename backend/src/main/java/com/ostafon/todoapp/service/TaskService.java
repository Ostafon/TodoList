package com.ostafon.todoapp.service;

import com.ostafon.todoapp.model.Task;
import com.ostafon.todoapp.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository repo;


    public ResponseEntity<List<Task>> getAllTasks() {
        if (repo.findAll().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Task> getTaskById(int id) {
        if (repo.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(repo.findById(id).get(), HttpStatus.OK);
    }

    public ResponseEntity<Task> createTask(Task task) {
        if(task == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(repo.save(task), HttpStatus.CREATED);
    }

    public ResponseEntity<Task> updateTask(Integer id, Task task) {
        if (repo.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Task newTask = repo.findById(id).get();
        newTask.setDescription(task.getDescription());
        newTask.setCompleted(task.isCompleted());
        repo.save(newTask);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    public ResponseEntity<?> deleteTask(Integer id) {
        if (repo.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
