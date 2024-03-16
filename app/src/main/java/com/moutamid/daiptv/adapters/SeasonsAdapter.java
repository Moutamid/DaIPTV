package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.lisetenrs.SeasonClicked;
import com.moutamid.daiptv.models.SeasonsItem;

import java.util.ArrayList;

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.SeasonsVH> {
    Context context;
    ArrayList<SeasonsItem> list;
    SeasonClicked seasonClicked;

    public SeasonsAdapter(Context context, ArrayList<SeasonsItem> list, SeasonClicked seasonClicked) {
        this.context = context;
        this.list = list;
        this.seasonClicked = seasonClicked;
    }

    @NonNull
    @Override
    public SeasonsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeasonsVH(LayoutInflater.from(context).inflate(R.layout.seasons_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonsVH holder, int position) {
        SeasonsItem seasonsItem = list.get(holder.getAbsoluteAdapterPosition());
        String s = seasonsItem.season.replace("S", "Saison ");
        holder.seasonNo.setText(s);
        holder.episodeNo.setText(seasonsItem.episodeCount + " Episodes");

        holder.itemView.setOnClickListener(v -> seasonClicked.clicked(holder.getAbsoluteAdapterPosition()));

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
