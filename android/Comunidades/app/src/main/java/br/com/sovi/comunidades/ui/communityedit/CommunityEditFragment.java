package br.com.sovi.comunidades.ui.communityedit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import br.com.sovi.comunidades.R;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityEditFragment extends Fragment implements CommunityEditView {

    private CommunityEditPresenter presenter;

    private OnCommunityEditFragmentInteractionListener mListener;

    @BindView(R.id.name)
    EditText nameView;

    @BindView(R.id.description)
    EditText descriptionView;

    public CommunityEditFragment() {
        // Required empty public constructor
    }

    public static CommunityEditFragment newInstance() {
        CommunityEditFragment fragment = new CommunityEditFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        presenter = new CommunityEditPresenter(getContext(), this);
        presenter.init();
    }

    @OnClick(R.id.saveCommunity)
    public void onSaveCommunityClick() {
        CommunityEditVo vo = new CommunityEditVo();
        vo.setName(nameView.getText().toString());
        vo.setDescription(descriptionView.getText().toString());

        presenter.saveCommunity(vo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCommunityEditFragmentInteractionListener) {
            mListener = (OnCommunityEditFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCommunityEditFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCommunityCreationSuccess(FbCommunity fbCommunity) {
        if (mListener != null) {
            mListener.onCommunityCreated(fbCommunity);
        }
    }

    public interface OnCommunityEditFragmentInteractionListener {
        void onCommunityCreated(FbCommunity fbCommunity);
    }
}
