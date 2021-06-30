package ca.utoronto.ece.cimsah.logger.scales.simple;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.utoronto.ece.cimsah.logger.R;

public class SimpleIntroFragment extends Fragment {

    public static final String ARG_INSTRUCTIONS = "arg_instructions";

    private String mInstructions;

    public SimpleIntroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scale_intro, container, false);
        Button startButton = view.findViewById(R.id.button_start_scale);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((SimpleScaleActivity)getActivity()).start();
            }
        });

        TextView tv = view.findViewById(R.id.textView_scale_intro);
        tv.setText(mInstructions);

        return view;
    }

    public static SimpleIntroFragment newInstance(String scaleInstructions) {
        SimpleIntroFragment fragment = new SimpleIntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INSTRUCTIONS, scaleInstructions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInstructions = getArguments().getString(ARG_INSTRUCTIONS);
        }
    }
}
