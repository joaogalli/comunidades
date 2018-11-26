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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import br.com.sovi.comunidades.MapUtils;
import br.com.sovi.comunidades.R;
import br.com.sovi.comunidades.firebase.db.FirebaseConstants;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import br.com.sovi.comunidades.firebase.db.model.FbUser;
import br.com.sovi.comunidades.service.AuthenticationService;
import br.com.sovi.comunidades.ui.base.BasePresenter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;

public class MainPresenter extends BasePresenter {

    private MainView view;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private AuthenticationService authenticationService;

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

        authenticationService = new AuthenticationService(getContext());
    }

    public void onCriarComunidadeClick() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            view.showGoogleLogin(mGoogleSignInClient);
        } else {
            view.showCommunityCreation();
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            registerFirebaseUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerFirebaseUser(FirebaseUser user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseConstants.DATABASE_REFERENCE)
                .child(FirebaseConstants.TABLE_USER);


        Single.create((SingleOnSubscribe<FbUser>) emitter -> {
            usersRef
            .orderByChild("uid")
            .equalTo(user.getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        FbUser value = iterator.next().getValue(FbUser.class);
                        emitter.onSuccess(value);
                    } else {
                        emitter.onSuccess(new FbUser());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());
                }
            });
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .flatMap((Function<FbUser, SingleSource<FbUser>>) fbUser -> {
            if (fbUser.getUid() != null) {
                return observer -> observer.onSuccess(fbUser);
            } else {
                return observer -> {
                    FbUser fbUser1 = buildFbUser(user);

                    DatabaseReference databaseReference = usersRef.push();
                    String key = databaseReference.getKey();
                    fbUser1.setId(key);

                    usersRef.setValue(MapUtils.buildSingleMap(key, fbUser1));

                    observer.onSuccess(fbUser1);
                };
            }
        })
        .subscribe(new SingleObserver<FbUser>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(FbUser fbUser) {
                authenticationService.setAuthenticatedUser(fbUser);
                view.hideProgressDialog();
                view.showLoginSuccessful();
            }

            @Override
            public void onError(Throwable e) {
                authenticationService.logout();
                // TODO erro ao registrar
                // TODO deslogar e começar denovo
                view.hideProgressDialog();
            }
        });
    }

    private FbUser buildFbUser(FirebaseUser user) {
        FbUser fb = new FbUser();

        fb.setName(user.getDisplayName());
        fb.setEmail(user.getEmail());
        fb.setUid(user.getUid());

        return fb;
    }

    public void logoff() {
        mAuth.signOut();
    }
}
