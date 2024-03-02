package com.moutamid.daiptv.utilis;

import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Constants {
    public static final String USER = "USER";
    public static final String PASS = "PASS";
    public static final String SELECTED_PAGE = "SELECTED_PAGE";
    public static final String USER_LIST = "USER_LIST";
    public static final String PASS_USER = "PASS_USER";
    public static final String MY_LIST = "MY_LIST";
    public static final String ChannelList = "ChannelList";
    public static final String TYPE_CHANNEL = "channel";
    public static final String TYPE_MOVIE = "movie";
    public static final String TYPE_SERIES = "series";
    public static final String TYPE_TV = "tv";
    public static final String imageLink = "https://image.tmdb.org/t/p/original";
    public static final String movieSearch = "https://api.themoviedb.org/3/search/"; // https://api.themoviedb.org/3/search/tv?query=
    public static final String movieDetails = "https://api.themoviedb.org/3/";
    public static final String[] permissions = new String[] {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    };

    public static void getPermissions(Activity context) {
        if (check(context)) {
            context.shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_VIDEO);
            context.shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            context.shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            context.shouldShowRequestPermissionRationale(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(context, permissions, 2);
        }
    }

    public static boolean check(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    public static String getImageLink(String path){
        return imageLink + path;
    }

    public static String getMovieData(String name, String type){
        name = name.replace(" " , "%20");
        String api_key = "&api_key=26bedf3e3be75a2810a53f4a445e7b1f";
        return movieSearch + type + "?query=" + name + api_key + "&include_adult=false&language=en-US&page=1";
    }
    public static String getMovieDetails(int id, String type){ // Type movie / tv
        String api_key = "?api_key=26bedf3e3be75a2810a53f4a445e7b1f";
        return movieDetails + type + "/" + id + api_key + "&append_to_response=videos,images,credits";
    }

    public static boolean checkInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    public static void checkApp(Activity activity) {
        String appName = "daiptv";

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if ((input = in != null ? in.readLine() : null) == null) break;
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> new AlertDialog.Builder(activity)
                            .setMessage(msg)
                            .setCancelable(false)
                            .show());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public static void checkFeature(Activity activity, String features) {
        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if ((input = in != null ? in.readLine() : null) == null) break;
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData);
                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");
                if (value) {
                    activity.runOnUiThread(() -> new AlertDialog.Builder(activity)
                            .setMessage(msg)
                            .setCancelable(false)
                            .show());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
