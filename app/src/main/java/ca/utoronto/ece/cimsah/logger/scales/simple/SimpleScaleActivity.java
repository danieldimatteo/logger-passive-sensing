package ca.utoronto.ece.cimsah.logger.scales.simple;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ca.utoronto.ece.cimsah.logger.BuildConfig;
import ca.utoronto.ece.cimsah.logger.R;
import timber.log.Timber;


public class SimpleScaleActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    public static final String KEY_SCALE_ACTIVITY_RESULTS = "key_activity_results";
    private static final String STATE_ANSWERS = "user_answers";
    private static final String STATE_QUESTION = "user_current_question";
    private static final String STATE_HELP_VISIBLE = "help_visible";

    private FloatingActionButton mFab = null;
    private long mLastClickTime = 0; // this is to prevent double-clicking of the FAB
    private ProgressBar mProgressBar = null;
    private int mCurrentQuestion = 0;
    private ArrayList<Integer> mAnswers = null;
    private boolean mHelpMenuItemVisible = false;


    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_INSTRUCTIONS ="arg_instructions";
    public static final String ARG_QUESTIONS = "arg_questions";

    private String[] mQuestions;
    private int mNumQuestions;
    private String mTitle;
    private String mInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);
        Toolbar toolbar = findViewById(R.id.scale_toolbar);
        setSupportActionBar(toolbar);

        // get arguments
        Bundle b = getIntent().getExtras();
        mQuestions = b.getStringArray(ARG_QUESTIONS);
        mNumQuestions = mQuestions.length;
        mTitle = b.getString(ARG_TITLE);
        mInstructions = b.getString(ARG_INSTRUCTIONS);

        getSupportActionBar().setTitle(mTitle);

        mProgressBar = findViewById(R.id.scale_progress_bar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.setMax(mNumQuestions);
        }

        mFab = findViewById(R.id.scale_fab);

        if (mFab != null) {
            mFab.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
            mFab.hide();
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // mis-clicking prevention, using threshold of 1000 ms
                    // this is necessary because you can actually click fast enough to register
                    // two clicks before the FAB disappears, causing you to skip over a question
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    nextQuestion();
                }
            });
        }


        if (findViewById(R.id.scale_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {

                // Create an instance of ExampleFragment
                SimpleIntroFragment introFragment = SimpleIntroFragment.newInstance(mInstructions);

                // Add the fragment to the 'fragment_container' FrameLayout
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.scale_fragment_container, introFragment);
                transaction.commit();
            }
        }

        if (savedInstanceState == null) {
            mAnswers = new ArrayList<>();
            for (int i = 0; i < mNumQuestions; i++) {
                mAnswers.add(new Integer(-1));
            }
        } else {
            mAnswers = savedInstanceState.getIntegerArrayList(STATE_ANSWERS);
            mCurrentQuestion = savedInstanceState.getInt(STATE_QUESTION);
            //mHelpMenuItemVisible = savedInstanceState.getBoolean(STATE_HELP_VISIBLE);
            invalidateOptionsMenu();
            if (mProgressBar != null) {
                mProgressBar.setMax(mNumQuestions);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setScaleY(2f);
                mProgressBar.setProgress(mCurrentQuestion);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mHelpMenuItemVisible) {
            getMenuInflater().inflate(R.menu.menu_lsas, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_help, null));
            AlertDialog helpDialog = builder.create();
            helpDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // save state
        savedInstanceState.putIntegerArrayList(STATE_ANSWERS, mAnswers);
        savedInstanceState.putInt(STATE_QUESTION, mCurrentQuestion);
        savedInstanceState.putBoolean(STATE_HELP_VISIBLE, mHelpMenuItemVisible);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }



    public void start() {
        nextQuestion();
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setScaleY(2f);
            mProgressBar.setProgress(1);
        }
        //mHelpMenuItemVisible = true;
        invalidateOptionsMenu();
    }

    public void allowNextQuestion() {
        if (mFab != null) {
            mFab.show();
            mFab.setClickable(true);
        }
    }

    private void nextQuestion() {
        if (mFab != null) {
            mFab.hide();
        }

        if (mProgressBar!= null) {
            mProgressBar.setProgress(mCurrentQuestion+1);
        }

        // get the answer before replacing fragment - the first fragment is an intro with no
        // answer, so ignore that one
        if (mCurrentQuestion != 0) {
            Fragment curFragment = getFragmentManager().findFragmentById(R.id.scale_fragment_container);
            Integer answer = ((SimpleQuestionFragment)curFragment).getAnswer();
            mAnswers.set(mCurrentQuestion - 1, answer);
        }

        // the last questions shows the FAB with a "done" symbol, so change its icon if
        // transitioning to the last question
        if (mCurrentQuestion == mNumQuestions - 1) {
            mFab.setImageResource(R.drawable.ic_done_white_24dp);
        }

        if (mCurrentQuestion < mNumQuestions) {
            // Create fragment and give it an argument specifying the article it should show
            mCurrentQuestion++;
            Integer answer = mAnswers.get(mCurrentQuestion-1);
            SimpleQuestionFragment questionFragment = SimpleQuestionFragment.newInstance(
                    mCurrentQuestion,
                    mQuestions[mCurrentQuestion-1],
                    answer,
                    mNumQuestions);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);
            transaction.replace(R.id.scale_fragment_container, questionFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else {
            // in this case we're done
            endScale();
        }
    }

    private void endScale() {
        if (BuildConfig.DEBUG && mAnswers.size() != mNumQuestions) {
            throw new RuntimeException("Scale ended with " + mAnswers.size() + " answers completed");
        }

        // clear the notification asking the user to complete the LSAS now that they finished it
        //NotificationReceiver.clearNotification(this);

        String message = "Thank you!";

        clearBackStack();
        SimpleOutroFragment outroFragment = SimpleOutroFragment.newInstance(message);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);
        transaction.replace(R.id.scale_fragment_container, outroFragment);
        transaction.commit();

        // signal success
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra(KEY_SCALE_ACTIVITY_RESULTS, mAnswers);
        setResult(RESULT_OK, intent);
    }


    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            mCurrentQuestion--;
            // if we're going back to the start, hide the FAB and the help action button
            if (mCurrentQuestion == 0) {
                if (mFab != null){
                    mFab.hide();
                }
                mHelpMenuItemVisible = false;
                invalidateOptionsMenu();
            } else if (mFab != null) {
                // otherwise make sure its not the checkmark yet
                mFab.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
            }
            // update progressbar
            if (mProgressBar != null) {
                mProgressBar.setProgress(mCurrentQuestion);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
