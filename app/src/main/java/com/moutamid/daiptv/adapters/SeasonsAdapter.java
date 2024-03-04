package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.models.SeasonsItem;

import java.util.ArrayList;

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.SeasonsVH> {
    Context context;
    ArrayList<SeasonsItem> list;

    public SeasonsAdapter(Context context, ArrayList<SeasonsItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SeasonsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeasonsVH(LayoutInflater.from(context).inflate(R.layout.seasons_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonsVH holder, int position) {
        SeasonsItem seasonsItem = list.get(holder.getAbsoluteAdapterPosition());
        holder.seasonNo.setText(seasonsItem.season);
        holder.episodeNo.setText(seasonsItem.episodeCount + " Episodes");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SeasonsVH extends RecyclerView.ViewHolder{
        TextView seasonNo, episodeNo;
        public SeasonsVH(@NonNull View itemView) {
            super(itemView);
            seasonNo = itemView.findViewById(R.id.seasonNo);
            episodeNo = itemView.findViewById(R.id.episodeNo);
        }
    }

}
