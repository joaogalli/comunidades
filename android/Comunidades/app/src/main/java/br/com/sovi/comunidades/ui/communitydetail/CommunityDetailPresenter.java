package br.com.sovi.comunidades.ui.communitydetail;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.sovi.comunidades.firebase.db.FirebaseConstants;
import br.com.sovi.comunidades.firebase.db.model.Community;
import br.com.sovi.comunidades.ui.base.BasePresenter;
import br.com.sovi.comunidades.ui.communitysearch.CommunitySearchVo;
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
        Single.create((SingleOnSubscribe<Community>) emitter -> {

            FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstants.DATABASE_REFERENCE)
                    .child(FirebaseConstants.TABLE_COMMUNITIES)
                    .child(communityId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Community community = dataSnapshot.getValue(Community.class);
                                emitter.onSuccess(community);
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
        .subscribe(new SingleObserver<Community>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Community community) {
                view.showCommunity(community);
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
