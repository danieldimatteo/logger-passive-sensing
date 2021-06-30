package ca.utoronto.ece.cimsah.logger.intro;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

import ca.utoronto.ece.cimsah.logger.R;
import ca.utoronto.ece.cimsah.logger.authentication.EmailPasswordActivity;
import ca.utoronto.ece.cimsah.logger.util.Setup;


public class IntroActivity extends AppIntro2 {
    private final static String TAG = "IntroActivity";
    private static final int LOGIN_REQUEST = 0;
    public static final int RESULT_REQUEST_CODE = 1;
    private CheckLocationSettingFragment checkLocationSettingFragment;

    String[] slideDescriptions = {
        "This app requires access to your device's location. Please click the button below to approve this request before moving on to the next page. If you do not consent, please uninstall the app and withdraw from the study.",
        "This app requires access to your device's microphone. Please click the button below to approve this request before moving on to the next page. If you do not consent, please uninstall the app and withdraw from the study.",
        "This app requires access to your device's calendar. Please click the button below to approve this request before moving on to the next page. If you do not consent, please uninstall the app and withdraw from the study.",
        "This app requires access to your device's contacts. Please click the button below to approve this request before moving on to the next page. If you do not consent, please uninstall the app and withdraw from the study.",
    };

    String[] slideTitles = {
        "Location",
        "Audio",
        "Calendar",
        "Contacts",
    };

    String[] slideColors = {
        "#33ccff",
        "#33ccff",
        "#33ccff",
        "#33ccff"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add the two intro slides that do nothing other than offer some text
        addSlide(AppIntro2Fragment.newInstance("Welcome", "Thank you for installing the Logger " +
                "app! We need to take a few steps to set up the app",
                R.drawable.ic_account_circle_blue_grey_800_48dp, Color.parseColor(slideColors[0])));

        addSlide(AppIntro2Fragment.newInstance("Allowing data collection", "The following " +
                "screens will ask you for permission to collect data. Please make sure you have read and agreed to the study description before continuing.",
                R.drawable.ic_account_circle_blue_grey_800_48dp, Color.parseColor(slideColors[0])));

        // slide to guide user through battery optimization - only exists for sdk level 23 and above
        if (Build.VERSION.SDK_INT >= 23) {
            addSlide(IgnoreBatteryOptimizationFragment.newInstance());
        }

        // add permission-requesting slides
        addSlide(PermissionRequestFragment.newInstance(slideTitles[0], slideDescriptions[0], Manifest.permission.ACCESS_FINE_LOCATION));
        checkLocationSettingFragment = new CheckLocationSettingFragment();
        addSlide(checkLocationSettingFragment);
        addSlide(PermissionRequestFragment.newInstance(slideTitles[1], slideDescriptions[1], Manifest.permission.RECORD_AUDIO));
        addSlide(PermissionRequestFragment.newInstance(slideTitles[2], slideDescriptions[2], Manifest.permission.READ_CALENDAR));
        addSlide(PermissionRequestFragment.newInstance(slideTitles[3], slideDescriptions[3], Manifest.permission.READ_CONTACTS));

        // Declare a new image view
        ImageView imageView = new ImageView(this);

        // Set background color
        imageView.setBackgroundColor(Color.parseColor("#33ccff"));

        // Set layout params
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Bind the background to the intro
        setBackgroundView(imageView);

        skipButtonEnabled = false;
    }

    @Override
    public void onDonePressed(Fragment cf) {
        super.onDonePressed(cf);

        Intent intent = new Intent(getActivity(), EmailPasswordActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
    }


    @Override
    final protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode);
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                Setup.setIntroCompleted(this);
                Intent returnResult = new Intent();
                setResult(RESULT_OK, returnResult);
                finish();
            } else {
                Intent returnResult = new Intent();
                setResult(RESULT_CANCELED, returnResult);
                finish();
            }
        } else if (requestCode == CheckLocationSettingFragment.REQUEST_CHECK_SETTINGS) {
            checkLocationSettingFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public IntroActivity getActivity() {
        return this;
    }
}
