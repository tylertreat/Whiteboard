package com.whiteboard.auth;

import com.clarionmedia.infinitum.di.annotation.Bean;
import com.whiteboard.model.User;

@Bean
public class SessionManager {

    private User sSessionUser;

    public User getUser() {
        return sSessionUser;
    }

    public void setUser(User user) {
        sSessionUser = user;
    }

}
