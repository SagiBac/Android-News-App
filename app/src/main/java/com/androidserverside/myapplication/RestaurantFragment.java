package com.androidserverside.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class RestaurantFragment extends Fragment implements RestaurantAdapter.RestaurantEventListener {
    GoogleAPIService googleAPIService;
    final int LOCATION_PERMISSION_CODE = 1;
    private List<Restaurant> Restaurants;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private RestaurantAdapter RestaurantAdapter;
    private ProgressBar progressBar;
    private RequestQueue queue;
    private String FullApiURL;
    private double latitude=0, longitude=0;
    LocationManager locationManager;
    final String TAG = "Restaurants - ERROR";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        googleAPIService = new GoogleAPIService();
        View rootView = inflater.inflate(R.layout.restaurants_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.Restaurant_RecyclerView);
        progressBar = rootView.findViewById(R.id.RestaurantFragment_ProgressBar);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        Restaurants = new ArrayList<Restaurant>();
        RestaurantAdapter = new RestaurantAdapter(Restaurants);
        recyclerView.setAdapter(RestaurantAdapter);

        TurnOnGPS();
        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
        }

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(final Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                AskForRestaurantsAfterGettingLocation();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                // do something
            }

            @Override
            public void onProviderDisabled(String provider) {
                // notify user "GPS or Network provider" not available
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5000, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5000, locationListener);

        //
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Attention").setMessage("The application must have location permission")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    void AskForRestaurantsAfterGettingLocation() {
        String LocationCall = "?location=" + latitude + "," + longitude;
        String Radius = "&radius=1500";
        String Type = "&type=restaurant";
        String APIKEY = "&key=" + googleAPIService.getGOOGLE_API_KEY();
        FullApiURL = googleAPIService.getAPI_ROOT() + LocationCall + Radius + Type + APIKEY;

        if(getActivity()!= null)
        {
            queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            final StringRequest request = new StringRequest(FullApiURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject rootObject = new JSONObject(response);
                        JSONArray results = rootObject.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject currentObject = results.getJSONObject(i);
                            final String name = currentObject.getString("name");
                            final String rating = currentObject.getString("rating");

                            JSONObject currentGeoObject = currentObject.getJSONObject("geometry").getJSONObject("location");
                            final String lat = currentGeoObject.getString("lat");
                            final String lng = currentGeoObject.getString("lng");

                            JSONObject ImageDetailsJSON = currentObject.getJSONArray("photos").getJSONObject(0);
                            final int ImageHeight = ImageDetailsJSON.getInt("height");
                            final int ImageWidth = ImageDetailsJSON.getInt("width");
                            final String ImageReference = ImageDetailsJSON.getString("photo_reference");

                            final String ImageApiCallURl = googleAPIService.getGOOGLE_PLACE_GETIMAGE() + "?" + "maxwidth=" + ImageWidth + "&" + "photoreference=" + ImageReference + "&key=" + googleAPIService.getGOOGLE_API_KEY();

                            final Restaurant currentRestaurant = new Restaurant();
                            currentRestaurant.setName(name);
                            currentRestaurant.setRating(rating);
                            currentRestaurant.setLatitude(lat);
                            currentRestaurant.setLongitude(lng);

                            // Initialize a new ImageRequest
                            final ImageRequest imageRequest = new ImageRequest(
                                    ImageApiCallURl, // Image URL
                                    new Response.Listener<Bitmap>() { // Bitmap listener
                                        @Override
                                        public void onResponse(Bitmap response) {
                                            // Do something with response
                                            currentRestaurant.setImage(response);
                                            RestaurantAdapter.notifyDataSetChanged();
                                        }
                                    },
                                    0, // Image width
                                    0, // Image height
                                    // Image scale type
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
                            Restaurants.add(currentRestaurant);
                            RestaurantAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError) {
                        Log.e(TAG, "Update Location Request timed out.");
                    } else if (error instanceof NoConnectionError) {
                        Log.e(TAG, "Update Location no connection.");
                    } else if (error instanceof AuthFailureError) {
                        Log.e(TAG, "Auth failure");
                    } else if (error instanceof ServerError) {
                        Log.e(TAG, "Server Error");
                    } else if (error instanceof NetworkError) {
                        Log.e(TAG, "Network Error");
                    } else if (error instanceof ParseError) {
                        Log.e(TAG, "Parse Error");
                    }
                }
            });
            RestaurantAdapter.setListener(new RestaurantAdapter.RestaurantEventListener() {
                @Override
                public void GoToRestaurant(int position) {
                    Toast.makeText(getActivity(), Restaurants.get(position).getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + Restaurants.get(position).getLatitude() + "," + Restaurants.get(position).getLongitude()));
                    startActivity(intent);
                }
            });
            queue.add(request);
        }
    }

    @Override
    public void GoToRestaurant(int position) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", latitude, longitude, Restaurants.get(position).getLatitude(), Restaurants.get(position).getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    protected void TurnOnGPS()
    {
        LocationManager service = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
           GPSRequestDialog();
        }
    }

    public void GPSRequestDialog()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_fragment);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = dialog.findViewById(R.id.dialog_title_text);
        TextView message = dialog.findViewById(R.id.dialog_message);
        Button dialogButton = dialog.findViewById(R.id.dialog_button_ok);

        title.setText("GPS is not enabled!");
        message.setText("This application requires a GPS connection for its features.\n\n" +
                "You'll be navigated to GPS settings. \n" +
                "Please click on OK button and enable GPS!");
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
    });
            dialog.show();
    }
}