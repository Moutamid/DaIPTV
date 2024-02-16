package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.VideoPlayerActivity;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchVH> {

    Context context;
    List<ChannelsModel> list;

    public SearchAdapter(Context context, List<ChannelsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchVH(LayoutInflater.from(context).inflate(R.layout.child_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchVH holder, int position) {
        ChannelsModel model = list.get(holder.getAbsoluteAdapterPosition());
        if (model != null){
            try {
                Glide.with(context).load(model.getChannelImg()).placeholder(R.color.grey2).into(holder.image);

                holder.play.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra("url", model.getChannelUrl()).putExtra("name", model.getChannelName()));
                });

                holder.add.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setCancelable(true)
                            .setTitle("Add to Favorites")
                            .setMessage("Would you like to add this item to your Favorites list? Once added, you can easily access it later.")
                            .setPositiveButton("Add", (dialog, which) -> {
                                dialog.dismiss();
                            }).setNegativeButton("Close", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                });
            } catch (Exception e){
                e.printStackTrace();
            }
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
            play = itemView.findViewById(R.id.play);
            image = itemView.findViewById(R.id.image);
        }
    }

}
