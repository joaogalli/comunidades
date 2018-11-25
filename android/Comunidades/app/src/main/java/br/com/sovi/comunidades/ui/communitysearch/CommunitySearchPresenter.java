package br.com.sovi.comunidades.ui.communitysearch;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.sovi.comunidades.firebase.db.FirebaseConstants;
import br.com.sovi.comunidades.firebase.db.model.Community;
import br.com.sovi.comunidades.ui.base.BasePresenter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CommunitySearchPresenter extends BasePresenter {

    private CommunitySearchView view;

    public CommunitySearchPresenter(Context context, CommunitySearchView view) {
        super(context);
        this.view = view;
    }

    public void search(String pattern) {
        Single.create((SingleOnSubscribe<List<Community>>) emitter -> {

            FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstants.DATABASE_REFERENCE)
                    .child(FirebaseConstants.TABLE_COMMUNITIES)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                List<Community> communities = new ArrayList<>();
                                for (DataSnapshot child : children) {
                                    try {
                                        Community community = child.getValue(Community.class);
                                        communities.add(community);
                                    } catch (Exception ex) {
                                        // TODO Crash
                                    }
                                }
                                emitter.onSuccess(communities);
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
                .map(communities -> {
                    List<CommunitySearchVo> vos = new ArrayList<>();
                    for (Community community : communities) {
                        CommunitySearchVo vo = new CommunitySearchVo();
                        vo.setId(community.getId());
                        vo.setName(community.getName());
                        vos.add(vo);
                    }
                    return vos;
                })
                .subscribe(new SingleObserver<List<CommunitySearchVo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<CommunitySearchVo> communitySearchVos) {
                        view.showCommunities(communitySearchVos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO
                    }
                });
    }
}
