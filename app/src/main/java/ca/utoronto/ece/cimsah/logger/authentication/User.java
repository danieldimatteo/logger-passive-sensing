package ca.utoronto.ece.cimsah.logger.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.concurrent.CountDownLatch;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import timber.log.Timber;

/**
 * Created by dandm on 2016-11-21.
 */
public class User {
    private final static String TAG = "User";
    private SharedPreferences sharedPreferences;
    private static final String PREF_USERNAME = "pref_username";
    private static final String PREF_PASSWORD = "pref_password";
    private static final String PREF_UID = "pref_uid";
    private String token;
    private boolean loginSuccessful;
    private FirebaseUser firebaseUser;

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public User(Context context) {
        sharedPreferences = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);
    }

    public String getUsername() {
        return sharedPreferences.getString(PREF_USERNAME,
                LoggerProperties.getInstance().getDefaultUsername());
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USERNAME, username);
        editor.commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(PREF_PASSWORD,
                LoggerProperties.getInstance().getDefaultUsername());
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_PASSWORD, password);
        editor.commit();
    }

    public String getUid() {
        return sharedPreferences.getString(PREF_UID,
                LoggerProperties.getInstance().getDefaultUsername());
    }

    public void setUid(String uid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_UID, uid);
        editor.commit();
    }

    // this method is synchronous - don't call it from the UI thread
    public String getUserToken() throws AuthenticationException {
        final CountDownLatch latch = new CountDownLatch(1);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            signIn();
            user = firebaseAuth.getCurrentUser();
        }

        if (user == null) {
            String message = "Login to firebase failed for user " + getUsername();
            throw new AuthenticationException(message);
        }

        setFirebaseUser(user);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getFirebaseUser().getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            // save token
                            setToken(task.getResult().getToken());
                            latch.countDown();
                        } else {
                            // throw exception, but not from here (can't throw it from within the runnable ...)
                            latch.countDown();
                        }
                    }
                });

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Timber.e(e);
        }
        return getToken();
    }

    private void setToken(String token) {
        this.token = token;
    }

    private String getToken() {
        return this.token;
    }

    private void setLoginSuccessful(boolean successful) {
        this.loginSuccessful = successful;
    }

    // this method is synchronous - don't call it from the UI thread
    public boolean signIn() {
        final String KEY_LOGIN_RESULT = "KEY_LOGIN_RESULT";
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                setLoginSuccessful(b.getBoolean(KEY_LOGIN_RESULT));
                countDownLatch.countDown();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(getUsername(), getPassword())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final Message msg = new Message();
                                final Bundle b = new Bundle();
                                b.putBoolean(KEY_LOGIN_RESULT, task.isSuccessful());
                                msg.setData(b);
                                handler.sendMessage(msg);
                            }
                        });
            }
        }).start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Timber.e(e);
        }

        return loginSuccessful;
    }
}
