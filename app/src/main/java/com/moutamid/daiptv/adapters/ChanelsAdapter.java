package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.VideoPlayerActivity;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.database.ChannelsDAO;
import com.moutamid.daiptv.dialogs.AddFavortDialog;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.EPGModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

            Glide.with(context)
                    .load(model.getChannelImg().trim()).placeholder(R.color.transparent)
                    .into(holder.image);
//            try {
//                String link = model.getChannelImg();
//                GlideUrl glideUrl = new GlideUrl(link, new LazyHeaders.Builder()
//                        .addHeader("User-Agent",
//                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit / 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36")
//                        .build());
//                Glide.with(context).load(glideUrl).placeholder(R.color.transparent).into(holder.image).onStart();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            List<EPGModel> epgList = AppDatabase.getInstance(context).epgDAO().getTitle(model.getChannelID().trim());
            Log.d(TAG, "onBindViewHolder: " + epgList.size());
            for (EPGModel e : epgList){
                Date startDate = Constants.parseDate(e.getStart());
                Date endDate = Constants.parseDate(e.getStop());
                if (Constants.isCurrentDateInBetween(startDate, endDate)) {
                    holder.epg.setText(e.getTitle());
                    break;
                }
            }

            holder.title.setText(model.getChannelName());

            holder.itemView.setOnClickListener(v -> {
                AppDatabase.getInstance(context).recentDAO().insert(model);
                context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra("url", model.getChannelUrl()).putExtra("name", model.getChannelName()));
            });

            holder.itemView.setOnLongClickListener(v -> {
                new AddFavortDialog(context, model).show();
                return false;
            });

            holder.add.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setCancelable(true)
                        .setTitle("Ajouter aux Favoris")
                        .setMessage("Souhaitez-vous ajouter cet article à votre liste de favoris ? Une fois ajouté, vous pourrez facilement y accéder plus tard.")
                        .setPositiveButton("Ajouter", (dialog, which) -> {
                            dialog.dismiss();
                            UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                            ArrayList<ChannelsModel> list = Stash.getArrayList(userModel.id, ChannelsModel.class);
                            list.add(model);
                            Stash.put(userModel.id, list);
                        }).setNegativeButton("Fermer", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            });
        }
    }

    public class ParentVH extends RecyclerView.ViewHolder {
        TextView title, epg;
        ImageView image;
        MaterialCardView add;

        public ParentVH(@NonNull View itemView) {
            super(itemView);
            add = itemView.findViewById(R.id.add);
            title = itemView.findViewById(R.id.title);
            epg = itemView.findViewById(R.id.epg);
            image = itemView.findViewById(R.id.image);
        }
    }

}
