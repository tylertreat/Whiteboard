package com.whiteboard.service.impl;

import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.whiteboard.dao.TokenDao;
import com.whiteboard.model.InviteToken;
import com.whiteboard.service.TokenService;

@Bean("tokenService")
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenDao mTokenDao;

    @Override
    public InviteToken getInviteToken(String token) {
        return mTokenDao.getInviteToken(token);
    }

    @Override
    public void deleteInviteToken(InviteToken token) {
        mTokenDao.deleteInviteToken(token);
    }

    @Override
    public InviteToken createInviteToken(String email) {
        return mTokenDao.createInviteToken(email);
    }

}
