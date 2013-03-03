package com.whiteboard.dao;

import com.whiteboard.model.User;

public interface UserDao {

    void saveUser(User user);

    User getUserByEmail(String email);

}
