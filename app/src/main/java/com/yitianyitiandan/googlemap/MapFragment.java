package com.yitianyitiandan.googlemap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.yitianyitiandan.googlemap.adapter.MapAdapter;
import com.yitianyitiandan.googlemap.model.Hospital;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = "MapFragment";
    private OnFragmentInteractionListener mListener;
    private MapView mapView;
    private GoogleMap googleMap;
    private ResponseGson hospitalGson;
    private static final String HOSPITAL_API = "https://data.tycg.gov.tw/api/v1/rest/datastore/c3f12332-0515-444e-a7aa-cf3d4ad4d1f3?format=json";
//    private static final String STORE_API = ""
    private List<Hospital> hospitals = new ArrayList<>();
    public MapFragment() {

    }

    public static MapFragment getInstance() {
        return new MapFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initMapView(savedInstanceState, view);
        network(HOSPITAL_API);
        //
        RecyclerView rv = view.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        MapAdapter adapter = new MapAdapter(getContext(),hospitals);
        rv.setAdapter(adapter);
        return view;
    }



    private void initMapView(Bundle savedInstanceState, View view) {
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void network(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            Handler mainThreadHandler = new Handler(Looper.getMainLooper());
            String str;
            @Override
            public void onFailure(Call call, final IOException e) {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Callback - 伺服器連線錯誤" + e.getMessage());
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) {
                Log.d(TAG, "NWorkCtrl callback onResponse: OK");
                try {
                    str = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        hospitalGson = gson.fromJson(str, ResponseGson.class);
                        int length = hospitalGson.result.records.size();
                        for (int i = 0; i < length; i++) {
                            ResponseGson.Records tmp = hospitalGson.result.records.get(i);
                            hospitals.add(new Hospital(tmp.zone, tmp.name, tmp.address, tmp.tel));
//                            Log.d(TAG, hospitals.get(i).getName());
                        }
                        mapView.getMapAsync(MapFragment.this);
                    }
                });
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        this.googleMap = googleMap;
        // For showing a move to my location button
//                googleMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = null;
        Log.d(TAG, "hospitals.size(): "+hospitals.size());
        for(int i = 0; i < 10; i++) {
            try {
                addressList = geocoder.getFromLocationName(hospitals.get(i).getAddress(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList == null || addressList.isEmpty()) {
                Log.d(TAG, "第"+i+"個無法找到經緯度");
            } else {
                Address address = addressList.get(0);
                LatLng position = new LatLng(address.getLatitude(), address.getLongitude());
                String snippet = hospitals.get(i).getZone();
                String title = hospitals.get(i).getName();
                Log.d(TAG, title+" "+snippet);
                this.googleMap.addMarker(new MarkerOptions().position(position).snippet(snippet).title(title));
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(14).build();
                this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
