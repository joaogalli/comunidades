package br.com.sovi.comunidades.ui.main;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public interface MainView {
    void showComunityCreation();

    void showProgressDialog();

    void hideProgressDialog();

    void showLoginSuccessful();

    void showGoogleLogin(GoogleSignInClient mGoogleSignInClient);

}
