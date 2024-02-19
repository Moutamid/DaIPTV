package com.moutamid.daiptv.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.fxn.stash.Stash;
import com.google.android.gms.security.ProviderInstaller;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.ActivityCreateBinding;
import com.moutamid.daiptv.models.ChannelsGroupModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.models.SeriesGroupModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    UserModel userModel;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        channelList = new ArrayList<>();

        database = AppDatabase.getInstance(this);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        PRDownloader.initialize(getApplicationContext());

        updateAndroidSecurityProvider();

        // startPRDownloader();

        // startDownloading();

        new ReadFileAsyncTask("m3u_data.txt").execute();
    }

    private void startPRDownloader() {
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        String url = userModel.url;
        String filePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        Log.d(TAG, "startDownloading: " + filePath);
        PRDownloader.download(url, filePath, "downloaded_file.m3u")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        Log.d(TAG, "onStartOrResume: Started");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        int pro = (int) ((progress.currentBytes / progress.totalBytes) * 100);
                        binding.progress.setText(pro + "%");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        binding.message.setText("Getting Channels...");
                        binding.progress.setText("0%");
                        new ReadFileAsyncTask(filePath + File.separator + "downloaded_file.m3u").execute();
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d(TAG, "onError: serverError " + error.isServerError());
                        Log.d(TAG, "onError: connectionError " + error.isConnectionError());
                        if (error.isConnectionError()) {
                            binding.message.setText(error.getConnectionException().getMessage());
                        } else if (error.isServerError()) {
                            binding.message.setText("Error : " + error.getServerErrorMessage());
                        } else {
                            binding.message.setText("Error : " + error.getResponseCode());
                        }
                        Stash.clear(Constants.USER);
                    }
                });
    }

    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startDownloading() {
        String url = userModel.url;
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "downloaded_file.m3u";
        Log.d(TAG, "startDownloading: " + filePath);

        FileRequest fileRequest = new FileRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "startDownloading: complete");
                    // File downloaded successfully
                    // You can perform further operations here
                    runOnUiThread(() -> {
                        binding.message.setText("Getting Channels...");
                        binding.progress.setText("0%");
                    });
                    new ReadFileAsyncTask(filePath).execute();
                },
                error -> {
                    Log.e(TAG, "startDownloading: error " + error.getMessage());
                    error.printStackTrace();
                    Stash.clear(Constants.USER);
                });

        requestQueue.add(fileRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownloading();
            } else {
                Toast.makeText(this, "Permission is required to get the Data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ReadFileAsyncTask extends AsyncTask<Void, Integer, List<ChannelsModel>> {
        private String fileName;
        int totalLines = 22000; // 500000
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
//                File file = new File(fileName);
//                FileInputStream fis = new FileInputStream(file);
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
                        if (a.length > 1) {
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

                        if (b[0].equals(Constants.TYPE_MOVIE)) {
                            runOnUiThread(() -> {
                                if (message != null && message.get() != null) {
                                    // Update your TextView with the progress
                                    message.get().setText("Getting Films...");
                                }
                            });
                            database.moviesGroupDAO().insert(moviesGroupModel);
                        } else if (b[0].equals(Constants.TYPE_SERIES)) {
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
                Log.d(TAG, "readFile: " + e.getLocalizedMessage());
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

    public class FileRequest extends Request<NetworkResponse> {
        private final Response.Listener<NetworkResponse> mListener;
        private final Handler mHandler;
        private long mStartTime;

        public FileRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            mListener = listener;
            mHandler = new Handler(Looper.getMainLooper());
            mStartTime = SystemClock.elapsedRealtime();
            setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            setShouldRetryServerErrors(false);
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        public byte[] getBody() {
            return null;
        }

        @Override
        public String getBodyContentType() {
            return "application/octet-stream";
        }

        @Override
        public Priority getPriority() {
            return Priority.IMMEDIATE;
        }

        @Override
        public void deliverError(VolleyError error) {
            super.deliverError(error);
        }

        @Override
        protected Map<String, String> getParams() {
            return null;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return super.getHeaders();
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
            Log.d(TAG, "deliverResponse: response");
            mHandler.post(() -> {
                long elapsedTime = SystemClock.elapsedRealtime() - mStartTime;
                int progress = (int) (elapsedTime / 1000);
                runOnUiThread(() -> binding.progress.setText(progress + "%"));
            });
        }

    }


}