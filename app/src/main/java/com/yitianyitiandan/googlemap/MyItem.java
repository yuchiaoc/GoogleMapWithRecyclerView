package com.yitianyitiandan.googlemap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private int index;
    private LatLng position;
    private String title;
    private String snippet;

    public MyItem(double lat, double lng) {
        position = new LatLng(lat, lng);
    }

    public MyItem(int index, LatLng position, String title, String snippet) {
        this.index = index;
        this.position = position;
        this.title = title;
        this.snippet = snippet;
    }

    public int getIndex() {return index; }
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
