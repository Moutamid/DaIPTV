package com.moutamid.daiptv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.moutamid.daiptv.activities.EditProfileActivity;
import com.moutamid.daiptv.activities.MyListActivity;
import com.moutamid.daiptv.adapters.ParentAdapter;
import com.moutamid.daiptv.databinding.ActivityMainBinding;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    UserModel userModel;
    int[] images = {R.drawable.imag1,R.drawable.imag12,R.drawable.imag13,R.drawable.imag4,R.drawable.imag5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchbar.clearFocus();
                binding.searchbar.onActionViewCollapsed();
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.profile.setOnClickListener(this::showMenu);
        binding.ancher.setOnClickListener(this::showMenu);

        ArrayList<Integer> items = new ArrayList<>();
        ArrayList<ParentItemModel> parent = new ArrayList<>();
        for (int i=0; i<=10; i++) {
            int j = new Random().nextInt(images.length);
            items.add(images[j]);
        }

        parent.add(new ParentItemModel("Horror", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Drama", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Movie", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Sci-Fi", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("News", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Sports", items));

        binding.recycler.setHasFixedSize(false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        binding.recycler.setAdapter(new ParentAdapter(this, parent));

    }

    private void search(String query) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
    }

    private void showMenu(View view) {
        View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_popup_menu, null);
        PopupWindow popupWindow = new PopupWindow(customLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        TextView name = customLayout.findViewById(R.id.name);
        name.setText(userModel.username);

        MaterialButton edit = customLayout.findViewById(R.id.edit);
        MaterialButton list = customLayout.findViewById(R.id.list);
        MaterialButton help = customLayout.findViewById(R.id.help);

        help.setOnClickListener(v -> popupWindow.dismiss());
        list.setOnClickListener(v -> popupWindow.dismiss());
        edit.setOnClickListener(v -> popupWindow.dismiss());

        edit.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, EditProfileActivity.class));
        });
        list.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, MyListActivity.class));
        });
        help.setOnClickListener(v -> {
            popupWindow.dismiss();
            Uri mailtoUri = Uri.parse("mailto:example123@gmail.com" +
                    "?subject=" + Uri.encode("Help & Support") +
                    "&body=" + Uri.encode("Your Complain??"));

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, mailtoUri);
            startActivity(emailIntent);
        });
        popupWindow.showAsDropDown(view);
    }
}