package ca.utoronto.ece.cimsah.logger.intro;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlidePolicy;

import ca.utoronto.ece.cimsah.logger.R;
import ca.utoronto.ece.cimsah.logger.util.Battery;
import timber.log.Timber;

public class IgnoreBatteryOptimizationFragment extends Fragment implements ISlidePolicy {

    Button batterySettingsButton;
    TextView tvInstruction0;
    TextView tvInstruction1;
    TextView tvInstruction2;
    TextView tvInstruction3;
    TextView tvInstruction4;
    TextView tvInstruction5;

    public static IgnoreBatteryOptimizationFragment newInstance() {
        IgnoreBatteryOptimizationFragment fragment = new IgnoreBatteryOptimizationFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ignore_battery_optimization, container, false);

        batterySettingsButton = view.findViewById(R.id.request_ignore_battery_optimization_button);
        batterySettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPowerSettings();
            }
        });

        tvInstruction0 = view.findViewById(R.id.tv_battery_insn0);
        tvInstruction1 = view.findViewById(R.id.tv_battery_insn1);
        tvInstruction2 = view.findViewById(R.id.tv_battery_insn2);
        tvInstruction3 = view.findViewById(R.id.tv_battery_insn3);
        tvInstruction4 = view.findViewById(R.id.tv_battery_insn4);
        tvInstruction5 = view.findViewById(R.id.tv_battery_insn5);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Battery.isIgnoringBatteryOptimizations(getActivity())) {
            hideInstructions();
        } else {
            //
            Timber.d("Showing instructions to turn off battery optimizations");
        }
    }

    private void hideInstructions() {
        if (tvInstruction0 != null) {
            tvInstruction0.setText("Battery setting is correct, please proceed to the next page!");
        }
        if (tvInstruction1 != null) {
            tvInstruction1.setVisibility(View.INVISIBLE);
        }
        if (tvInstruction2 != null) {
            tvInstruction2.setVisibility(View.INVISIBLE);
        }
        if (tvInstruction3 != null) {
            tvInstruction3.setVisibility(View.INVISIBLE);
        }
        if (tvInstruction4 != null) {
            tvInstruction4.setVisibility(View.INVISIBLE);
        }
        if (tvInstruction5 != null) {
            tvInstruction5.setVisibility(View.INVISIBLE);
        }
        if (batterySettingsButton != null) {
            batterySettingsButton.setClickable(false);
            batterySettingsButton.setVisibility(View.GONE);
        }


    }

    @TargetApi(23)
    private void openPowerSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        startActivity(intent);
    }

    /**
     * @return whether the user is allowed to leave this slide or not
     */
    @Override
    public boolean isPolicyRespected() {
        // only allow user to proceed if they successfull turned off battery optimizations
        return Battery.isIgnoringBatteryOptimizations(getActivity());
    }

    /**
     * This method gets called if the user tries to leave the slide although isPolicyRespected
     * returned false. One may show some error message here.
     */
    @Override
    public void onUserIllegallyRequestedNextPage() {
        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Please make the change to battery settings to continue in the study", Snackbar.LENGTH_LONG);
        snackBar.show();
    }
}
