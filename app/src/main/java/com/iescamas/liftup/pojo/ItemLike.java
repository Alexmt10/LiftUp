package com.iescamas.liftup.pojo;

public class ItemLike {

    public String fromUserId;
    public String fromUserName;

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public ItemLike(String fromUserName, String fromUserId) {
        this.fromUserName = fromUserName;
        this.fromUserId = fromUserId;
    }

    public ItemLike() {
    }
}
