package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.utilis.CircularLayoutManager;

import java.util.ArrayList;

public class ChanelsAdapter extends RecyclerView.Adapter<ChanelsAdapter.ParentVH> {

    Context context;
    ArrayList<ChannelsModel> list;

    public ChanelsAdapter(Context context, ArrayList<ChannelsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ParentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentVH(LayoutInflater.from(context).inflate(R.layout.channels_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParentVH holder, int position) {
        ChannelsModel model = list.get(holder.getAdapterPosition());
        holder.title.setText(model.title);
        holder.epg.setText(model.epg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ParentVH extends RecyclerView.ViewHolder{
        TextView title, epg;
        public ParentVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            epg = itemView.findViewById(R.id.epg);
        }
    }

}
