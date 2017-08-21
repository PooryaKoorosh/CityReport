package com.example.koorosh.cityreport.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.koorosh.cityreport.G;
import com.example.koorosh.cityreport.R;
import com.example.koorosh.cityreport.StructPosts;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Koorosh on 8/15/2017.
 */

public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }
    View mView;
    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                LatLng place = null;

                for(int i=0 ; i < G.all_posts.size() ; i++)
                {
                    // For dropping a marker at a point on the Map
                    StructPosts structPosts = G.all_posts.get(i);
                     place = new LatLng(structPosts.lat, structPosts.lng);
                    Log.e("onMapReady2: ", structPosts.lat + " : " + structPosts.lng + " : " + structPosts.types.id);
                 Marker mk = googleMap.addMarker(new MarkerOptions().position(place)
                            .title(structPosts.title).snippet(structPosts.text)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(G.GetProperIcon(Integer.parseInt(structPosts.types.id)))));

                    mk.hideInfoWindow();

                }

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if(marker.isInfoWindowShown())
                        marker.showInfoWindow();
                        else
                            marker.hideInfoWindow();
                        return false;
                    }
                });

                if(place != null) {
                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
