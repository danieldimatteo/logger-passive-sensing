package ca.utoronto.ece.cimsah.logger.scales.lsas;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ca.utoronto.ece.cimsah.logger.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class LsasIntroFragment extends Fragment {

    public LsasIntroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lsas_intro, container, false);
        Button startButton = view.findViewById(R.id.button_start_lsas);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((LsasActivity)getActivity()).start();
            }
        });

        return view;
    }
}
