package com.androidserverside.myapplication;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SportsFragment extends Fragment {
    final String SPORTNEWS_LINK = "https://newsapi.org/v2/top-headlines?country=il&category=sports&apiKey=ae6f7ce5700c4b3196f3aa546c68f74c";
    private List<Sports> Sports;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RequestQueue queue;
    final String TAG = "SPORTFRAGMENT - ERROR";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sports_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.Sport_RecyclerView);
        progressBar = rootView.findViewById(R.id.Sport_ProgressBar);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        Sports = new ArrayList<Sports>();
        final SportsAdapter sportsAdapter= new SportsAdapter(Sports);
        recyclerView.setAdapter(sportsAdapter);

        if(getActivity()!= null)
        {
            queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            final StringRequest request = new StringRequest(SPORTNEWS_LINK, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject rootObject = new JSONObject(response);
                        JSONArray results = rootObject.getJSONArray("articles");

                        for(int i = 0; i<results.length(); i++){
                            JSONObject currentObject = results.getJSONObject(i);
                            final String title = currentObject.getString("title");
                            final String urlToArticle = currentObject.getString("url");
                            final String description = currentObject.getString("description");
                            final String urlToImage = currentObject.getString("urlToImage");

                            final Sports currentSports = new Sports();
                            currentSports.setTitle(title);
                            currentSports.setDescription(description);
                            currentSports.setUrlToArticle(urlToArticle);

                            // Initialize a new ImageRequest
                            ImageRequest imageRequest = new ImageRequest(
                                    urlToImage, // Image URL
                                    new Response.Listener<Bitmap>() { // Bitmap listener
                                        @Override
                                        public void onResponse(Bitmap response) {
                                            // Do something with response
                                            currentSports.setImageBitMap(response);
                                            sportsAdapter.notifyDataSetChanged();
                                        }
                                    },
                                    0, // Image width
                                    0, // Image height
                                    Bitmap.Config.RGB_565, //Image decode configuration
                                    new Response.ErrorListener() { // Error listener
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // Do something with error response
                                            error.printStackTrace();
                                        }
                                    }
                            );
                            queue.add(imageRequest);
                            Sports.add(currentSports);
                            sportsAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "ERROR");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError) {
                        Log.e(TAG,"Update Location Request timed out.");
                    }else if (error instanceof NoConnectionError){
                        Log.e(TAG,"Update Location no connection.");
                    } else if (error instanceof AuthFailureError) {
                        Log.e(TAG,"Auth failure");
                    } else if (error instanceof ServerError) {
                        Log.e(TAG,"Server Error");
                    } else if (error instanceof NetworkError) {
                        Log.e(TAG,"Network Error");
                    } else if (error instanceof ParseError) {
                        Log.e(TAG,"Parse Error");
                    }
                }
            });

            sportsAdapter.setListener(new SportsAdapter.ArticleEventListener() {
                @Override
                public void SeeFullArticle(int position) {
                    String url = Sports.get(position).UrlToArticle;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            queue.add(request);
        }

        return rootView;

    }

    public void SeeFullArticle(int position) {
        String url = Sports.get(position).UrlToArticle;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
