package com.stark.springbootarchunit.service;

import com.stark.springbootarchunit.controller.UserRestController;
import com.stark.springbootarchunit.domain.User;
import com.stark.springbootarchunit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private UserRestController userRestController;

    public List<User> getUsers() {
        Iterable iterable = userRepository.findAll();
        List<User> users = new ArrayList<>();
        iterable.forEach(rec->users.add((User) rec));
        return users;
    }

    public User getUser(String userId) {
        Optional<User> optional = userRepository.findById(userId);
        return optional.orElse(null);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void doNothingInService(){
        userRestController.testMethod();
    }

}
