package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.VideoPlayerActivity;
import com.moutamid.daiptv.dialogs.AddFavortDialog;
import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.List;

public class SearchFilmsAdapter extends RecyclerView.Adapter<SearchFilmsAdapter.SearchVH> {

    Context context;
    List<ChannelsFilmsModel> list;
    private static final String TAG = "SearchAdapter";

    public SearchFilmsAdapter(Context context, List<ChannelsFilmsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchVH(LayoutInflater.from(context).inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchVH holder, int position) {
        ChannelsFilmsModel model = list.get(holder.getAbsoluteAdapterPosition());
        try {
            String link = model.getChannelImg().startsWith("/") ? Constants.getImageLink(model.getChannelImg()) : model.getChannelImg().trim();
            Glide.with(context).load(link).placeholder(R.color.transparent).into(holder.image);

            holder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra("url", model.getChannelUrl().trim()).putExtra("name", model.getChannelName()));
            });

            holder.itemView.setOnLongClickListener(v -> {
                ChannelsModel channelsModel = new ChannelsModel();
                channelsModel.setID(model.getID());
                channelsModel.setChannelGroup(model.getChannelGroup());
                channelsModel.setChannelID(model.getChannelID());
                channelsModel.setChannelName(model.getChannelName());
                channelsModel.setChannelUrl(model.getChannelUrl());
                channelsModel.setChannelImg(model.getChannelImg());
                channelsModel.setType(model.getType());
                channelsModel.setPosterUpdated(model.isPosterUpdated());
                new AddFavortDialog(context, channelsModel).show();
                return false;
            });

            holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus)
                    Log.d(TAG, "onBindViewHolder: " + model.getChannelName());
            });

        } catch (Exception e){
            Log.d(TAG, "onBindViewHolder: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SearchVH extends RecyclerView.ViewHolder{
        ImageView image;
        MaterialCardView add;
        MaterialCardView play;
        public SearchVH(@NonNull View itemView) {
            super(itemView);
            add = itemView.findViewById(R.id.add);
//            play = itemView.findViewById(R.id.play);
            image = itemView.findViewById(R.id.image);
        }
    }

}
