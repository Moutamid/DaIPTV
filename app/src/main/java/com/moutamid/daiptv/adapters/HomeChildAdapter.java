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
import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.DetailActivity;
import com.moutamid.daiptv.activities.DetailSeriesActivity;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.dialogs.AddFavortDialog;
import com.moutamid.daiptv.lisetenrs.ItemSelectedHome;
import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;

public class HomeChildAdapter extends RecyclerView.Adapter<HomeChildAdapter.MovieVH> {
    private static final String TAG = "HomeChildAdapter";
    Context context;
    ArrayList<MovieModel> list;
    ItemSelectedHome itemSelected;

    public HomeChildAdapter(Context context, ArrayList<MovieModel> list, ItemSelectedHome itemSelected) {
        this.context = context;
        this.list = list;
        this.itemSelected = itemSelected;
    }

    @NonNull
    @Override
    public MovieVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieVH(LayoutInflater.from(context).inflate(R.layout.child_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVH holder, int position) {
        MovieModel model = list.get(holder.getAbsoluteAdapterPosition());
        Glide.with(context).load(Constants.getImageLink(model.banner)).placeholder(R.color.grey2).into(holder.image);
        holder.itemView.setOnLongClickListener(v -> {
            if (model.type.equals(Constants.TYPE_SERIES)) {
                ChannelsSeriesModel model1 = AppDatabase.getInstance(context).seriesDAO().getSearchChannel(model.original_title);
                ChannelsModel channelsModel = getSeriesModel(model1, model);
                new AddFavortDialog(context, channelsModel).show();
            } else {
                ChannelsFilmsModel model1 = AppDatabase.getInstance(context).filmsDAO().getSearchChannel(model.original_title);
                ChannelsModel channelsModel = getFilmsModel(model1, model);
                new AddFavortDialog(context, channelsModel).show();
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (model.type.equals(Constants.TYPE_SERIES)) {
                ChannelsSeriesModel channelsModel = AppDatabase.getInstance(context).seriesDAO().getSearchChannel(model.original_title);
                Stash.put(Constants.PASS_SERIES, channelsModel);
                context.startActivity(new Intent(context, DetailSeriesActivity.class));
            } else {
                ChannelsFilmsModel model1 = AppDatabase.getInstance(context).filmsDAO().getSearchChannel(model.original_title);
                ChannelsModel channelsModel = getFilmsModel(model1, model);
                Stash.put(Constants.PASS, channelsModel);
                context.startActivity(new Intent(context, DetailActivity.class));
            }
        });

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + model.original_title);
                if (hasFocus) {
                    ChannelsModel channelsModel = new ChannelsModel();
                    channelsModel.setChannelName(model.original_title);
                    channelsModel.setChannelGroup(model.type);
                    itemSelected.selected(channelsModel);
                }
            }
        });
    }

    @NonNull
    private static ChannelsModel getSeriesModel(ChannelsSeriesModel model1, MovieModel model) {
        ChannelsModel channelsModel = new ChannelsModel();
        if (model1 != null){
            channelsModel.setChannelID(model1.getChannelID());
            channelsModel.setChannelImg(model1.getChannelImg());
            channelsModel.setChannelName(model1.getChannelName());
            channelsModel.setType(model1.getType());
            channelsModel.setPosterUpdated(model1.isPosterUpdated());
            channelsModel.setChannelGroup(model1.getChannelGroup());
            channelsModel.setChannelUrl(model1.getChannelUrl());
        } else {
            channelsModel.setChannelImg(model.banner);
            channelsModel.setChannelName(model.original_title);
            channelsModel.setType(model.type);
            channelsModel.setChannelGroup(model.type);
        }
        return channelsModel;
    }
    @NonNull
    private static ChannelsModel getFilmsModel(ChannelsFilmsModel model1, MovieModel model) {
        ChannelsModel channelsModel = new ChannelsModel();
        if (model1 != null){
            channelsModel.setChannelID(model1.getChannelID());
            channelsModel.setChannelImg(model1.getChannelImg());
            channelsModel.setChannelName(model1.getChannelName());
            channelsModel.setType(model1.getType());
            channelsModel.setPosterUpdated(model1.isPosterUpdated());
            channelsModel.setChannelGroup(model1.getChannelGroup());
            channelsModel.setChannelUrl(model1.getChannelUrl());
        } else {
            channelsModel.setChannelImg(model.banner);
            channelsModel.setChannelName(model.original_title);
            channelsModel.setType(model.type);
            channelsModel.setChannelGroup(model.type);
        }
        return channelsModel;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MovieVH extends RecyclerView.ViewHolder{
        ImageView image;
        public MovieVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

}
