package com.whiteboard.service;

import com.whiteboard.model.InviteToken;

public interface TokenService {

    InviteToken getInviteToken(String token);

    void deleteInviteToken(InviteToken token);

    InviteToken createInviteToken(String email);

}
