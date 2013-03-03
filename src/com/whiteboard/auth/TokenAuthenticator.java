package com.whiteboard.auth;

import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.digitalxyncing.communication.Authenticator;
import com.whiteboard.model.InviteToken;
import com.whiteboard.service.TokenService;

@Bean
public class TokenAuthenticator implements Authenticator {

    @Autowired
    private TokenService mTokenService;

    @Override
    public boolean isAuthenticated(String address, int port, String token) {
        String[] emailAndToken = token.split(" ");
        String email = emailAndToken[0];
        token = emailAndToken[1];
        InviteToken inviteToken = mTokenService.getInviteToken(token);
        if (inviteToken == null)
            return false;
        if (!inviteToken.getInvited().equalsIgnoreCase(email))
            return false;
        mTokenService.deleteInviteToken(inviteToken);
        return true;
    }

}
