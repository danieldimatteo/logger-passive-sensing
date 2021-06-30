package ca.utoronto.ece.cimsah.logger.scales.sds;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.xw.repo.BubbleSeekBar;

import ca.utoronto.ece.cimsah.logger.R;

public class SdsWorkFragment extends Fragment {
    private static final String KEY_SDS_RESULTS = "KEY_SDS_RESULTS";
    private SdsResult sdsResults;
    private BubbleSeekBar workDisruptionScale = null;
    private CheckBox unrelatedToDisorderCheckbox = null;

    private OnSdsFragmentFinishedListener mListener;

    public SdsWorkFragment() {
        // Required empty public constructor
    }

    public static SdsWorkFragment newInstance(SdsResult results) {
        SdsWorkFragment fragment = new SdsWorkFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_SDS_RESULTS, results);
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
        View view = inflater.inflate(R.layout.fragment_sds_work, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab_sds_work);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });
        workDisruptionScale = view.findViewById(R.id.seekBar_work);
        unrelatedToDisorderCheckbox = view.findViewById(R.id.checkBox);
        sdsResults = getArguments().getParcelable(KEY_SDS_RESULTS);
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            sdsResults.setWorkDisruption(workDisruptionScale.getProgress());
            sdsResults.setNoWorkUnrelatedToDisorder(unrelatedToDisorderCheckbox.isChecked());
            mListener.onFinished(OnSdsFragmentFinishedListener.QuestionType.WORK, sdsResults);
        }
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
