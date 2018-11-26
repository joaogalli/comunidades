package br.com.sovi.comunidades.ui.communitydetail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.sovi.comunidades.R;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityDetailFragment extends Fragment implements CommunityDetailView {

    private OnCommunityDetailFragmentInteractionListener mListener;

    private CommunityDetailPresenter presenter;

    @BindView(R.id.name)
    TextView nameView;

    @BindView(R.id.description)
    TextView descriptionView;

    public CommunityDetailFragment() {
        // Required empty public constructor
    }

    public static CommunityDetailFragment newInstance() {
        CommunityDetailFragment fragment = new CommunityDetailFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        presenter = new CommunityDetailPresenter(getContext(), this);
        presenter.init();
    }

    public void setCommunity(String communityId) {
        presenter.setCommunity(communityId);
    }

    @Override
    public void showCommunity(FbCommunity fbCommunity) {
        nameView.setText(fbCommunity.getName());
        descriptionView.setText(fbCommunity.getDescription());
    }

    @OnClick(R.id.subscribe)
    public void onSubscribeClick() {
        presenter.onSubscribeClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCommunityDetailFragmentInteractionListener) {
            mListener = (OnCommunityDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCommunityDetailFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCommunityDetailFragmentInteractionListener {
    }
}
