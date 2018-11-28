package br.com.sovi.comunidades.ui.communityedit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;

import br.com.sovi.comunidades.R;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import br.com.sovi.comunidades.utils.StatesAndCities;
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

    @BindView(R.id.state)
    AppCompatSpinner stateSpinner;

    @BindView(R.id.city)
    AppCompatSpinner citySpinner;

    private List<StatesAndCities.State> states;

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

        StatesAndCities statesAndCities = new StatesAndCities(getContext());
        states = statesAndCities.getStates();

        ArrayAdapter<StatesAndCities.State> spinnerArrayAdapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, states);

        stateSpinner.setAdapter(spinnerArrayAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySpinner.setVisibility(View.VISIBLE);
                StatesAndCities.State state = states.get(position);
                if (state != null) {
                    citySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, state.getCidades()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                citySpinner.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.saveCommunity)
    public void onSaveCommunityClick() {
        CommunityEditVo vo = new CommunityEditVo();
        vo.setName(nameView.getText().toString());
        vo.setDescription(descriptionView.getText().toString());
        vo.setState(((StatesAndCities.State) stateSpinner.getSelectedItem()).getNome());
        vo.setCity(citySpinner.getSelectedItem().toString());

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
