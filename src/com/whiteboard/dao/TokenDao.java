package com.whiteboard.dao;

import com.whiteboard.model.InviteToken;

public interface TokenDao {

    InviteToken getInviteToken(String token);

    void deleteInviteToken(InviteToken token);

    InviteToken createInviteToken(String email);

}
