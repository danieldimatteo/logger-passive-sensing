package ca.utoronto.ece.cimsah.logger.scales.sds;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.utoronto.ece.cimsah.logger.R;

public class SdsDaysLostFragment extends Fragment {
    private static final String KEY_SDS_RESULTS = "KEY_SDS_RESULTS";
    private SdsResult sdsResults;
    private TextInputEditText daysLostEditText = null;
    private TextInputEditText daysUnproductiveEditText = null;


    private OnSdsFragmentFinishedListener mListener;

    public SdsDaysLostFragment() {
        // Required empty public constructor
    }

    public static SdsDaysLostFragment newInstance(SdsResult sdsResults) {
        SdsDaysLostFragment fragment = new SdsDaysLostFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_SDS_RESULTS, sdsResults);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sds_dayslost, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab_sds_dayslost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });
        daysLostEditText = view.findViewById(R.id.et_days_lost);
        daysUnproductiveEditText = view.findViewById(R.id.et_days_unproductive);

        sdsResults = getArguments().getParcelable(KEY_SDS_RESULTS);

        return view;
    }

    public void onButtonPressed() {
        if (validInput() && mListener != null) {
            sdsResults.setDaysLost(Integer.parseInt(daysLostEditText.getText().toString()));
            sdsResults.setDaysUnproductive(Integer.parseInt(daysUnproductiveEditText.getText().toString()));
            mListener.onFinished(OnSdsFragmentFinishedListener.QuestionType.DAYS_LOST, sdsResults);
        }
    }

    private boolean validInput() {
        daysLostEditText.setError(null);
        daysUnproductiveEditText.setError(null);

        boolean valid = true;
        View focusView = null;

        String daysLostString =  daysLostEditText.getText().toString();
        String daysUnproductiveString = daysUnproductiveEditText.getText().toString();

        if (daysLostString.isEmpty()) {
            daysLostEditText.setError("Please enter a number from 0 to 7");
            focusView = daysLostEditText;
            valid = false;
        } else if (daysUnproductiveString.isEmpty()) {
            daysUnproductiveEditText.setError("Please enter a number from 0 to 7");
            focusView = daysUnproductiveEditText;
            valid = false;
        } else {
            int daysLost = Integer.parseInt(daysLostString);
            int daysUnproductive = Integer.parseInt(daysUnproductiveString);
            if (daysLost < 0 || daysLost > 7) {
                daysLostEditText.setError("Please enter a number from 0 to 7");
                focusView = daysLostEditText;
                valid = false;
            } else if (daysUnproductive < 0 || daysUnproductive > 7) {
                daysUnproductiveEditText.setError("Please enter a number from 0 to 7");
                focusView = daysUnproductiveEditText;
                valid = false;
            }
        }
        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSdsFragmentFinishedListener) {
            mListener = (OnSdsFragmentFinishedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
