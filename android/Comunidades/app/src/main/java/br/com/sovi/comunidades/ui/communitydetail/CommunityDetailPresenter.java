package br.com.sovi.comunidades.ui.communitydetail;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.sovi.comunidades.firebase.db.FirebaseConstants;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import br.com.sovi.comunidades.ui.base.BasePresenter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommunityDetailPresenter extends BasePresenter {

    private CommunityDetailView view;

    public CommunityDetailPresenter(Context context, CommunityDetailView view) {
        super(context);
        this.view = view;
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
                view.showCommunity(fbCommunity);
            }

            @Override
            public void onError(Throwable e) {
                // TODO
            }
        });


    }

    protected class CommunityNotFoundException extends RuntimeException {

    }
}
