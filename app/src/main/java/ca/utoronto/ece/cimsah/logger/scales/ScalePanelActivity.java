package ca.utoronto.ece.cimsah.logger.scales;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.R;
import ca.utoronto.ece.cimsah.logger.model.ScaleResult;
import ca.utoronto.ece.cimsah.logger.notifications.NotificationReceiver;
import ca.utoronto.ece.cimsah.logger.scales.lsas.LsasActivity;
import ca.utoronto.ece.cimsah.logger.scales.lsas.LsasAnswer;
import ca.utoronto.ece.cimsah.logger.scales.sds.SdsActivity;
import ca.utoronto.ece.cimsah.logger.scales.sds.SdsResult;
import ca.utoronto.ece.cimsah.logger.scales.simple.SimpleScaleActivity;
import ca.utoronto.ece.cimsah.logger.sync.FirestoreWriter;
import timber.log.Timber;

public class ScalePanelActivity extends AppCompatActivity {
    private final static String TAG = "ScalePanelActivity";

    public final static String ARG_WHICH_PANEL = "arg_which_panel";

    private final int SDS_REQUEST_CODE = 1;
    private final int PHQ9_REQUEST_CODE = 2;
    private final int GAD7_REQUEST_CODE = 3;
    private final int LSAS_REQUEST_CODE = 4;

    private Type mWhichPanel;
    private CurrentScale mCurrentScale = CurrentScale.SDS;

    private final String KEY_WHICH_PANEL = "key_which_panel";
    private final String KEY_CURRENT_SCALE = "key_current_scale";

