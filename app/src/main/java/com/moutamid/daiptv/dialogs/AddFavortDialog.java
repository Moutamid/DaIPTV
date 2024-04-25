package com.moutamid.daiptv.dialogs;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;

public class AddFavortDialog {
    Context context;
    ChannelsModel model;

    public AddFavortDialog(Context context, ChannelsModel model) {
        this.context = context;
        this.model = model;
    }

    public void show() {
        new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle("Ajouter aux Favoris")
                .setMessage("Souhaitez-vous ajouter cet article à votre liste de favoris ? Une fois ajouté, vous pourrez facilement y accéder plus tard.")
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    dialog.dismiss();
                    UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                    ArrayList<ChannelsModel> list = Stash.getArrayList(userModel.id, ChannelsModel.class);
                    if (model!=null){
                        list.add(model);
                        Stash.put(userModel.id, list);
                    } else {
                        Toast.makeText(context, "Je ne peux pas être ajouté à la liste pour le moment", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Fermer", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}
