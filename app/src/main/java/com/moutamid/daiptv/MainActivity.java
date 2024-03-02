package com.moutamid.daiptv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fxn.stash.Stash;
import com.google.android.gms.common.Feature;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.daiptv.activities.EditProfileActivity;
import com.moutamid.daiptv.activities.ManageProfileActivity;
import com.moutamid.daiptv.activities.MyListActivity;
import com.moutamid.daiptv.adapters.ParentAdapter;
import com.moutamid.daiptv.databinding.ActivityMainBinding;
import com.moutamid.daiptv.fragments.ChannelsFragment;
import com.moutamid.daiptv.fragments.FilmFragment;
import com.moutamid.daiptv.fragments.HomeFragment;
import com.moutamid.daiptv.fragments.RechercheFragment;
import com.moutamid.daiptv.fragments.SeriesFragment;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.EPGModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.models.ProgrammeModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.models.XMLModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.Features;
import com.moutamid.daiptv.utilis.VolleySingleton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    UserModel userModel;
    public MaterialCardView toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        updateAndroidSecurityProvider();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        binding.profile.setOnClickListener(this::showMenu);
        binding.ancher.setOnClickListener(this::showMenu);

        toolbar = binding.toolbar;

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();

        get();

        binding.Accueil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.checkFeature(MainActivity.this, Features.HOME);
                    binding.indicatorAccueil.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
                } else {
                    binding.indicatorAccueil.setVisibility(View.GONE);
                }
            }
        });

        binding.Chaines.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.checkFeature(MainActivity.this, Features.CHANNELS);
                    binding.indicatorChaines.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChannelsFragment()).commit();
                } else {
                    binding.indicatorChaines.setVisibility(View.GONE);
                }
            }
        });

        binding.Films.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.checkFeature(MainActivity.this, Features.FILMS);
                    binding.indicatorFilms.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FilmFragment()).commit();
                } else {
                    binding.indicatorFilms.setVisibility(View.GONE);
                }
            }
        });

        binding.series.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.checkFeature(MainActivity.this, Features.SERIES);
                    binding.indicatorSeries.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SeriesFragment()).commit();
                } else {
                    binding.indicatorSeries.setVisibility(View.GONE);
                }
            }
        });

        binding.Recherche.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.checkFeature(MainActivity.this, Features.RECHERCHE);
                    binding.indicatorRecherche.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new RechercheFragment()).commit();
                } else {
                    binding.indicatorRecherche.setVisibility(View.GONE);
                }
            }
        });

        binding.Accueil.setOnClickListener(v -> {
            Constants.checkFeature(MainActivity.this, Features.HOME);
            binding.indicatorAccueil.setVisibility(View.VISIBLE);
            binding.indicatorChaines.setVisibility(View.GONE);
            binding.indicatorFilms.setVisibility(View.GONE);
            binding.indicatorSeries.setVisibility(View.GONE);
            binding.indicatorRecherche.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
        });
        binding.Chaines.setOnClickListener(v -> {
            Constants.checkFeature(MainActivity.this, Features.CHANNELS);
            binding.indicatorAccueil.setVisibility(View.GONE);
            binding.indicatorChaines.setVisibility(View.VISIBLE);
            binding.indicatorFilms.setVisibility(View.GONE);
            binding.indicatorSeries.setVisibility(View.GONE);
            binding.indicatorRecherche.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChannelsFragment()).commit();
        });
        binding.Films.setOnClickListener(v -> {
            Constants.checkFeature(MainActivity.this, Features.FILMS);
            binding.indicatorAccueil.setVisibility(View.GONE);
            binding.indicatorChaines.setVisibility(View.GONE);
            binding.indicatorFilms.setVisibility(View.VISIBLE);
            binding.indicatorSeries.setVisibility(View.GONE);
            binding.indicatorRecherche.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FilmFragment()).commit();
        });
        binding.series.setOnClickListener(v -> {
            Constants.checkFeature(MainActivity.this, Features.SERIES);
            binding.indicatorAccueil.setVisibility(View.GONE);
            binding.indicatorChaines.setVisibility(View.GONE);
            binding.indicatorFilms.setVisibility(View.GONE);
            binding.indicatorSeries.setVisibility(View.VISIBLE);
            binding.indicatorRecherche.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SeriesFragment()).commit();
        });
        binding.Recherche.setOnClickListener(v -> {
            Constants.checkFeature(MainActivity.this, Features.RECHERCHE);
            binding.indicatorAccueil.setVisibility(View.GONE);
            binding.indicatorChaines.setVisibility(View.GONE);
            binding.indicatorFilms.setVisibility(View.GONE);
            binding.indicatorSeries.setVisibility(View.GONE);
            binding.indicatorRecherche.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new RechercheFragment()).commit();
        });

    }

    public interface ApiService {
        @GET("/xmltv.php?username=sHnEqTKwSbGnKRzq&password=gNXzbCNSykk693zt&type=m3u_plus&output=mpegts")
        Call<XMLModel> getData();
    }

    private void get() {
        Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            String url = "http://infinity-ott.com:8080/xmltv.php?username=sHnEqTKwSbGnKRzq&password=gNXzbCNSykk693zt&type=m3u_plus&output=mpegts";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("TAGGER", "onResponse/45: data loaded");
                    Log.d("TAGGER", "onResponse/45: data: : " + response);
                    Log.d("TAGGER", "onResponse/45: length: : " + response.length());

                    try {
                        String xmlContent = response.toString();
                        Log.d(TAG, "XML : " + xmlContent);
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

                        // Get the root element
                        Element root = document.getDocumentElement();
                        // Get a NodeList of programme elements
                        NodeList programmeList = root.getElementsByTagName("programme");
                        Log.d(TAG, "programmeList: " + programmeList.getLength());
                        // Loop through the NodeList and retrieve values
                        ArrayList<EPGModel> epgList = Stash.getArrayList(Constants.EPG, EPGModel.class);
                        for (int i = 0; i < programmeList.getLength(); i++) {
                            Node programmeNode = programmeList.item(i);
                            if (programmeNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element programmeElement = (Element) programmeNode;

                                // Get attributes
                                String start = programmeElement.getAttribute("start");
                                String stop = programmeElement.getAttribute("stop");
                                String channel = programmeElement.getAttribute("channel");

                                // Get child elements
                                String title = programmeElement.getElementsByTagName("title").item(0).getTextContent();
                                String desc = programmeElement.getElementsByTagName("desc").item(0).getTextContent();

                                EPGModel epgModel = new EPGModel(start, stop, channel, title);
                                epgList.add(epgModel);
                                Stash.put(Constants.EPG, epgList);
                                // Print information

                                Log.d(TAG, "getEPG: Programme " + (i + 1));

                                System.out.println("Start: " + start);
                                System.out.println("Stop: " + stop);
                                System.out.println("Channel: " + channel);
                                System.out.println("Title: " + title);
                                System.out.println("Description: " + desc);
                                System.out.println();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.toString());
                }
            });
            queue.add(stringRequest);
        }).start();
    }

    private void epg() {
        RequestQueue requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.GET, "http://infinity-ott.com:8080/xmltv.php?username=sHnEqTKwSbGnKRzq&password=gNXzbCNSykk693zt&type=m3u_plus&output=mpegts", response -> {
            if (!response.isEmpty()) {
                Log.d(TAG, "epg: loaded");
            }
        }, error -> {
            Log.d(TAG, "epg: " + error.networkResponse.statusCode);
        });
        requestQueue.add(request);
    }

    private void getEPG() {
        new Thread(() -> {
            try {
                String url = "http://infinity-ott.com:8080";
                Log.d(TAG, "getEPG: ");
//                URL url = new URL(url");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                // Read the XML response
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                StringBuilder responseBuilder = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    responseBuilder.append(line);
//                }
//                reader.close();
                /////////////////////////////////////////
//                OkHttpClient client = new OkHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("text/plain");
//                RequestBody body = RequestBody.create(mediaType, "");
//                Request request = new Request.Builder()
//                        .url("http://infinity-ott.com:8080/xmltv.php?username=sHnEqTKwSbGnKRzq&password=gNXzbCNSykk693zt&type=m3u_plus&output=mpegts")
//                        .method("GET", body)
//                        .build();
//                Response response = client.newCall(request).execute();

                ApiService apiService = VolleySingleton.getClient().create(ApiService.class);
                apiService.getData().enqueue(new Callback<XMLModel>() {
                    @Override
                    public void onResponse(Call<XMLModel> call, Response<XMLModel> response) {
                        if (response.isSuccessful()) {
                            XMLModel tvModel = response.body();
                            if (tvModel != null) {
                                // Access the data
                                String generatorInfoName = tvModel.getGeneratorInfoName();
                                String generatorInfoUrl = tvModel.getGeneratorInfoUrl();

                                List<ProgrammeModel> programmeList = tvModel.getProgrammeList();
                                for (ProgrammeModel programme : programmeList) {
                                    String start = programme.getStart();
                                    String stop = programme.getStop();
                                    String channel = programme.getChannel();
                                    String title = programme.getTitle();
                                    String description = programme.getDescription();

                                    // Use the data as needed
                                    Log.d(TAG, "Title: " + title);
                                    Log.d(TAG, "Description: " + description);
                                }
                            }
                        } else {
                            // Handle unsuccessful response
                            try {
                                Log.e(TAG, response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<XMLModel> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });


/*                URL urlObject = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/xml"); // Specify XML acceptance

                InputStream content = connection.getInputStream();

                // Read the response content
                StringBuilder stringBuilder = new StringBuilder();
                int c;
                while ((c = content.read()) != -1) {
                    stringBuilder.append((char) c);
                }
                content.close();*/

                // Parse the XML content
               /* String xmlContent = response.toString();

                Log.d(TAG, "XML : " + xmlContent);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

                // Get the root element
                Element root = document.getDocumentElement();

                // Get a NodeList of programme elements
                NodeList programmeList = root.getElementsByTagName("programme");
                Log.d(TAG, "programmeList: " + programmeList.getLength());
                // Loop through the NodeList and retrieve values
                for (int i = 0; i < programmeList.getLength(); i++) {
                    Node programmeNode = programmeList.item(i);
                    if (programmeNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element programmeElement = (Element) programmeNode;

                        // Get attributes
                        String start = programmeElement.getAttribute("start");
                        String stop = programmeElement.getAttribute("stop");
                        String channel = programmeElement.getAttribute("channel");

                        // Get child elements
                        String title = programmeElement.getElementsByTagName("title").item(0).getTextContent();
                        String desc = programmeElement.getElementsByTagName("desc").item(0).getTextContent();

                        // Print information

                        Log.d(TAG, "getEPG: Programme " + (i + 1));

                        System.out.println("Start: " + start);
                        System.out.println("Stop: " + stop);
                        System.out.println("Channel: " + channel);
                        System.out.println("Title: " + title);
                        System.out.println("Description: " + desc);
                        System.out.println();
                    }
                }*/
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Error Message: " + e.getMessage());
                Log.d(TAG, "Error Stack : " + Arrays.toString(e.getStackTrace()));
            }
        }).start();
    }

    private static final String TAG = "MainActivity";

    @Override
    protected void onResume() {
        super.onResume();
        userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
    }

    private void showMenu(View view) {
        View customLayout = LayoutInflater.from(this).inflate(R.layout.custom_popup_menu, null);
        PopupWindow popupWindow = new PopupWindow(customLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        binding.ancher.animate().rotation(270f).setDuration(400).start();

        TextView name = customLayout.findViewById(R.id.name);
        name.setText(userModel.username);

        MaterialButton edit = customLayout.findViewById(R.id.edit);
        MaterialButton list = customLayout.findViewById(R.id.list);
        MaterialButton help = customLayout.findViewById(R.id.help);
        MaterialButton manage = customLayout.findViewById(R.id.manage);

        popupWindow.setOnDismissListener(() -> binding.ancher.animate().rotation(90f).setDuration(400).start());

        edit.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, EditProfileActivity.class));
        });
        manage.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, ManageProfileActivity.class));
        });
        list.setOnClickListener(v -> {
            popupWindow.dismiss();
            startActivity(new Intent(this, MyListActivity.class));
        });
        help.setOnClickListener(v -> {
            popupWindow.dismiss();
            try {
                Uri mailtoUri = Uri.parse("mailto:example123@gmail.com" +
                        "?subject=" + Uri.encode("Help & Support") +
                        "&body=" + Uri.encode("Your Complain??"));

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, mailtoUri);
                startActivity(emailIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        popupWindow.showAsDropDown(view);
    }

    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}