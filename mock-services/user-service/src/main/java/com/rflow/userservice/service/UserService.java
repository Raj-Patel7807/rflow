package com.rflow.userservice.service;

import com.rflow.userservice.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();

    public UserService() {
        users.add(new User(1L, "Raj", "raj@gmail.com"));
        users.add(new User(2L, "RajJR", "rajjr@gmail.com"));
        users.add(new User(3L, "user", "user@gmail.com"));
        users.add(new User(4L, "user1", "user1@gmail.com"));
        users.add(new User(5L, "user2", "user2@gmail.com"));
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(Long userId) {
        for(int i=0; i<users.size(); i++) {
            if(users.get(i).getId() == userId) {
                return users.get(i);
            }
        }
        return null;
    }
}
