package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.DetailActivity;
import com.moutamid.daiptv.activities.SeriesActivity;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;

public class HomeChildAdapter extends RecyclerView.Adapter<HomeChildAdapter.MovieVH> {
    private static final String TAG = "HomeChildAdapter";
    Context context;
    ArrayList<MovieModel> list;
    ItemSelected itemSelected;

    public HomeChildAdapter(Context context, ArrayList<MovieModel> list, ItemSelected itemSelected) {
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
            new AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle("Add to Favorites")
                    .setMessage("Would you like to add this item to your Favorites list? Once added, you can easily access it later.")
                    .setPositiveButton("Add", (dialog, which) -> {
                        dialog.dismiss();
                        UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                        ChannelsModel model1 = AppDatabase.getInstance(context).channelsDAO().getSearchChannel(model.original_title, model.type);
                        ArrayList<ChannelsModel> list = Stash.getArrayList(userModel.id, ChannelsModel.class);
                        list.add(model1);
                        Stash.put(userModel.id, list);
                    }).setNegativeButton("Close", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            ChannelsModel channelsModel = AppDatabase.getInstance(context).channelsDAO().getSearchChannel(model.original_title, model.type);
            Stash.put(Constants.PASS, channelsModel);
            if (model.type.equals(Constants.TYPE_SERIES))
                context.startActivity(new Intent(context, SeriesActivity.class));
            else
                context.startActivity(new Intent(context, DetailActivity.class));
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
