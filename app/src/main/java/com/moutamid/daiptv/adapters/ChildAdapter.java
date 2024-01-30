package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.SplashActivity;

import java.util.ArrayList;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildVH> {

    Context context;
    ArrayList<Integer> list;

    public ChildAdapter(Context context, ArrayList<Integer> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChildVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildVH(LayoutInflater.from(context).inflate(R.layout.child_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildVH holder, int position) {
        int circularPosition = position % list.size();
        holder.image.setImageResource(list.get(circularPosition));
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

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : Integer.MAX_VALUE;
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
