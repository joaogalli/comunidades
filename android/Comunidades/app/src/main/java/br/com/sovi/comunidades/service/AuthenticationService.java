package br.com.sovi.comunidades.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import br.com.sovi.comunidades.firebase.db.model.FbUser;

public class AuthenticationService {

    private Context mContext;

    public static final String SHARED_PREFERENCES_NAME = "userservice";

    public static final String AUTHENTICATED_USER = "authenticatedUser";

    private static Gson gson = new Gson();

    public AuthenticationService(Context mContext) {
        this.mContext = mContext;
    }

    public void setAuthenticatedUser(FbUser user) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(AUTHENTICATED_USER, gson.toJson(user));
        edit.apply();
    }

    public FbUser getAuthenticatedUser() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return gson.fromJson(sharedPref.getString(AUTHENTICATED_USER, null), FbUser.class);
    }

    public void logout() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear();
        edit.apply();
    }
}
