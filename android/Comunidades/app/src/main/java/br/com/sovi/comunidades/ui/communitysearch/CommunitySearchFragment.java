package br.com.sovi.comunidades.ui.communitysearch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import br.com.sovi.comunidades.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunitySearchFragment extends Fragment implements CommunitySearchView {

    private OnCommunitySearchFragmentInteractionListener mListener;

    private CommunitySearchPresenter presenter;

    private CommunitySearchRecyclerViewAdapter communitySearchRecyclerViewAdapter;

    @BindView(R.id.pattern)
    EditText searchText;

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    public CommunitySearchFragment() {
        // Required empty public constructor
    }

    public static CommunitySearchFragment newInstance() {
        return new CommunitySearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        presenter = new CommunitySearchPresenter(getContext(), this);
        presenter.init();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        communitySearchRecyclerViewAdapter = new CommunitySearchRecyclerViewAdapter(getContext(), mListener);
        recyclerView.setAdapter(communitySearchRecyclerViewAdapter);
    }

    @OnClick(R.id.searchButton)
    public void searchButton() {
        swipeRefreshLayout.setRefreshing(true);
        presenter.search(searchText.getText().toString());
    }

    @Override
    public void showCommunities(List<CommunitySearchVo> communitySearchVos) {
        swipeRefreshLayout.setRefreshing(false);
        communitySearchRecyclerViewAdapter.setItems(communitySearchVos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCommunitySearchFragmentInteractionListener) {
            mListener = (OnCommunitySearchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCommunitySearchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCommunitySearchFragmentInteractionListener {
        void onCommunityClick(String communityId);
    }
}
