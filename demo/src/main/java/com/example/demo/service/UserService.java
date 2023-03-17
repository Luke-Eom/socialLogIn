package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {

    void memberJoin(User user);
    void updateUser(User user);
    User getUser(String username);
}
