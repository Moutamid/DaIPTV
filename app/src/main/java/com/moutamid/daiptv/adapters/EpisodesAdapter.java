package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.lisetenrs.EpisodeClicked;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.EpisodesModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeVH> {
    Context context;
    ArrayList<EpisodesModel> list;
    EpisodeClicked episodeClicked;

    public EpisodesAdapter(Context context, ArrayList<EpisodesModel> list, EpisodeClicked episodeClicked) {
        this.context = context;
        this.list = list;
        this.episodeClicked = episodeClicked;
    }

    @NonNull
    @Override
    public EpisodeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpisodeVH(LayoutInflater.from(context).inflate(R.layout.episode_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeVH holder, int position) {
        EpisodesModel model = list.get(holder.getAbsoluteAdapterPosition());
        Glide.with(context).load(Constants.getImageLink(model.image)).placeholder(R.color.black).into(holder.coverImage);
        holder.seasonNo.setText(model.se);
        holder.name.setText(model.name);
        holder.desc.setText(model.desc);

        holder.itemView.setOnClickListener(v -> {
            episodeClicked.clicked(model);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EpisodeVH extends RecyclerView.ViewHolder{
        ImageView coverImage;
        TextView seasonNo;
        TextView name;
        TextView desc;
        public EpisodeVH(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.coverImage);
            seasonNo = itemView.findViewById(R.id.seasonNo);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
        }
    }

}
