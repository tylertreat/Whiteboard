package com.whiteboard.service.impl;

import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.whiteboard.dao.UserDao;
import com.whiteboard.model.User;
import com.whiteboard.service.UserService;

@Bean("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao mUserDao;

    @Override
    public void saveUser(User user) {
        mUserDao.saveUser(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return mUserDao.getUserByEmail(email);
    }

}
