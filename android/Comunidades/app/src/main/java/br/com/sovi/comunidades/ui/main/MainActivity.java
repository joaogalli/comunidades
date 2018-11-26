package br.com.sovi.comunidades.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import br.com.sovi.comunidades.R;
import br.com.sovi.comunidades.firebase.db.model.FbCommunity;
import br.com.sovi.comunidades.ui.communitydetail.CommunityDetailFragment;
import br.com.sovi.comunidades.ui.communityedit.CommunityEditFragment;
import br.com.sovi.comunidades.ui.communitysearch.CommunitySearchFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainView,
        CommunityEditFragment.OnCommunityEditFragmentInteractionListener,
        CommunitySearchFragment.OnCommunitySearchFragmentInteractionListener,
        CommunityDetailFragment.OnCommunityDetailFragmentInteractionListener {

    public static final int RC_SIGN_IN = 9001;
    private static final int COMMUNITY_SEARCH_ITEM = 0;
    private static final int COMMUNITY_DETAIL_ITEM = 1;
    private static final int COMMUNITY_EDIT_ITEM = 2;

    private MainPresenter presenter;

    private CommunityEditFragment communityEditFragment;

    private CommunitySearchFragment communitySearchFragment;

    private CommunityDetailFragment communityDetailFragment;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        communityDetailFragment = new CommunityDetailFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        presenter = new MainPresenter(this, this);
        presenter.init();
    }

    @OnClick(R.id.criarComunidade)
    public void onCriarComunidadeClick() {
        presenter.onCriarComunidadeClick();
    }

    @Override
    public void showCommunityCreation() {
        viewPager.setCurrentItem(COMMUNITY_EDIT_ITEM);
    }

    @OnClick(R.id.logoff)
    public void onLogoffClick() {
        presenter.logoff();
    }

    @Override
    public void showGoogleLogin(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                presenter.firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                System.err.println(e);
                Toast.makeText(this, "An error occurred login in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showProgressDialog() {
        Toast.makeText(this, "LOADING...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgressDialog() {
        Toast.makeText(this, "FINISHED LOADING...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginSuccessful() {
        Toast.makeText(this, "LOGIN SUCCESS", Toast.LENGTH_SHORT).show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    viewPager.setCurrentItem(COMMUNITY_SEARCH_ITEM);
                    return true;
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onCommunityClick(String communityId) {
        communityDetailFragment.setCommunity(communityId);
        viewPager.setCurrentItem(COMMUNITY_DETAIL_ITEM);
    }

    @Override
    public void onCommunityCreated(FbCommunity fbCommunity) {
        communityDetailFragment.setCommunity(fbCommunity.getId());
        viewPager.setCurrentItem(COMMUNITY_DETAIL_ITEM);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case COMMUNITY_SEARCH_ITEM:
                    communitySearchFragment = CommunitySearchFragment.newInstance();
                    return communitySearchFragment;
                case COMMUNITY_DETAIL_ITEM:
                    return communityDetailFragment;
                case COMMUNITY_EDIT_ITEM:
                    communityEditFragment = CommunityEditFragment.newInstance();
                    return communityEditFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Busca";
                case 1:
                    return "Detalhes";
                case 2:
                    return "Criação";
                default:
                    return null;
            }
        }
    }

}
