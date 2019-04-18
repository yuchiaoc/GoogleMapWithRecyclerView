package com.yitianyitiandan.googlemap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = "MapFragment";
    private OnFragmentInteractionListener mListener;
    private MapView mapView;
    private GoogleMap googleMap;
    private ResponseGson hospitalGson;
    private static final String HOSPITAL_API = "https://data.tycg.gov.tw/api/v1/rest/datastore/c3f12332-0515-444e-a7aa-cf3d4ad4d1f3?format=json&limit=107";
//    private static final String STORE_API = ""
    private List<Hospital> hospitals = new ArrayList<>();
    private ClusterManager<MyItem> clusterManager;
    private RecyclerView rv;
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
        rv = view.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        MapAdapter adapter = new MapAdapter(getContext(),hospitals);
        rv.setAdapter(adapter);
        new PagerSnapHelper().attachToRecyclerView(rv);
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
                        int length = 30;//hospitalGson.result.records.size();
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
        setUpUi();
        setUpClusterer();
    }

    private void setUpUi(){
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void setUpClusterer() {
        // Position the map.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.9968690,121.3226142), 14));
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        // Point the map's listeners at the listeners implemented by the cluster manager.
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {
                rv.smoothScrollToPosition(myItem.getIndex());
                return false;
            }
        });
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {
        // For dropping a marker at a point on the Map
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = null;
        Log.d(TAG, "hospitals.size(): "+hospitals.size());
        for(int i = 0; i < 30; i++) {
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
                String title = hospitals.get(i).getName();
                String snippet = hospitals.get(i).getZone();
                Log.d(TAG, title+" "+snippet);
                MyItem item = new MyItem(i, position, title, snippet);
                clusterManager.addItem(item);
//                this.googleMap.addMarker(new MarkerOptions().position(position).snippet(snippet).title(title));
            }
        }
        // For zooming automatically to the location of the marker
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(24.9968690,121.3226142)).zoom(14).build();
//        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
