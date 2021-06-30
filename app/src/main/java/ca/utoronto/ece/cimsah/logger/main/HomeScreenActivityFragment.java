package ca.utoronto.ece.cimsah.logger.main;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.io.IOException;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.R;
import ca.utoronto.ece.cimsah.logger.audio.AudioRecorderService;
import ca.utoronto.ece.cimsah.logger.encrypt.TestEncryptedUpload;
import ca.utoronto.ece.cimsah.logger.notifications.Schedule;
import ca.utoronto.ece.cimsah.logger.scales.ScalePanelActivity;
import ca.utoronto.ece.cimsah.logger.sync.SyncCallback;
import ca.utoronto.ece.cimsah.logger.sync.SyncManager;
import ca.utoronto.ece.cimsah.logger.util.NetworkHelper;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeScreenActivityFragment extends Fragment implements Button.OnClickListener {
    private final static String TAG = "HomeScreenFragment";

    private TextView inProgressTextView;
    private TextView toCompleteHeading;
    private TextView toCompleteInstructions;
    private TextView toWithdrawHeading;
    private TextView toWithdrawInstructions;


    private Button completeScalesButton;

    private TextView completeScalesTextView;

    private TextView thanksTextView;
    private TextView completionCodeTextView;
    private TextView completionUrlHeadingTextView;
    private TextView completionUrlLinkTextView;

    public HomeScreenActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        inProgressTextView = view.findViewById(R.id.home_screen_in_progress);
        toCompleteHeading = view.findViewById(R.id.home_screen_to_complete_heading);
        toCompleteInstructions = view.findViewById(R.id.home_screen_to_complete_instructions);
        toWithdrawHeading = view.findViewById(R.id.home_screen_to_withdraw_heading);
        toWithdrawInstructions = view.findViewById(R.id.home_screen_to_withdraw_instructions);

        completeScalesButton = view.findViewById(R.id.home_screen_do_exit_scales_button);
        completeScalesButton.setOnClickListener(this);

        completeScalesTextView = view.findViewById(R.id.home_screen_complete_scales_tv);

        thanksTextView = view.findViewById(R.id.home_screen_thank_you);
        completionCodeTextView = view.findViewById(R.id.home_screen_completion_code);
        completionUrlHeadingTextView = view.findViewById(R.id.home_screen_completion_URL_heading);
        completionUrlLinkTextView = view.findViewById(R.id.home_screen_completion_URL_link);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        updateUi();
    }

    public void updateUi() {
        Resources res = getResources();
        Schedule schedule = new Schedule(getActivity());

        inProgressTextView.setVisibility(View.VISIBLE);
        toCompleteHeading.setVisibility(View.VISIBLE);
        toCompleteInstructions.setVisibility(View.VISIBLE);
        toWithdrawHeading.setVisibility(View.VISIBLE);
        toWithdrawInstructions.setVisibility(View.VISIBLE);



        if (LoggerProperties.getInstance().getStudyType(getActivity()) == LoggerProperties.StudyType.PROLIFIC) {
            toWithdrawInstructions.setText(res.getString(R.string.withdraw_instructions_prolific));
            String progressText = String.format(
                    res.getString(R.string.in_progress_message_prolific),
                    schedule.getPrettyDateAndTimeOfExitInterview());

            toCompleteInstructions.setText(progressText);
        } else {
            toWithdrawInstructions.setText(res.getString(R.string.withdraw_instructions_clinic));
            String progressText = String.format(
                    res.getString(R.string.in_progress_message_clinic),
                    schedule.getPrettyDateAndTimeOfExitInterview());

            toCompleteInstructions.setText(progressText);
        }

        completeScalesTextView.setVisibility(View.GONE);
        completeScalesButton.setVisibility(View.GONE);

        thanksTextView.setVisibility(View.GONE);
        completionCodeTextView.setVisibility(View.GONE);
        completionUrlHeadingTextView.setVisibility(View.GONE);
        completionUrlLinkTextView.setVisibility(View.GONE);

        Timber.d("schedule.trialTimeLineComplete() = %b", schedule.trialTimelineComplete());
        if (schedule.trialTimelineComplete()) {
            inProgressTextView.setVisibility(View.GONE);
            toCompleteHeading.setVisibility(View.GONE);
            toCompleteInstructions.setVisibility(View.GONE);
            toWithdrawHeading.setVisibility(View.GONE);
            toWithdrawInstructions.setVisibility(View.GONE);
            if (!ScalePanelActivity.isComplete(getActivity(), ScalePanelActivity.Type.EXIT)) {
                completeScalesTextView.setVisibility(View.VISIBLE);
                completeScalesButton.setVisibility(View.VISIBLE);
            } else {
                // trial done and exit interviews done
                completeScalesTextView.setVisibility(View.GONE);
                completeScalesButton.setVisibility(View.GONE);
                thanksTextView.setVisibility(View.VISIBLE);
                if (LoggerProperties.getInstance().getStudyType(getActivity()) == LoggerProperties.StudyType.PROLIFIC) {
                    thanksTextView.setText(res.getString(R.string.study_complete_message_prolific));
                    // show completion codes
                    completionCodeTextView.setVisibility(View.VISIBLE);
                    completionCodeTextView.setText("Completion code: " + LoggerProperties.getInstance().getCompletionCode());
                    completionUrlHeadingTextView.setVisibility(View.VISIBLE);
                    completionUrlLinkTextView.setVisibility(View.VISIBLE);
                    completionUrlLinkTextView.setText(LoggerProperties.getInstance().getCompletionUrl());
                } else {
                    thanksTextView.setText(res.getString(R.string.study_complete_message_clinic));
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_screen_do_exit_scales_button:
                launchExitScales();
                break;
        }
    }


    private void launchExitScales() {
        Intent scalePanelIntent = new Intent(getActivity(), ScalePanelActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(ScalePanelActivity.ARG_WHICH_PANEL, ScalePanelActivity.Type.EXIT);
        scalePanelIntent.putExtras(b);
        startActivity(scalePanelIntent);
    }

    private void testEncryptedUpload() {
        TestEncryptedUpload testEncryptedUpload = new TestEncryptedUpload();
        testEncryptedUpload.execute(getActivity());
    }

    private void recordAudio() {
        Intent service = new Intent(getActivity(), AudioRecorderService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            getActivity().startForegroundService(service);
        } else {
            getActivity().startService(service);
        }
    }

    private void attemptManualSync() {
        if (NetworkHelper.connectedToWifi(getActivity())) {
            SyncManager syncManager = new SyncManager(getActivity());
            syncManager.sync(new SyncCallback() {
                @Override
                public void onSyncComplete(IOException e) {
                    if (e != null) {
                        Timber.e(e);
                    } else {
                        Timber.d("Sync successful!");
                    }
                }
            });

        }
    }


}
