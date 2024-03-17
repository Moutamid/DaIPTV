package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.DetailActivity;
import com.moutamid.daiptv.activities.DetailSeriesActivity;
import com.moutamid.daiptv.activities.SeriesActivity;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;
import java.util.Objects;

public class ChildAdapter extends PagedListAdapter<ChannelsModel, ChildAdapter.ChildVH> {

    Context context;
    private static final String TAG = "ChildAdapter";
    ItemSelected itemSelected;
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

    protected ChildAdapter(Context context, ItemSelected itemSelected) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.itemSelected = itemSelected;
    }


    @NonNull
    @Override
    public ChildVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildVH(LayoutInflater.from(context).inflate(R.layout.child_item, parent, false));
    }

    @Nullable
    @Override
    protected ChannelsModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildVH holder, int position) {
        ChannelsModel model = getItem(holder.getAbsoluteAdapterPosition());
        GlideUrl glideUrl = new GlideUrl(model.getChannelImg(), new LazyHeaders.Builder()
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit / 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36")
                .build());
        Glide.with(context).load(glideUrl).placeholder(R.color.grey2).into(holder.image);
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle("Add to Favorites")
                    .setMessage("Would you like to add this item to your Favorites list? Once added, you can easily access it later.")
                    .setPositiveButton("Add", (dialog, which) -> {
                        dialog.dismiss();
                        UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                        ArrayList<ChannelsModel> list = Stash.getArrayList(userModel.id, ChannelsModel.class);
                        list.add(model);
                    }).setNegativeButton("Close", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Stash.put(Constants.PASS, model);
            if (model.type.equals(Constants.TYPE_SERIES))
                context.startActivity(new Intent(context, DetailSeriesActivity.class));
            else
                context.startActivity(new Intent(context, DetailActivity.class));
        });

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + model.getChannelImg());
                if (hasFocus) {
                    itemSelected.selected(model);
                }
            }
        });
    }

    public class ChildVH extends RecyclerView.ViewHolder {
        ImageView image;

        public ChildVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

}
