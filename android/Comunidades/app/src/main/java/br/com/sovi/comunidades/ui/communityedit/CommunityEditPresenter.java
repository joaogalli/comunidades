package br.com.sovi.comunidades.ui.communityedit;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.com.sovi.comunidades.firebase.db.FirebaseConstants;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import br.com.sovi.comunidades.firebase.db.model.FbUser;
import br.com.sovi.comunidades.service.AuthenticationService;
import br.com.sovi.comunidades.ui.base.BasePresenter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommunityEditPresenter extends BasePresenter {

    private CommunityEditView view;

    private FbCommunity currentCommunity;

    private AuthenticationService authenticationService;

    public CommunityEditPresenter(Context context, CommunityEditView view) {
        super(context);
        this.view = view;
    }

    @Override
    public void init() {
        super.init();

        authenticationService = new AuthenticationService(getContext());
    }

    public void saveCommunity(CommunityEditVo vo) {
        FbCommunity bean = fromVoToBean(vo);

        Single.create((SingleOnSubscribe<FbCommunity>) emitter -> {
            DatabaseReference child = FirebaseDatabase.getInstance()
                    .getReference(FirebaseConstants.DATABASE_REFERENCE)
                    .child(FirebaseConstants.TABLE_COMMUNITIES);

            if (TextUtils.isEmpty(bean.getId())) {
                DatabaseReference push = child.push();
                bean.setId(push.getKey());
            }

            Map<String, Object> map = new HashMap<>();
            map.put(bean.getId(), bean);
            child.updateChildren(map);

            emitter.onSuccess(bean);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<FbCommunity>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(FbCommunity fbCommunity) {
                view.onCommunityCreationSuccess(fbCommunity);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), "FALHOU", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private FbCommunity fromVoToBean(CommunityEditVo vo) {
        if (currentCommunity == null) {
            currentCommunity = new FbCommunity();

            FbUser authenticatedUser = authenticationService.getAuthenticatedUser();
            currentCommunity.setOwnerId(authenticatedUser.getId());
        }

        currentCommunity.setName(vo.getName());
        currentCommunity.setDescription(vo.getDescription());
        return currentCommunity;
    }
}
