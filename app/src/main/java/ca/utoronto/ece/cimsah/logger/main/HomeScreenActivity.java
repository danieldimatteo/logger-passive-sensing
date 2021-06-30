package ca.utoronto.ece.cimsah.logger.main;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import ca.utoronto.ece.cimsah.logger.R;
import ca.utoronto.ece.cimsah.logger.intro.IntroActivity;
import ca.utoronto.ece.cimsah.logger.scales.ScalePanelActivity;
import ca.utoronto.ece.cimsah.logger.util.Setup;
import ca.utoronto.ece.cimsah.logger.util.Tester;
import timber.log.Timber;


public class HomeScreenActivity extends AppCompatActivity {
    private static final String TAG = "HomeScreenActivity";
    private final int INTRO_REQUEST_CODE = 1;
    private final int INTAKE_PANEL_REQUEST_CODE = 2;
    private final int EXIT_PANEL_REQUEST_CODE = 3;
    private SweetAlertDialog setupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if this is the first time we're running the app or not
        if (!Setup.isIntroCompleted(this)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivityForResult(intent, INTRO_REQUEST_CODE);
        } else if (!ScalePanelActivity.isComplete(this, ScalePanelActivity.Type.INTAKE)) {
            launchIntakeScales();
        } else {
            // app was already set up, but re-start everything in case the app was force-quit since
            // last running
            Setup.startLogging(this);
        }

        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.home_screen_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {

                // Create an instance of ExampleFragment
                HomeScreenActivityFragment homeScreenActivityFragment = new HomeScreenActivityFragment();

                // Add the fragment to the 'fragment_container' FrameLayout
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.home_screen_fragment_container, homeScreenActivityFragment);
                transaction.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homescreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTRO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Timber.d( "Intro completed successfully");
                    launchIntakeScales();
                } else {
                    finish();
                }
                break;
            case INTAKE_PANEL_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    doSetup();
                } else {
                    finish();
                }
                break;
        }
    }

    private void doSetup() {
        showSetupDialog();
        Setup.doInitialSetup(this, new Setup.SetupResult() {
            @Override
            public void onSuccess() {
                doSanityChecks();
            }

            @Override
            public void onFailure(String failureMessage) {
                dismissSetupDialog(false, failureMessage);
            }
        });
    }

    private void doSanityChecks() {
        Tester.runSanityChecks(this, new Tester.OnTesterResult() {
            @Override
            public void onComplete(Exception e) {
                if (e == null) {
                    dismissSetupDialog(true, null);
                } else {
                    dismissSetupDialog(false, e.getMessage());
                }
            }
        });
    }

    private void showSetupDialog() {
        setupDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        setupDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        setupDialog.setTitleText("Performing setup");
        setupDialog.setCancelable(false);
        setupDialog.show();
    }

    private void dismissSetupDialog(boolean successful, String message) {
        if (setupDialog != null) {
            if (successful) {
                setupDialog.setTitleText("Success!")
                        .setContentText("The app is setup and running")
                        .setConfirmText("OK")
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            } else {
                setupDialog.setTitleText("Uh-oh!")
                        .setContentText(message)
                        .setConfirmText("OK")
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.home_screen_fragment_container);
        if (currentFragment instanceof  HomeScreenActivityFragment) {
            ((HomeScreenActivityFragment) currentFragment).updateUi();
        }

    }

    private void launchIntakeScales() {
        Intent scalePanelIntent = new Intent(this, ScalePanelActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(ScalePanelActivity.ARG_WHICH_PANEL, ScalePanelActivity.Type.INTAKE);
        scalePanelIntent.putExtras(b);
        startActivityForResult(scalePanelIntent, INTAKE_PANEL_REQUEST_CODE);
    }


}
