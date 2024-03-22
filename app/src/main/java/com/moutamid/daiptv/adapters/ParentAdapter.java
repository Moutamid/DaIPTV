package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.utilis.CircularLayoutManager;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

import java.util.ArrayList;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentVH> {
    private static final String TAG = "ParentAdapter";
    Context context;
    ChannelViewModel itemViewModel;
    ArrayList<ParentItemModel> list;
    LifecycleOwner viewLifecycleOwner;
    String type;
    ItemSelected itemSelected;

    public ParentAdapter(Context context, ArrayList<ParentItemModel> list, String type, ChannelViewModel itemViewModel, LifecycleOwner viewLifecycleOwner, ItemSelected itemSelected) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.itemViewModel = itemViewModel;
        this.viewLifecycleOwner = viewLifecycleOwner;
        this.itemSelected = itemSelected;
    }

    @NonNull
    @Override
    public ParentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentVH(LayoutInflater.from(context).inflate(R.layout.parent_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParentVH holder, int position) {
        ParentItemModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.name);

        holder.childRC.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.childRC.setHasFixedSize(false);
        ChildAdapter adapter = new ChildAdapter(context, itemSelected);
        holder.childRC.setAdapter(adapter);

        if (model.isRoom){
            itemViewModel.getItemsByGroup(model.name, type).observe(viewLifecycleOwner, adapter::submitList);
        } else {
            if (type.equals(Constants.TYPE_MOVIE)) {
                itemViewModel.getTopFilms().observe(viewLifecycleOwner, adapter::submitList);
            } else {
                itemViewModel.getTopSeries().observe(viewLifecycleOwner, adapter::submitList);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ParentVH extends RecyclerView.ViewHolder{
        TextView name;
        RecyclerView childRC;
        public ParentVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            childRC = itemView.findViewById(R.id.childRC);
        }
    }

}
