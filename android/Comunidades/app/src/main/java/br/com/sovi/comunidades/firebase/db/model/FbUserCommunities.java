package br.com.sovi.comunidades.firebase.db.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FbUserCommunities {

    private Date dateSubscribed = new Date();

    public FbUserCommunities() {
    }

    public Date getDateSubscribed() {
        return dateSubscribed;
    }

    public void setDateSubscribed(Date dateSubscribed) {
        this.dateSubscribed = dateSubscribed;
    }
}
