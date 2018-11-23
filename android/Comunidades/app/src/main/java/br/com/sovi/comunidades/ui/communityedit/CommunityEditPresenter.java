package br.com.sovi.comunidades.ui.communityedit;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.com.sovi.comunidades.firebase.db.FirebaseConstants;
import br.com.sovi.comunidades.firebase.db.model.Community;
import br.com.sovi.comunidades.ui.base.BasePresenter;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

public class CommunityEditPresenter extends BasePresenter {

    private CommunityEditView view;

    private Community currentCommunity;

    public CommunityEditPresenter(Context context, CommunityEditView view) {
        super(context);
        this.view = view;
    }

    public void saveCommunity(CommunityEditVo vo) {
        final Community bean = fromVoToBean(vo);

        Single.create(new SingleOnSubscribe<Community>() {
            @Override
            public void subscribe(SingleEmitter<Community> emitter) throws Exception {
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
            }
        }).subscribe(new SingleObserver<Community>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Community community) {
                Toast.makeText(getContext(), "SUCESSO", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), "FALHOU", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Community fromVoToBean(CommunityEditVo vo) {
        if (currentCommunity == null)
            currentCommunity = new Community();
        currentCommunity.setName(vo.getName());
        currentCommunity.setDescription(vo.getDescription());

        return currentCommunity;
    }
}
