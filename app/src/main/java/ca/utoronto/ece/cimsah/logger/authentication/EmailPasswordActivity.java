package ca.utoronto.ece.cimsah.logger.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.R;
import timber.log.Timber;

/**
 * Note that the user is asked to supply a username, not an email address.
 * We append the domain of the Logger backend to the username to make it look like an email
 * address to the Firebase authentication module
 */
public class EmailPasswordActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPasswordActivity";

    public SweetAlertDialog mProgressDialog;

    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Timber.d( "onAuthStateChanged:signed_in: %s", user.getUid());
                } else {
                    // User is signed out
                    Timber.d("onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn(final String email, final String password) {

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            User user = new User(getApplicationContext());
                            user.setUsername(email);
                            user.setPassword(password);
                            user.setUid(task.getResult().getUser().getUid());
                            detectAndSaveStudyType(email);
                            returnResult();
                        } else {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                mProgressDialog.setTitleText("Login failed");
                                mProgressDialog.setContentText("Please try re-entering your username and password");
                                mProgressDialog = null;
                            }
                        }
                    }
                });

    }

    private void returnResult() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.email_sign_in_button) {
            // append email domain to username for form the full email address
            String email = mEmailField.getText().toString()
                    + LoggerProperties.getInstance().getEmailDomain();
            signIn(email, mPasswordField.getText().toString());
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mProgressDialog.setTitleText("Signing in");
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void detectAndSaveStudyType(String email) {
        Pattern pattern = Pattern.compile("^start\\d{4}"
                + LoggerProperties.getInstance().getEmailDomain());
        Matcher matcher = pattern.matcher(email);
        if (matcher.find()) {
            LoggerProperties.getInstance().setStudyType(this, LoggerProperties.StudyType.START_CLINIC);
        } else {
            LoggerProperties.getInstance().setStudyType(this, LoggerProperties.StudyType.PROLIFIC);
        }
    }

}
