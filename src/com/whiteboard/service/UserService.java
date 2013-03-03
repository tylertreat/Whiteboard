package com.whiteboard.service;

import com.whiteboard.model.User;

public interface UserService {

    void saveUser(User user);

    User getUserByEmail(String email);

}
