package com.ostafon.todoapp.controller;

import com.ostafon.todoapp.model.Task;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/update")
    @SendTo("/topic/tasks")
    public Task broadcastUpdate(Task task) {
        return task;
    }
}
