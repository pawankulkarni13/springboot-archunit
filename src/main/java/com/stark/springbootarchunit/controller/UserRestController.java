package com.stark.springbootarchunit.controller;

import com.stark.springbootarchunit.domain.User;
import com.stark.springbootarchunit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId){
        return userService.getUser(userId);
    }

    @PostMapping
    public void saveUser(@RequestBody User user){
        userService.saveUser(user);
    }

    public void testMethod(){
        System.out.println("Do Nothing !!!");
    }


}
