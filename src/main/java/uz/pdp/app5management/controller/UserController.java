package uz.pdp.app5management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.pdp.app5management.entity.User;
import uz.pdp.app5management.payload.ApiResponse;
import uz.pdp.app5management.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public HttpEntity<?> getAllUsersForDirectorAndManager(){
        List<User> users = userService.getAllUsersForDirectorAndManager();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getAllInfoAboutUser(@PathVariable UUID id){
        return ResponseEntity.ok(userService.getAllInfoAboutUser(id));
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteUser(@PathVariable UUID id){
        ApiResponse apiResponse = userService.deleteUser(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 204 : 409).body(apiResponse);
    }

}
