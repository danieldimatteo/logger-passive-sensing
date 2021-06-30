package ca.utoronto.ece.cimsah.logger.intro;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import ca.utoronto.ece.cimsah.logger.R;
import timber.log.Timber;

public class CheckLocationSettingFragment extends Fragment implements ISlidePolicy {
    public static final int REQUEST_CHECK_SETTINGS = 10;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5*60*1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private Button checkLocationSettingButton;
    private Boolean settingIsEnabled = false;


    public static CheckLocationSettingFragment newInstance() {
        CheckLocationSettingFragment fragment = new CheckLocationSettingFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsClient = LocationServices.getSettingsClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_location_setting, container, false);

        checkLocationSettingButton = view.findViewById(R.id.check_location_setting_button);
        checkLocationSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationSettings();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void checkLocationSettings() {
        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        mSettingsClient = LocationServices.getSettingsClient(getActivity());
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                      Timber.d("Good");
                      settingIsEnabled = true;
                      locationAlreadyOn();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Timber.d("Location settings are not satisfied. Attempting to upgrade " + "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Timber.d("PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Timber.e(errorMessage);
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Timber.d("User agreed to make required location settings changes.");
                        settingIsEnabled = true;
                        break;
                    case Activity.RESULT_CANCELED:
                        Timber.d("User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    /**
     * @return whether the user is allowed to leave this slide or not
     */
    @Override
    public boolean isPolicyRespected() {
        // only allow user to proceed if they successfull turned off battery optimizations
        return settingIsEnabled;
    }

    /**
     * This method gets called if the user tries to leave the slide although isPolicyRespected
     * returned false. One may show some error message here.
     */
    @Override
    public void onUserIllegallyRequestedNextPage() {
        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Please turn on your device's Location to continue in the study", Snackbar.LENGTH_LONG);
        snackBar.show();
    }

    public void locationAlreadyOn() {
        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Thanks! Tap the arrow to continue", Snackbar.LENGTH_SHORT);
        snackBar.show();
    }
}
