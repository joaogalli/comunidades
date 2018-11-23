package br.com.sovi.comunidades.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import br.com.sovi.comunidades.R;
import br.com.sovi.comunidades.ui.base.BasePresenter;

import static android.support.constraint.Constraints.TAG;

public class MainPresenter extends BasePresenter {

    private MainView view;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    public MainPresenter(Context context, MainView view) {
        super(context);
        this.view = view;
    }

    @Override
    public void init() {
        super.init();
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getContext().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    public void onCriarComunidadeClick() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            view.showGoogleLogin(mGoogleSignInClient);
        } else {
            view.showComunityCreation();
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        view.showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            view.showLoginSuccessful();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        view.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
}
