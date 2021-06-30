package ca.utoronto.ece.cimsah.logger.intro;

import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.github.paolorotolo.appintro.ISlidePolicy;

import ca.utoronto.ece.cimsah.logger.R;
import ca.utoronto.ece.cimsah.logger.util.PermissionsWrapper;

/**
 * Created by dandm on 2017-01-28.
 */

public class PermissionRequestFragment extends Fragment implements ISlidePolicy {
    private static final String TAG = "PermissionRequestFragment";

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_PERMISSION = "permission";

    String title;
    String description;
    String permission;
    Button requestButton;

    public static PermissionRequestFragment newInstance(String title, String description,
                                                        String permission) {
        PermissionRequestFragment fragment = new PermissionRequestFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_PERMISSION, permission);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_TITLE)) {
                title = getArguments().getString(ARG_TITLE);
            }
            if (getArguments().containsKey(ARG_DESCRIPTION)) {
                description = getArguments().getString(ARG_DESCRIPTION);
            }
            if (getArguments().containsKey(ARG_PERMISSION)) {
                permission = getArguments().getString(ARG_PERMISSION);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission_request, container, false);

        TextView titleTextView = view.findViewById(R.id.perm_request_title);
        titleTextView.setText(title);

        TextView descriptionTextView = view.findViewById(R.id.perm_request_desc);
        descriptionTextView.setText(description);

        requestButton = view.findViewById(R.id.request_perms_button);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionsWrapper.checkSelfPermission(getActivity(), permission)
                        == PackageManager.PERMISSION_GRANTED) {
                    Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "Permission granted, please tap the arrow to continue", Snackbar.LENGTH_LONG);
                    snackBar.show();
                } else {
                    PermissionsWrapper.requestPermission(getActivity(), permission, 0);
                }
            }
        });
        return view;
    }


    /**
     * @return whether the user is allowed to leave this slide or not
     */
    @Override
    public boolean isPolicyRespected() {
        //return userAnsweredRequest;
        return (PermissionsWrapper.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * This method gets called if the user tries to leave the slide although isPolicyRespected
     * returned false. One may show some error message here.
     */
    @Override
    public void onUserIllegallyRequestedNextPage() {
        Snackbar snackBar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                "Please grant this permission to continue in the study", Snackbar.LENGTH_LONG);
        snackBar.show();
    }
}
