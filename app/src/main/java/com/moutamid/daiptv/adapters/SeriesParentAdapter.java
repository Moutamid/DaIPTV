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
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SeriesParentAdapter extends RecyclerView.Adapter<SeriesParentAdapter.ParentVH> {
    private static final String TAG = "SeriesParentAdapter";
    private static final String TAG2 = "TESTING123";
    Context context;
    ChannelViewModel itemViewModel;
    ArrayList<ParentItemModel> list;
    LifecycleOwner viewLifecycleOwner;
    String type;
    ItemSelected itemSelected;
    RequestQueue requestQueue;
    AppDatabase database;

    public SeriesParentAdapter(Context context, ArrayList<ParentItemModel> list, String type, ChannelViewModel itemViewModel, LifecycleOwner viewLifecycleOwner, ItemSelected itemSelected) {
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
        return new ParentVH(LayoutInflater.from(context).inflate(R.layout.parent_item, parent, false));
    }
    SeriesChildAdapter adapter;
    @Override
    public void onBindViewHolder(@NonNull ParentVH holder, int position) {
        ParentItemModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.name);

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.childRC.setLayoutManager(lm);
        holder.childRC.setHasFixedSize(false);
        adapter = new SeriesChildAdapter(context, itemSelected, type);
        holder.childRC.setAdapter(adapter);

        holder.childRC.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int first = lm.findFirstCompletelyVisibleItemPosition();
            int last = lm.findLastCompletelyVisibleItemPosition();
            ArrayList<ChannelsSeriesModel> channelsList = Stash.getArrayList(model.name, ChannelsSeriesModel.class);
            if (!channelsList.isEmpty()) {
                for (int i = first; i < last; i++) {
                    ChannelsSeriesModel channelsModel = channelsList.get(i);
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
        });

        if (model.isRoom) {
            itemViewModel.getSeries(model.name, type).observe(viewLifecycleOwner, new Observer<PagedList<ChannelsSeriesModel>>() {
                @Override
                public void onChanged(PagedList<ChannelsSeriesModel> channelsModels) {
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
            itemViewModel.getTopSeries().observe(viewLifecycleOwner, adapter::submitList);
        }
    }

    ArrayList<ChannelsSeriesModel> channelsList = new ArrayList<>();

    private void makeApiCall(ChannelsSeriesModel item, int absoluteAdapterPosition) {
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

    private void getDetails(int id, ChannelsSeriesModel item, int absoluteAdapterPosition) {
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
                        database.seriesDAO().update(item.getID(), link);
                        itemViewModel.getSeries(item.channelGroup, type).observe(viewLifecycleOwner, new Observer<PagedList<ChannelsSeriesModel>>() {
                            @Override
                            public void onChanged(PagedList<ChannelsSeriesModel> channelsModels) {
                                adapter.submitList(channelsModels);
                                // Copy items to myList
                                Log.d("LINKKK", "onChanged: " + channelsList.size());
                            }
                        });
                        Log.d("LINKKK", "absoluteAdapterPosition: " + absoluteAdapterPosition);
                        adapter.notifyItemChanged(absoluteAdapterPosition);
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

    public class ParentVH extends RecyclerView.ViewHolder {
        TextView name;
        RecyclerView childRC;

        public ParentVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            childRC = itemView.findViewById(R.id.childRC);
        }
    }

}
