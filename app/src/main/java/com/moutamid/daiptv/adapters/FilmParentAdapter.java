package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FilmParentAdapter extends RecyclerView.Adapter<FilmParentAdapter.ParentVH> {
    private static final String TAG = "ParentAdapter";
    Context context;
    ChannelViewModel itemViewModel;
    ArrayList<ParentItemModel> list;
    LifecycleOwner viewLifecycleOwner;
    String type;
    ItemSelected itemSelected;
    RequestQueue requestQueue;
    AppDatabase database;

    public FilmParentAdapter(Context context, ArrayList<ParentItemModel> list, String type, ChannelViewModel itemViewModel, LifecycleOwner viewLifecycleOwner, ItemSelected itemSelected) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.itemViewModel = itemViewModel;
        this.viewLifecycleOwner = viewLifecycleOwner;
        this.itemSelected = itemSelected;
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        database = AppDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public ParentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentVH(LayoutInflater.from(context).inflate(R.layout.film_parent_item, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    int pos;
    ChildAdapter adapter;
    @Override
    public void onBindViewHolder(@NonNull ParentVH holder, int position) {
        ParentItemModel model = list.get(holder.getAbsoluteAdapterPosition());
        pos = holder.getAbsoluteAdapterPosition();
        holder.name.setText(model.name);

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.childRC.setLayoutManager(lm);
        holder.childRC.setHasFixedSize(false);
        adapter = new ChildAdapter(context, itemSelected, type);
        holder.childRC.setAdapter(adapter);

        holder.childRC.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int first = lm.findFirstCompletelyVisibleItemPosition();
            int last = lm.findLastCompletelyVisibleItemPosition();
            ArrayList<ChannelsFilmsModel> channelsList = Stash.getArrayList(model.name, ChannelsFilmsModel.class);
            if (!channelsList.isEmpty()) {
                if (channelsList.size() > last) {
                    for (int i = first; i < last; i++) {
                        ChannelsFilmsModel channelsModel = channelsList.get(i);
                        if (channelsModel != null) {
                            if (!channelsModel.isPosterUpdated) {
                                Log.d(TAG, "onScrollChange: changing poster for " + channelsModel.channelName);
                                boolean exclude = channelsModel.channelName.startsWith("|XXX|") || channelsModel.channelName.startsWith("XXX|") ||
                                        channelsModel.channelName.startsWith("|XX|") || channelsModel.channelName.startsWith("XX|") ||
                                        channelsModel.channelName.startsWith("|X|") || channelsModel.channelName.startsWith("X|");
                                if (!exclude)
                                    makeApiCall(channelsModel, i);
                            }
                        }
                    }
                }
            }
        });

        if (model.isRoom) {
            itemViewModel.getAllFilms(model.name, type).observe(viewLifecycleOwner, new Observer<PagedList<ChannelsFilmsModel>>() {
                @Override
                public void onChanged(PagedList<ChannelsFilmsModel> channelsModels) {
                    adapter.submitList(channelsModels);
                    // Copy items to myList
                    if (holder.getAbsoluteAdapterPosition() > 0) {
                        channelsList = new ArrayList<>(channelsModels);
                        Stash.put(model.name, channelsList);
                    }
                    Log.d(TAG, "onChanged: " + channelsList.size());
                }
            });
        } else {
            if (type.equals(Constants.TYPE_MOVIE)) {
                itemViewModel.getTopFilms().observe(viewLifecycleOwner, adapter::submitList);
            }
        }
    }

    ArrayList<ChannelsFilmsModel> channelsList = new ArrayList<>();

    private void makeApiCall(ChannelsFilmsModel item, int absoluteAdapterPosition) {
        String name = Constants.regexName(item.channelName);
        Log.d(TAG, "makeApiCall: " + name);
        String type = item.type.equals(Constants.TYPE_SERIES) ? Constants.TYPE_TV : Constants.TYPE_MOVIE;
        String url = Constants.getMovieData(name, Constants.extractYear(item.channelName), type);
        Log.d(TAG, "makeApiCall: " + url);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "makeApiCall:  " + response.toString());
                        JSONArray array = response.getJSONArray("results");
                        JSONObject object = array.getJSONObject(0);
                        int id = object.getInt("id");
                        getDetails(id, item, absoluteAdapterPosition);
                    } catch (JSONException e) {
                        Log.d(TAG, "makeApiCall: Error ");
                        e.printStackTrace();
                    }
                }, error -> {
            Log.d(TAG, "ERROR: " + error.getLocalizedMessage());
            error.printStackTrace();
        });
        requestQueue.add(objectRequest);
    }

    private void getDetails(int id, ChannelsFilmsModel item, int absoluteAdapterPosition) {
        String type = item.type.equals(Constants.TYPE_SERIES) ? Constants.TYPE_TV : Constants.TYPE_MOVIE;
        String url = Constants.getMovieDetails(id, type, "");
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray images = response.getJSONObject("images").getJSONArray("posters");
                        String lang = "NULL";
                        int index = 0;

                        for (int i = 0; i < images.length(); i++) {
                            JSONObject object = images.getJSONObject(i);
                            lang = object.getString("iso_639_1");

                            if (lang.equals("fr")) {
                                Log.d(TAG, "getDetails: FR");
                                index = i;
                                break;
                            } else if (lang.equals("en") && index == 0) {
                                Log.d(TAG, "getDetails: ENG");
                                index = i;
                            } else if (lang.equals("null") && index == 0) {
                                Log.d(TAG, "getDetails: NULL");
                                index = i;
                            }
                        }

                        String poster = images.getJSONObject(index).getString("file_path");
                        String link = poster.isEmpty() ? item.getChannelImg() : poster;
                        Log.d("LINKKK", "getDetails: " + link);
                        database.channelsDAO().update(item.getID(), link);
                       // adapter = new ChildAdapter(context, itemSelected, type);
                        itemViewModel.getFilms(item.channelGroup, type).observe(viewLifecycleOwner, new Observer<PagedList<ChannelsFilmsModel>>() {
                            @Override
                            public void onChanged(PagedList<ChannelsFilmsModel> channelsModels) {
                                adapter.submitList(channelsModels);
                                // Copy items to myList
                                Log.d("LINKKK", "onChanged: " + channelsList.size());
                            }
                        });
                        Log.d("LINKKK", "absoluteAdapterPosition: " + absoluteAdapterPosition);
                       // adapter.notifyItemChanged(absoluteAdapterPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        requestQueue.add(objectRequest);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public int getPosition() {
        return pos;
    }

    public class ParentVH extends RecyclerView.ViewHolder {
        TextView name;
        RecyclerView childRC;

        public ParentVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            childRC = itemView.findViewById(R.id.filmChildRC);
        }
    }

}
