package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.DetailActivity;
import com.moutamid.daiptv.activities.SplashActivity;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.ArrayList;
import java.util.Objects;

public class ChildAdapter extends PagedListAdapter<ChannelsModel, ChildAdapter.ChildVH> {

    Context context;

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

    protected ChildAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }


    @NonNull
    @Override
    public ChildVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildVH(LayoutInflater.from(context).inflate(R.layout.child_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildVH holder, int position) {
        ChannelsModel model = getItem(holder.getAbsoluteAdapterPosition());
        Glide.with(context).load(model.getChannelImg()).placeholder(R.color.grey2).into(holder.image);
        holder.itemView.setOnLongClickListener(v -> {
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
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, DetailActivity.class));
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

    }

    public class ChildVH extends RecyclerView.ViewHolder{
        ImageView image;
        MaterialCardView add;
        public ChildVH(@NonNull View itemView) {
            super(itemView);
            add = itemView.findViewById(R.id.add);
            image = itemView.findViewById(R.id.image);
        }
    }

}
