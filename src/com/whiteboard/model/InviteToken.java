package com.whiteboard.model;

import com.clarionmedia.infinitum.orm.annotation.PrimaryKey;
import org.joda.time.DateTime;

public class InviteToken {

    @PrimaryKey
    private String mGuid;
    private String mOwner;
    private String mInvited;
    private DateTime mExpirationDate;

    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String guid) {
        mGuid = guid;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getInvited() {
        return mInvited;
    }

    public void setInvited(String invited) {
        mInvited = invited;
    }

    public DateTime getExpirationDate() {
        return mExpirationDate;
    }

    public void setExpirationDate(DateTime expirationDate) {
        mExpirationDate = expirationDate;
    }
}
