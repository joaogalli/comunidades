package br.com.sovi.comunidades.ui.communityedit;

import br.com.sovi.comunidades.firebase.db.model.FbCommunity;

public interface CommunityEditView {
    void onCommunityCreationSuccess(FbCommunity fbCommunity);
}
