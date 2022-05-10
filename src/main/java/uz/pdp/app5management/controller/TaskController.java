package uz.pdp.app5management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.app5management.entity.Task;
import uz.pdp.app5management.payload.ApiResponse;
import uz.pdp.app5management.payload.TaskDto;
import uz.pdp.app5management.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping
    public HttpEntity<?> addTask(@RequestBody TaskDto taskDto){
        ApiResponse apiResponse = taskService.addTask(taskDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 201 : 409).body(apiResponse);
    }

    @GetMapping("/done")
    public HttpEntity<?> taskDone(@RequestParam UUID id){
        ApiResponse apiResponse = taskService.taskDone(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getTaskById(@PathVariable UUID id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public HttpEntity<?> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/notDoneOnTime")
    public HttpEntity<?> getNotDoneOnTimeTasks(){
        List<Task> tasks = taskService.getNotDoneOnTimeTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/byUserId")
    public HttpEntity<?> getTasksByWorker(){
        return ResponseEntity.ok(taskService.getTasksByWorker());
    }

    @PutMapping("/{id}")
    public HttpEntity<?> editTask(@PathVariable UUID id, @RequestBody TaskDto taskDto){
        ApiResponse apiResponse = taskService.editTask(id, taskDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 202 : 409).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteTask(@PathVariable UUID id){
        ApiResponse apiResponse = taskService.deleteTask(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 202 : 409).body(apiResponse);
    }

}
