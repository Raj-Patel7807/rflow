package com.rflow.userservice.service;

import com.rflow.userservice.exception.UserNotFoundException;
import com.rflow.userservice.model.User;
import com.rflow.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not Found with Id: " + userId));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User user) {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User Not Found with Id: " + id));

        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());

        return userRepository.save(oldUser);
    }
}
