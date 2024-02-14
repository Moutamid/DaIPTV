package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.utilis.CircularLayoutManager;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

import java.util.ArrayList;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentVH> {

    Context context;
    ChannelViewModel itemViewModel;
    ArrayList<ParentItemModel> list;
    LifecycleOwner viewLifecycleOwner;

    public ParentAdapter(Context context, ArrayList<ParentItemModel> list, ChannelViewModel itemViewModel, LifecycleOwner viewLifecycleOwner) {
        this.context = context;
        this.list = list;
        this.itemViewModel = itemViewModel;
        this.viewLifecycleOwner = viewLifecycleOwner;
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

        holder.childRC.setLayoutManager(new CircularLayoutManager(context));
        holder.childRC.setHasFixedSize(false);
        ChildAdapter adapter = new ChildAdapter(context);
        holder.childRC.setAdapter(adapter);

        itemViewModel.getItemsByGroup(model.name, Constants.TYPE_MOVIE).observe(viewLifecycleOwner, adapter::submitList);

        holder.right.setOnClickListener(v -> {
            holder.childRC.smoothScrollBy(400, 0);
        });
        holder.left.setOnClickListener(v -> {
            holder.childRC.smoothScrollBy(-400, 0);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ParentVH extends RecyclerView.ViewHolder{
        TextView name;
        RecyclerView childRC;
        LinearLayout right, left;
        public ParentVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            childRC = itemView.findViewById(R.id.childRC);
            right = itemView.findViewById(R.id.right);
            left = itemView.findViewById(R.id.left);
        }
    }

}
