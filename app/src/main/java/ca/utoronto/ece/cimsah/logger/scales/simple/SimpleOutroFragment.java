package ca.utoronto.ece.cimsah.logger.scales.simple;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.utoronto.ece.cimsah.logger.R;

public class SimpleOutroFragment extends Fragment {

    private static final String ARG_MESSAGE = "arg_message";
    private String mMessage;

    public SimpleOutroFragment() {
    }

    public static SimpleOutroFragment newInstance(String message) {
        SimpleOutroFragment fragment = new SimpleOutroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessage = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lsas_outro, container, false);
        Button endButton = view.findViewById(R.id.button_end_lsas);

        endButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        TextView tv = view.findViewById(R.id.textView_outro_message);
        tv.setText(mMessage);

        return view;
    }
}
