package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.VideoPlayerActivity;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.utilis.CircularLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ChanelsAdapter extends RecyclerView.Adapter<ChanelsAdapter.ParentVH> {

    Context context;
    List<ChannelsModel> list;

    public ChanelsAdapter(Context context, List<ChannelsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ParentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentVH(LayoutInflater.from(context).inflate(R.layout.channels_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParentVH holder, int position) {
        ChannelsModel model = list.get(holder.getAdapterPosition());
        Glide.with(context).load(model.getChannelImg()).placeholder(R.color.transparent).into(holder.image);
        holder.title.setText(model.getChannelName());
        holder.epg.setText(model.getChannelGroup());

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra("url", model.getChannelUrl()).putExtra("name", model.getChannelName()));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ParentVH extends RecyclerView.ViewHolder{
        TextView title, epg;
        ImageView image;
        public ParentVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            epg = itemView.findViewById(R.id.epg);
            image = itemView.findViewById(R.id.image);
        }
    }

}
