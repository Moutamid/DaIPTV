package com.moutamid.daiptv.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.DetailSeriesActivity;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.dialogs.AddFavortDialog;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SeriesChildAdapter extends PagedListAdapter<ChannelsSeriesModel, SeriesChildAdapter.ChildVH> {

    Context context;
    private static final String TAG = "SeriesChildAdapter";
    private static final String TAG2 = "TESTING123";
    ItemSelected itemSelected;
    String type;
    RequestQueue requestQueue;
    AppDatabase database;
    private static final DiffUtil.ItemCallback<ChannelsSeriesModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChannelsSeriesModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChannelsSeriesModel oldItem, @NonNull ChannelsSeriesModel newItem) {
                    return oldItem.getID() == newItem.getID();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ChannelsSeriesModel oldItem, @NonNull ChannelsSeriesModel newItem) {
                    return Objects.equals(oldItem, newItem);
                }
            };

    protected SeriesChildAdapter(Context context, ItemSelected itemSelected, String type) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.itemSelected = itemSelected;
        this.type = type;
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        database = AppDatabase.getInstance(context);
    }

    @Nullable
    @Override
    public PagedList<ChannelsSeriesModel> getCurrentList() {
        if (super.getCurrentList() != null) {
            Log.d(TAG2, "getCurrentList:  size " + super.getCurrentList().size());
        }
        return super.getCurrentList();
    }

    @NonNull
    @Override
    public ChildVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildVH(LayoutInflater.from(context).inflate(R.layout.series_child_item, parent, false));
    }

    @Nullable
    @Override
    protected ChannelsSeriesModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildVH holder, int position) {
        ChannelsSeriesModel model = getItem(holder.getAbsoluteAdapterPosition());
        if (model != null) {
            try {
                String link = model.getChannelImg().startsWith("/") ? Constants.getImageLink(model.getChannelImg()) : model.getChannelImg().trim();
                GlideUrl glideUrl = new GlideUrl(link, new LazyHeaders.Builder()
                        .addHeader("User-Agent",
                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit / 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36")
                        .build());
                Glide.with(context).load(glideUrl).placeholder(R.color.transparent).into(holder.image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (holder.getAbsoluteAdapterPosition() == 0 && holder.itemView.hasFocus()) {
                holder.itemView.requestFocus();
            }

            holder.itemView.setOnLongClickListener(v -> {
                ChannelsModel channelsModel = new ChannelsModel();
                channelsModel.setChannelGroup(model.getChannelGroup());
                channelsModel.setChannelID(model.getChannelID());
                channelsModel.setChannelName(model.getChannelName());
                channelsModel.setChannelUrl(model.getChannelUrl());
                channelsModel.setChannelImg(model.getChannelImg());
                channelsModel.setType(model.getType());
                channelsModel.setPosterUpdated(model.isPosterUpdated());
                new AddFavortDialog(context, channelsModel).show();
                return true;
            });

            holder.itemView.setOnClickListener(v -> {
                Stash.put(Constants.PASS_SERIES, model);
                Log.d(TAG, "onBindViewHolder: " + model.getChannelName());
                context.startActivity(new Intent(context, DetailSeriesActivity.class));
            });

            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.d(TAG2, "onFocusChange: " + model.getChannelName());
                    if (hasFocus) {
                        itemSelected.selectedSeries(model);
                    } else {
                        itemSelected.cancel();
                    }
                }
            });
        } else {
            Log.d(TAG, "onBindViewHolder: MODEL IS NULL");
        }
    }

    List<MoviesGroupModel> items = new ArrayList<MoviesGroupModel>();

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.d(TAG, "onAttachedToRecyclerView: Attached");
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        LinearLayoutManager lm = (LinearLayoutManager) manager;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int first = lm.findFirstVisibleItemPosition();
                int last = lm.findLastVisibleItemPosition();
                Log.d(TAG, "onScrolled: " + first);
                Log.d(TAG, "onScrolled: " + last);
//                    items = database.moviesGroupDAO().getAll();
//
//                    Log.d(TAG, "item size : " + items.size());
//
//                    int position = Stash.getInt("POS", 0);
//                    Log.d(TAG, "onScrolled: POS " + position);
//                    if (position > 0) {
//                        channelsList = Stash.getArrayList(items.get(position).movieGroup, ChannelsModel.class);
//                        if (!channelsList.isEmpty()) {
//                            for (int i = first; i <= last; i++) {
//                                ChannelsModel channelsModel = channelsList.get(i);
//                                //  Log.d(TAG, "onScrollChange: " + channelsModel.channelName);
//                                if (!channelsModel.isPosterUpdated) {
//                                    Log.d(TAG, "onScrollChange: changing poster for " + channelsModel.channelName);
//                                    boolean exclude = channelsModel.channelName.startsWith("|XXX|") || channelsModel.channelName.startsWith("XXX|") ||
//                                            channelsModel.channelName.startsWith("|XX|") || channelsModel.channelName.startsWith("XX|") ||
//                                            channelsModel.channelName.startsWith("|X|") || channelsModel.channelName.startsWith("X|");
//                                    if (!exclude)
//                                        makeApiCall(channelsModel, i);
//                                }
//                            }
//                        }
//                    }
            }
        });
    }

    private void makeApiCall(ChannelsModel item, int absoluteAdapterPosition) {
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

    private void getDetails(int id, ChannelsModel item, int absoluteAdapterPosition) {
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
                        database.channelsDAO().update(item.getID(), link);
                        notifyItemChanged(absoluteAdapterPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        requestQueue.add(objectRequest);
    }

    public class ChildVH extends RecyclerView.ViewHolder {
        ImageView image;

        public ChildVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

}
