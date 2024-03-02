package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.models.SeasonsItem;

import java.util.ArrayList;

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.SeasonsVH> {
    Context context;
    ArrayList<SeasonsItem> list;

    @NonNull
    @Override
    public SeasonsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeasonsVH(LayoutInflater.from(context).inflate(R.layout.seasons_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonsVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SeasonsVH extends RecyclerView.ViewHolder{

        public SeasonsVH(@NonNull View itemView) {
            super(itemView);
        }
    }

}
