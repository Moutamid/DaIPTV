package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.VideoPlayerActivity;
import com.moutamid.daiptv.database.ChannelsDAO;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.List;
import java.util.Objects;

public class ChanelsAdapter extends PagedListAdapter<ChannelsModel, ChanelsAdapter.ParentVH> {

    Context context;
    private static final String TAG = "ChannelsFragment";

    public ChanelsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<ChannelsModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChannelsModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChannelsModel oldItem, @NonNull ChannelsModel newItem) {
                    return oldItem.getID() == newItem.getID();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ChannelsModel oldItem, @NonNull ChannelsModel newItem) {
                    return Objects.equals(oldItem, newItem);
                }
            };


    @NonNull
    @Override
    public ParentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParentVH holder, int position) {
        ChannelsModel model = getItem(position);
        if (model != null) {
            Glide.with(context).load(model.getChannelImg()).placeholder(R.color.transparent).into(holder.image);
            holder.title.setText(model.getChannelName());
            holder.epg.setText(model.getChannelGroup());

            holder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra("url", model.getChannelUrl()).putExtra("name", model.getChannelName()));
            });
        }
    }

    public class ParentVH extends RecyclerView.ViewHolder {
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
