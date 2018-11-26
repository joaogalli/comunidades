package br.com.sovi.comunidades.ui.communitydetail;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sovi.comunidades.firebase.db.FirebaseConstants;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import br.com.sovi.comunidades.firebase.db.model.FbCommunitySubscriber;
import br.com.sovi.comunidades.firebase.db.model.FbUser;
import br.com.sovi.comunidades.firebase.db.model.FbUserCommunities;
import br.com.sovi.comunidades.service.AuthenticationService;
import br.com.sovi.comunidades.ui.base.BasePresenter;
import br.com.sovi.comunidades.utils.MapUtils;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommunityDetailPresenter extends BasePresenter {

    private CommunityDetailView view;

    private FbCommunity currentCommunity;

    private AuthenticationService authenticationService;

    public CommunityDetailPresenter(Context context, CommunityDetailView view) {
        super(context);
        this.view = view;
    }

    @Override
    public void init() {
        super.init();
        authenticationService = new AuthenticationService(getContext());
    }

    public void setCommunity(final String communityId) {
        Single.create((SingleOnSubscribe<FbCommunity>) emitter -> {

            FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstants.DATABASE_REFERENCE)
                    .child(FirebaseConstants.TABLE_COMMUNITIES)
                    .child(communityId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                FbCommunity fbCommunity = dataSnapshot.getValue(FbCommunity.class);
                                emitter.onSuccess(fbCommunity);
                            } else {
                                emitter.onError(new CommunityNotFoundException());
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
        .subscribe(new SingleObserver<FbCommunity>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(FbCommunity fbCommunity) {
                currentCommunity = fbCommunity;
                view.showCommunity(fbCommunity);
            }

            @Override
            public void onError(Throwable e) {
                // TODO
            }
        });
    }

    public void onSubscribeClick() {
        FbUser authenticatedUser = authenticationService.getAuthenticatedUser();

        Single.create((SingleOnSubscribe<FbCommunitySubscriber>) emitter -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstants.DATABASE_REFERENCE)
                    .child(FirebaseConstants.TABLE_COMMUNITY_SUBSCRIPTION)
                    .child(currentCommunity.getId())
                    .child(authenticatedUser.getId());

            databaseReference
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                FbCommunitySubscriber fb = new FbCommunitySubscriber();
                                databaseReference.setValue(MapUtils.buildSingleMap(authenticatedUser.getId(), fb));
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
                .subscribe(new SingleObserver<FbCommunitySubscriber>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(FbCommunitySubscriber fbCommunity) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });


        Single.create((SingleOnSubscribe<FbUserCommunities>) emitter -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstants.DATABASE_REFERENCE)
                    .child(FirebaseConstants.TABLE_USER_COMMUNITIES)
                    .child(authenticatedUser.getId())
                    .child(currentCommunity.getId());

            databaseReference
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                FbUserCommunities fb = new FbUserCommunities();
                                databaseReference.setValue(MapUtils.buildSingleMap(currentCommunity.getId(), fb));
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
                .subscribe(new SingleObserver<FbUserCommunities>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(FbUserCommunities fbCommunity) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });


    }

    private class CommunityNotFoundException extends RuntimeException {
    }
}
