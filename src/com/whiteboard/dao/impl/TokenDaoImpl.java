package com.whiteboard.dao.impl;

import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.clarionmedia.infinitum.di.annotation.PostConstruct;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext;
import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext.SessionType;
import com.clarionmedia.infinitum.orm.criteria.criterion.Conditions;
import com.whiteboard.auth.SessionManager;
import com.whiteboard.dao.TokenDao;
import com.whiteboard.model.InviteToken;
import org.joda.time.DateTime;

import java.util.UUID;

@Bean("tokenDao")
public class TokenDaoImpl implements TokenDao {

    @Autowired
    private InfinitumOrmContext mOrmContext;
    private Session mSession;

    @PostConstruct
    private void init() {
        mSession = mOrmContext.getSession(SessionType.SQLITE);
    }

    @Override
    public InviteToken getInviteToken(String token) {
        mSession.open();
        try {
            return mSession.createCriteria(InviteToken.class).add(Conditions.eq("mGuid", token)).unique();
        } finally {
            mSession.close();
        }
    }

    @Override
    public void deleteInviteToken(InviteToken token) {
        mSession.open();
        try {
            mSession.delete(token);
        } finally {
            mSession.close();
        }
    }

    @Override
    public InviteToken createInviteToken(String email) {
        mSession.open();
        InviteToken token = new InviteToken();
        token.setOwner(SessionManager.getUser().getEmail());
        token.setInvited(email);
        token.setExpirationDate(DateTime.now().plusWeeks(1));
        token.setGuid(UUID.randomUUID().toString());
        try {
            mSession.save(token);
            return token;
        } finally {
            mSession.close();
        }
    }

}
