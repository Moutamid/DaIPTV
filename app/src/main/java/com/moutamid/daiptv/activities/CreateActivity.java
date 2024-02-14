package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.models.ChannelsGroupModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.models.SeriesGroupModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.databinding.ActivityCreateBinding;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.FileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {
    ActivityCreateBinding binding;
    private static final String TAG = "FileReader";
    private final String EXT_INF_SP = "#EXTINF:";
    private final String TVG_NAME = "tvg-name=";
    private final String TVG_LOGO = "tvg-logo=";
    private final String GROUP_TITLE = "group-title=";
    private final String COMMA = ",";
    private final String HTTP = "http://";
    private final String HTTPS = "https://";
    private AppDatabase database;
    private ArrayList<ChannelsModel> channelList;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        channelList = new ArrayList<>();

        database = AppDatabase.getInstance(this);

        new ReadFileAsyncTask("m3u_data.txt").execute();
    }

    private class ReadFileAsyncTask extends AsyncTask<Void, Integer, List<ChannelsModel>> {
        private String fileName;
        int totalLines = 500000;
        private final WeakReference<TextView> progressTextView;
        private final WeakReference<TextView> message;

        ReadFileAsyncTask(String fileName) {
            this.fileName = fileName;
            this.progressTextView = new WeakReference<>(binding.progress);
            this.message = new WeakReference<>(binding.message);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressTextView != null && progressTextView.get() != null) {
                // Update your TextView with the progress
                progressTextView.get().setText(values[0] + "%");
            }
        }

        @Override
        protected ArrayList<ChannelsModel> doInBackground(Void... params) {

            InputStream inputStreamReader = null;
            BufferedReader bufferedReader = null;
            int i = 0;
            try {
                inputStreamReader = activity.getAssets().open(fileName);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStreamReader));

                String currentLine;
                ChannelsModel channel = new ChannelsModel();
                while ((currentLine = bufferedReader.readLine()) != null) {
                    i++;
                    int progress = (int) ((i / (float) totalLines) * 100);
                    publishProgress(progress);

                    currentLine = currentLine.replaceAll("\"", "");

                    if (currentLine.startsWith(EXT_INF_SP)) {
                        channel.setChannelName(currentLine.split(TVG_NAME).length > 1 ? currentLine.split(TVG_NAME)[1].split(TVG_LOGO)[0] : currentLine.split(COMMA)[1]);
                        Log.d(TAG, "ChannelName: " + channel.getChannelName());
                        channel.setChannelGroup(currentLine.split(GROUP_TITLE)[1].split(COMMA)[0]);
                        Log.d(TAG, "getChannelGroup: " + channel.getChannelGroup());
                        channel.setChannelImg(currentLine.split(TVG_LOGO).length > 1 ? currentLine.split(TVG_LOGO)[1].split(GROUP_TITLE)[0] : "");
                        Log.d(TAG, "getChannelImg: " + channel.getChannelImg());
                        continue;
                    }

                    if (currentLine.startsWith(HTTP) || currentLine.startsWith(HTTPS)) {
                        channel.setChannelUrl(currentLine);

                        String[] a = currentLine.split("8080/", 2);
                        String[] b = new String[2];
                        if (a.length > 1){
                            b = a[1].split("/", 2);
                        } else {
                            b[0] = Constants.TYPE_CHANNEL;
                        }

                        b[0] = b[0].equals(Constants.TYPE_MOVIE) || b[0].equals(Constants.TYPE_SERIES) ? b[0] : Constants.TYPE_CHANNEL;

                        channel.setType(b[0]);

                        Log.d(TAG, "getChannelUrl: " + channel.getChannelUrl());
                        channelList.add(channel);
                        database.channelsDAO().insert(channel);

                        ChannelsGroupModel groupModel = new ChannelsGroupModel(channel.getChannelGroup());
                        MoviesGroupModel moviesGroupModel = new MoviesGroupModel(channel.getChannelGroup());
                        SeriesGroupModel seriesGroupModel = new SeriesGroupModel(channel.getChannelGroup());

                        if (b[0].equals(Constants.TYPE_MOVIE)){
                            runOnUiThread(() -> {
                                if (message != null && message.get() != null) {
                                    // Update your TextView with the progress
                                    message.get().setText("Getting Films...");
                                }
                            });
                            database.moviesGroupDAO().insert(moviesGroupModel);
                        } else if (b[0].equals(Constants.TYPE_SERIES)){
                            runOnUiThread(() -> {
                                if (message != null && message.get() != null) {
                                    // Update your TextView with the progress
                                    message.get().setText("Getting Series...");
                                }
                            });
                            database.seriesGroupDAO().insert(seriesGroupModel);
                        } else {
                            runOnUiThread(() -> {
                                if (message != null && message.get() != null) {
                                    // Update your TextView with the progress
                                    message.get().setText("Getting Channels...");
                                }
                            });
                            database.channelsGroupDAO().insert(groupModel);
                        }
                        channel = new ChannelsModel();
                    }

                }
                Log.d(TAG, "Finally");
            } catch (IOException e) {
                Toast.makeText(activity, "File Read Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "readFile: "+ e.getLocalizedMessage());
            } finally {
                Log.d(TAG, "Finally");
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return channelList;
        }

        @Override
        protected void onPostExecute(List<ChannelsModel> channelsModels) {
            super.onPostExecute(channelsModels);
            if (channelList.size() == 0) {
                Toast.makeText(activity, "File Read Error", Toast.LENGTH_SHORT).show();
            } else {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }

        }
    }


}