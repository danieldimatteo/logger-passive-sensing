package ca.utoronto.ece.cimsah.logger.scales.sds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.R;

public class SdsActivity extends AppCompatActivity implements OnSdsFragmentFinishedListener {
    private static final String TAG = "SdsActivity";
    public static final String KEY_SDS_ACTIVITY_RESULT = "KEY_SDS_RESULT";
    private static final String KEY_CURRENT_FRAGMENT = "KEY_CURRENT_FRAGMENT";
    private static final String KEY_SDS_RESULTS = "KEY_SDS_RESULTS";
    private static final String KEY_SDS_ISCOMPLETE = "sds_iscomplete";
    private static final String KEY_SDS_TIMESTAMP = "sds_timestamp";
    private int currentFragment = 0; // start counting at zero
    private final int NUM_FRAGMENTS = 4;
    private SdsResult sdsResults = new SdsResult();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sds);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // fragments
        if (findViewById(R.id.sds_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sds_fragment_container, getCurrentSdsFragment()).commit();
        }
    }


    private Fragment getCurrentSdsFragment() {
        Fragment fragment;
        switch (currentFragment) {
            case 0:
                fragment = SdsWorkFragment.newInstance(sdsResults);
                break;
            case 1:
                fragment = SdsSocialLifeFragment.newInstance(sdsResults);
                break;
            case 2:
                fragment = SdsFamilyLifeFragment.newInstance(sdsResults);
                break;
            case 3:
                fragment = SdsDaysLostFragment.newInstance(sdsResults);
                break;
            default:
                fragment = SdsWorkFragment.newInstance(sdsResults);
                break;
        }
        return fragment;
    }

    @Override
    public void onFinished(OnSdsFragmentFinishedListener.QuestionType questionType,
                           SdsResult result) {
        currentFragment++;
        sdsResults = result;
        if (currentFragment < NUM_FRAGMENTS) {
            // Create fragment and give it an argument specifying the article it should show
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.sds_fragment_container, getCurrentSdsFragment());
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else {
            setSdsCompleted(this);
            Intent intent = new Intent();
            intent.putExtra(KEY_SDS_ACTIVITY_RESULT, sdsResults);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_CURRENT_FRAGMENT, currentFragment);
        savedInstanceState.putParcelable(KEY_SDS_RESULTS, sdsResults);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragment = savedInstanceState.getInt(KEY_CURRENT_FRAGMENT);
        sdsResults = savedInstanceState.getParcelable(KEY_SDS_RESULTS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        currentFragment--;
    }

    public static boolean isSdsCompleted(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);

        return prefs.getBoolean(KEY_SDS_ISCOMPLETE, false);
    }

    public static void setSdsCompleted(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SDS_ISCOMPLETE, true);
        editor.putLong(KEY_SDS_TIMESTAMP, System.currentTimeMillis());
        editor.commit();
    }
}
