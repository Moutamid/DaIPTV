package com.moutamid.daiptv.utilis;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.UserModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    private static final String TAG = "FileReader";
    private final String EXT_INF_SP = "#EXTINF:";
    private final String TVG_NAME = "tvg-name=";
    private final String TVG_LOGO = "tvg-logo=";
    private final String GROUP_TITLE = "group-title=";
    private final String COMMA = ",";
    private final String HTTP = "http://";
    private final String HTTPS = "https://";
    private final String fileName;
    private final List<ChannelsModel> channelList;
    private final Activity activity;

    public FileReader(Activity activity, String fileName) {
        this.activity = activity;
        this.fileName = fileName;
        this.channelList = new ArrayList<>();
    }

    public void readFile() {
        InputStream inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = activity.getAssets().open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStreamReader));

            String currentLine;

            ChannelsModel channel = new ChannelsModel();
            while ((currentLine = bufferedReader.readLine()) != null) {
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
                    String[] b = a[1].split("/", 2);

                    b[0] = b[0].equals("movie") || b[0].equals("series") ? b[0] : "channel";

                    channel.setType(b[0]);

                    Log.d(TAG, "getChannelUrl: " + channel.getChannelUrl());
                    channelList.add(channel);
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
                if (channelList.size() > 0) {
                    Stash.put(Constants.ChannelList, channelList);
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                } else {
                    Toast.makeText(activity, "File Read Error", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
