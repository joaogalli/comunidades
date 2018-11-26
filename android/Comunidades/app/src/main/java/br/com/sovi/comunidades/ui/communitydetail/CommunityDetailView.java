package br.com.sovi.comunidades.ui.communitydetail;

import br.com.sovi.comunidades.firebase.db.model.FbCommunity;

public interface CommunityDetailView {
    void showCommunity(FbCommunity fbCommunity);
}
