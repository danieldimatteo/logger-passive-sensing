package ca.utoronto.ece.cimsah.logger.notifications;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import ca.utoronto.ece.cimsah.logger.R;

public class SnoozeActivity extends AppCompatActivity {
    SweetAlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);

        NotificationReceiver.clearNotification(this);

        //Set Alarms
        NotificationReceiver.snoozeAlarm(this);

        //Let User Know
        dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        dialog.setContentText("You will be reminded again in an hour.");
        dialog.setTitleText("No Problem!");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        if (dialog != null) {
            dialog.cancel();
        }
    }
}
