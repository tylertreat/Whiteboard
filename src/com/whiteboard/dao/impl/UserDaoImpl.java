package com.whiteboard.dao.impl;

import android.content.ContentValues;
import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.clarionmedia.infinitum.di.annotation.PostConstruct;
import com.clarionmedia.infinitum.orm.ResultSet;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.orm.context.InfinitumOrmContext;
import com.clarionmedia.infinitum.orm.criteria.criterion.Conditions;
import com.clarionmedia.infinitum.orm.persistence.TypeAdapter;
import com.clarionmedia.infinitum.orm.persistence.TypeResolutionPolicy;
import com.clarionmedia.infinitum.orm.sqlite.SqliteTypeAdapter;
import com.whiteboard.dao.UserDao;
import com.whiteboard.model.User;
import org.joda.time.DateTime;

import java.lang.reflect.Field;

@Bean("userDao")
public class UserDaoImpl implements UserDao {

    @Autowired
    private InfinitumOrmContext mOrmContext;
    private Session mSession;

    @PostConstruct
    private void init() {
        mSession = mOrmContext.getSession(InfinitumOrmContext.SessionType.SQLITE);
        TypeAdapter<DateTime> jodaTimeAdapter = new SqliteTypeAdapter<DateTime>(TypeResolutionPolicy.SqliteDataType.TEXT) {
            @Override
            public void mapToObject(ResultSet result, int index, Field field,
                                    Object model) throws IllegalArgumentException, IllegalAccessException {
                long millis = result.getLong(index);
                DateTime dt = new DateTime(millis);
                field.set(model, dt);
            }
            @Override
            public void mapToColumn(DateTime value, String column, ContentValues values) {
                long millis = value.getMillis();
                values.put(column, millis);
            }
            @Override
            public void mapObjectToColumn(Object value, String column, ContentValues values) {
                DateTime dt = (DateTime) value;
                long millis = dt.getMillis();
                values.put(column, millis);
            }
        };
        mSession.registerTypeAdapter(DateTime.class, jodaTimeAdapter);
    }


    @Override
    public void saveUser(User user) {
        mSession.open();
        try {
            mSession.save(user);
        } finally {
            mSession.close();
        }
    }

    @Override
    public User getUserByEmail(String email) {
        mSession.open();
        try {
            return mSession.createCriteria(User.class).add(Conditions.eq("mEmail", email)).unique();
        } finally {
            mSession.close();
        }
    }
}
