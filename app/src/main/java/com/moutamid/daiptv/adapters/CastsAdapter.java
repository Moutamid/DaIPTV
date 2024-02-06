package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.models.CastModel;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.ArrayList;

public class CastsAdapter extends RecyclerView.Adapter<CastsAdapter.ParentVH> {

    Context context;
    ArrayList<CastModel> list;

    public CastsAdapter(Context context, ArrayList<CastModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ParentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentVH(LayoutInflater.from(context).inflate(R.layout.cast_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParentVH holder, int position) {
        CastModel model = list.get(holder.getAdapterPosition());
        holder.title.setText(model.title);
        holder.name.setText(model.name);
        holder.totalEpisode.setText(model.totalEpisode);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ParentVH extends RecyclerView.ViewHolder{
        TextView title, totalEpisode,name;
        public ParentVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            name = itemView.findViewById(R.id.name);
            totalEpisode = itemView.findViewById(R.id.totalEpisode);
        }
    }

}
