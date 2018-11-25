package br.com.sovi.comunidades.ui.communitysearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.sovi.comunidades.R;

public class CommunitySearchRecyclerViewAdapter extends RecyclerView.Adapter<CommunitySearchRecyclerViewAdapter.ViewHolder> {

    private Context context;

    private CommunitySearchFragment.OnCommunitySearchFragmentInteractionListener interactionListener;

    private List<CommunitySearchVo> list = new ArrayList<>();

    public CommunitySearchRecyclerViewAdapter(Context context, CommunitySearchFragment.OnCommunitySearchFragmentInteractionListener interactionListener) {
        this.context = context;
        this.interactionListener = interactionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_community_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CommunitySearchVo vo = list.get(i);

        viewHolder.nameView.setText(vo.getName());

        viewHolder.itemView.setOnClickListener(v -> {
            interactionListener.onCommunityClick(vo.getId());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItems(List<CommunitySearchVo> communitySearchVos) {
        list.clear();
        list.addAll(communitySearchVos);
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        CommunitySearchVo item;
        final View itemView;
        final TextView nameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            nameView = itemView.findViewById(R.id.name);
        }
    }

}
