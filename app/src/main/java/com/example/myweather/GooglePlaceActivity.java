package com.example.myweather;

import android.*;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import android.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GooglePlaceActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    final public static int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 111;

    private static final int GOOGLE_API_CLIENT_ID = 0;

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private TextView mPlaceDetailsText;

    private TextView mPlaceDetailsAttribution;

    private Button mCurrentLocation;

    private Button  mOk;

    private static final String TAG = "MainActivity";

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private String choosePlaceName;

    private String choosePlaceLatLng;

    private LocationListener locationListener = null;
    private LocationManager locationManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this/*, GOOGLE_API_CLIENT_ID  clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        setContentView(R.layout.activity_google_place);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

        // CurrentLocation
        mCurrentLocation = (Button) findViewById(R.id.btn_current_location);
        mCurrentLocation.setOnClickListener(mOnClickListener);

        mOk= (Button)findViewById(R.id.btn_ok);
        mOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                Editor editor = sp.edit();
                Set<String> cities = sp.getStringSet("cities", null);
                if(cities == null){
                    cities = new HashSet();
                    cities.add(choosePlaceName);
                    editor.putStringSet("cities", cities);
                    editor.putString(choosePlaceName, choosePlaceLatLng);
                    editor.apply();
                }
                else if(sp.getString(choosePlaceName, null) == null){
                    cities.add(choosePlaceName);
                    editor.putStringSet("cities", cities);
                    editor.putString(choosePlaceName, choosePlaceLatLng);
                    editor.apply();
                }
                else {
                    Toast.makeText(GooglePlaceActivity.this,
                            "You already choose this city ", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (ContextCompat.checkSelfPermission(GooglePlaceActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GooglePlaceActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
            else{
                getCurrentLocation();
            }
/*            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            if (result == null) {
                Toast.makeText(GooglePlaceActivity.this,
                        "Couldn't get current location result ", Toast.LENGTH_SHORT).show();
            } else {

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        if (!likelyPlaces.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Toast.makeText(GooglePlaceActivity.this,
                                    "get status is not success", Toast.LENGTH_SHORT).show();
                            likelyPlaces.release();
                            return;
                        }
                        Place place = likelyPlaces.get(0).getPlace();
                        String placeName = String.format("%s", place.getName());
                        String placeAddress = String.format("%s", place.getAddress());
                        String[] detail = placeAddress.split(",");
                        choosePlaceName = detail[1];
                        choosePlaceLatLng = Double.toString(place.getLatLng().latitude) + "," + Double.toString(place.getLatLng().longitude);
                        mPlaceDetailsText.setText(placeName);
                        mPlaceDetailsAttribution.setText(placeAddress);

                        likelyPlaces.release();
                    }
                });
            }*/
        }
    };

    public void getCurrentLocation(){
        if (ContextCompat.checkSelfPermission(GooglePlaceActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GooglePlaceActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 500, locationListener); //every 5000millison，500m change will be detected
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentCity(location.getLatitude(),location.getLongitude());
    }

    public void currentCity(double lat, double lon){
        Geocoder geocoder = new Geocoder(GooglePlaceActivity.this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if(addressList.size() > 0){
                choosePlaceName = addressList.get(0).getLocality();
                choosePlaceLatLng = Double.toString(lat) + "," + Double.toString(lon);
                mPlaceDetailsText.setText(choosePlaceName);
                mPlaceDetailsAttribution.setText(choosePlaceLatLng);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
     //       final String placeId = String.valueOf(item.placeId);
            final String placeId = item.placeId;
            Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            
/*            choosePlaceLatLng = Double.toString(place.getLatLng().latitude) + "," + Double.toString(place.getLatLng().longitude);
            choosePlaceName = place.getAddress().toString(); */
            currentCity(place.getLatLng().latitude, place.getLatLng().longitude);

            // Format details of the place for display and show it in a TextView.
         /*   mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri())); */
            mPlaceDetailsText.setText( place.getName() + choosePlaceLatLng + choosePlaceName);


            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString(), Html.FROM_HTML_MODE_LEGACY));
            }

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        if (Build.VERSION.SDK_INT >= 24)
            return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri), Html.FROM_HTML_MODE_LEGACY);
        else
            return null;

    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
        GooglePlaceActivity.this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getCurrentLocation();
                } else {

                    Toast.makeText(GooglePlaceActivity.this,
                            "current location request denied ", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
