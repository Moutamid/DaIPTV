package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.ArrayList;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeVH> {
    Context context;
    ArrayList<ChannelsModel> list;

    @NonNull
    @Override
    public EpisodeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpisodeVH(LayoutInflater.from(context).inflate(R.layout.episode_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeVH holder, int position) {
        ChannelsModel model = list.get(holder.getAbsoluteAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EpisodeVH extends RecyclerView.ViewHolder{
        public EpisodeVH(@NonNull View itemView) {
            super(itemView);
        }
    }

}