    private static final String KEY_INTAKE_PANEL_COMPLETE = "intake_panel_complete";
    private static final String KEY_EXIT_PANEL_COMPLETE = "exit_panel_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_panel);
        Toolbar toolbar = findViewById(R.id.scale_panel_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Questionnaires");

        Button startButton = findViewById(R.id.button_start_scale_panel);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPanel();
            }
        });

        Bundle args = getIntent().getExtras();
        if (args != null) {
            mWhichPanel = (Type) args.getSerializable(ARG_WHICH_PANEL);
        }
        Timber.d(mWhichPanel.toString());
    }

    private void startPanel() {
        mCurrentScale.launch(this);
    }

    // get the results of each scale and save them to Firestore
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FirestoreWriter firestoreWriter = new FirestoreWriter();
        switch (requestCode) {
            case SDS_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    SdsResult sdsResult = data.getParcelableExtra(SdsActivity.KEY_SDS_ACTIVITY_RESULT);
                    ScaleResult scaleResult = new ScaleResult(new Date(System.currentTimeMillis()),
                            mWhichPanel, sdsResult);
                    firestoreWriter.saveNewScaleResult(scaleResult);
                    launchNextScale();
                }
                break;
            case PHQ9_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    ArrayList<Integer> answers = data.getIntegerArrayListExtra(SimpleScaleActivity.KEY_SCALE_ACTIVITY_RESULTS);
                    ScaleResult scaleResult = new ScaleResult(new Date(System.currentTimeMillis()),
                            mWhichPanel, "PHQ-9", answers);
                    firestoreWriter.saveNewScaleResult(scaleResult);
                    launchNextScale();
                }
                break;
            case GAD7_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    ArrayList<Integer> answers = data.getIntegerArrayListExtra(SimpleScaleActivity.KEY_SCALE_ACTIVITY_RESULTS);
                    ScaleResult scaleResult = new ScaleResult(new Date(System.currentTimeMillis()),
                            mWhichPanel, "GAD-7", answers);
                    firestoreWriter.saveNewScaleResult(scaleResult);
                    launchNextScale();
                }
                break;
            case LSAS_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    ArrayList<LsasAnswer> answers = data.getParcelableArrayListExtra(LsasActivity.KEY_LSAS_RESULTS);
                    ScaleResult scaleResult = new ScaleResult(new Date(System.currentTimeMillis()),
                            mWhichPanel, answers);
                    firestoreWriter.saveNewScaleResult(scaleResult);
                    launchNextScale();
                }
                break;
        }
    }

    private void launchNextScale(){
        mCurrentScale = mCurrentScale.next();
        if (mCurrentScale != null) {
            mCurrentScale.launch(this);
        } else {
            // we're done
            completePanel();
        }
    }

    private void completePanel() {
        setComplete(this, mWhichPanel);
        if (mWhichPanel == Type.EXIT) {
            NotificationReceiver.clearNotification(this);
            NotificationReceiver.cancelNotifications(this);
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(KEY_WHICH_PANEL, mWhichPanel);
        savedInstanceState.putSerializable(KEY_CURRENT_SCALE, mCurrentScale);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWhichPanel = (Type) savedInstanceState.getSerializable(KEY_WHICH_PANEL);
        mCurrentScale = (CurrentScale) savedInstanceState.getSerializable(KEY_CURRENT_SCALE);
    }

    private void setComplete(Context context, Type whichPanel) {
        SharedPreferences prefs = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (whichPanel == Type.INTAKE) {
            editor.putBoolean(KEY_INTAKE_PANEL_COMPLETE, true);
        } else {
            editor.putBoolean(KEY_EXIT_PANEL_COMPLETE, true);
        }
        editor.apply();
    }

    public static boolean isComplete(Context context, Type whichPanel) {
        SharedPreferences prefs = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);
        Boolean isComplete;
        if (whichPanel == Type.INTAKE) {
            isComplete = prefs.getBoolean(KEY_INTAKE_PANEL_COMPLETE, false);
        } else {
            isComplete = prefs.getBoolean(KEY_EXIT_PANEL_COMPLETE, false);
        }
        Timber.d("ScalePanel %s isComplete = %s", whichPanel , isComplete.toString());
        return isComplete;
    }

    public enum Type{
        INTAKE, EXIT
    }

    public enum CurrentScale{
        SDS {
            @Override
            public void launch(ScalePanelActivity activity) {
                Intent intent = new Intent(activity, SdsActivity.class);
                activity.startActivityForResult(intent, activity.SDS_REQUEST_CODE);
            }
        },
        PHQ9 {
            @Override
            public void launch(ScalePanelActivity activity) {
                Intent intent = new Intent(activity, SimpleScaleActivity.class);
                Bundle b = new Bundle();
                Resources resources = activity.getResources();

                String instructions;
                String[] questions;
                String title;

                if (LoggerProperties.getInstance().getStudyType(activity) == LoggerProperties.StudyType.PROLIFIC) {
                    instructions = resources.getString(R.string.phq8_instructions);
                    questions = resources.getStringArray(R.array.phq8_questions_array);
                    title = resources.getString(R.string.phq8_title);
                } else {
                    instructions = resources.getString(R.string.phq9_instructions);
                    questions = resources.getStringArray(R.array.phq9_questions_array);
                    title = resources.getString(R.string.phq9_title);
                }

                b.putStringArray(SimpleScaleActivity.ARG_QUESTIONS, questions);
                b.putString(SimpleScaleActivity.ARG_INSTRUCTIONS, instructions);
                b.putString(SimpleScaleActivity.ARG_TITLE, title);
                intent.putExtras(b);
                activity.startActivityForResult(intent, activity.PHQ9_REQUEST_CODE);
            }
        },
        GAD7 {
            @Override
            public void launch(ScalePanelActivity activity) {
                Intent intent = new Intent(activity, SimpleScaleActivity.class);
                Bundle b = new Bundle();
                Resources resources = activity.getResources();
                String[] questions = resources.getStringArray(R.array.gad7_questions_array);

                b.putStringArray(SimpleScaleActivity.ARG_QUESTIONS, questions);
                b.putString(SimpleScaleActivity.ARG_INSTRUCTIONS, resources.getString(R.string.gad7_instructions));
                b.putString(SimpleScaleActivity.ARG_TITLE, resources.getString(R.string.gad7_title));
                intent.putExtras(b);
                activity.startActivityForResult(intent, activity.GAD7_REQUEST_CODE);
            }
        },
        LSAS {
            @Override
            public void launch(ScalePanelActivity activity) {
                Intent lsasIntent = new Intent(activity, LsasActivity.class);
                activity.startActivityForResult(lsasIntent, activity.LSAS_REQUEST_CODE);
            }
            @Override
            public CurrentScale next() {
                return null; // last scale in the panel
            }
        };

        public CurrentScale next() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() + 1];
        }

        public void launch(ScalePanelActivity activity) {

        }
    }


}
