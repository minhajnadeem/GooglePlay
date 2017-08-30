package com.example.minhaj.googleplay;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private PackageInfo info;
    private final int RC_PLACE_PICKER = 1;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            info = getPackageManager().getPackageInfo(
                    "com.example.minhaj.googleplay", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hash_key = new String(Base64.encode(md.digest(), 0));
                Log.d("place","hash:"+hash_key);
            }

        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }

        googleApiClient = new GoogleApiClient.Builder(this).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this,2, this).build();
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();//.setLatLngBounds(new LatLngBounds(new LatLng(31.4631859, 74.414334), new LatLng(31.4631859, 74.414334)));
        try {
            startActivityForResult(intentBuilder.build(this), RC_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("place", "play service repairable");
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("place", "play service not available");
            e.printStackTrace();
        }

        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> pendingResult = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
        pendingResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                for (PlaceLikelihood placeLikelihoodBuffer : placeLikelihoods){
                    Log.d("place","place :"+placeLikelihoodBuffer.getPlace().getName());
                }
                placeLikelihoods.release();
            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PLACE_PICKER && resultCode == Activity.RESULT_OK){
            Log.d("place", "result ok");
            Place place = PlacePicker.getPlace(this,data);
            String placeName = String.format("place name is %s",place.getName());
            Toast.makeText(this, placeName, Toast.LENGTH_SHORT).show();
        }else {
            Log.d("place","result not ok :"+resultCode);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("place","connection failed");
    }
}
