package br.com.sovi.comunidades.firebase.db.model;

import java.util.Date;

public class FbCommunitySubscriber {

    private Date dateSubscribed = new Date();

    public FbCommunitySubscriber() {
    }

    public Date getDateSubscribed() {
        return dateSubscribed;
    }

    public void setDateSubscribed(Date dateSubscribed) {
        this.dateSubscribed = dateSubscribed;
    }
}
