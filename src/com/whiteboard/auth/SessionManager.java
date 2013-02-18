package com.whiteboard.auth;

import com.whiteboard.model.User;

public class SessionManager {

    private static User sSessionUser;

    public static User getUser() {
        return sSessionUser;
    }

    public static void setUser(User user) {
        sSessionUser = user;
    }

}
