package ca.utoronto.ece.cimsah.logger.encrypt;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.util.Base64;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import ca.utoronto.ece.cimsah.logger.LoggerProperties;
import ca.utoronto.ece.cimsah.logger.authentication.User;
import ca.utoronto.ece.cimsah.logger.model.PubKeyBase64;
import timber.log.Timber;


public class KeyManager {
    private static final String TAG = "KeyManager";
    Context context;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_PUBKEY_BASE64 = "shared_preferences_public_key";

    public KeyManager(Context c) {
        context = c;
        sharedPreferences = context.getSharedPreferences(
                LoggerProperties.getInstance().getSharedPrefsFileName(),
                Context.MODE_PRIVATE);
    }

    public PublicKey getPublicKey() throws GeneralSecurityException {
        String publicKeyBase64 = sharedPreferences.getString(PREFS_PUBKEY_BASE64, null);
        byte[] publicKeyBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        return publicKey;
    }

    private void setPublicKey(String publicKeyBase64) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFS_PUBKEY_BASE64, publicKeyBase64);
        editor.apply();
        Timber.d( "Saving key: %s", publicKeyBase64);
    }

    public void init(final OnCompleteListener listener) {
        final User user = new User(context);
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("publicKeys").document(uid);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PubKeyBase64 pubKeyBase64 = documentSnapshot.toObject(PubKeyBase64.class);
                setPublicKey(pubKeyBase64.getPublicKeyBase64());
                Timber.d(pubKeyBase64.getPublicKeyBase64());
                listener.onComplete(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
                listener.onComplete(e);
            }
        });
    }

    public interface OnCompleteListener {
        void onComplete(Exception e);
    }
}
