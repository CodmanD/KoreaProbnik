package kodman.koreaprobnik.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import kodman.koreaprobnik.LoginActivity;
import kodman.koreaprobnik.MainActivity;
import kodman.koreaprobnik.R;

/**
 * Created by DI1 on 02.04.2018.
 */

public class FirestoreHelper {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private static AppCompatActivity activity;
    private static FirestoreHelper instance;
    static FirebaseStorage storage;


    private Context context;


    public static FirestoreHelper getInstance(AppCompatActivity activity)
    {
        if(instance==null)
        {

            instance = new FirestoreHelper(activity);
            storage  = FirebaseStorage.getInstance();

        }
        return instance;

    }

    private FirestoreHelper(AppCompatActivity activity)
    {
        this.activity=activity;


        //FirebaseFirestore.setLoggingEnabled(true);
        //Google Sign_iN
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    public void signIn() {
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent,Cnst.RC_SIGN_IN);
    }

public void uploadFile(Uri file){

    StorageReference storageRef = storage.getReferenceFromUrl("gs://koreaprobnik-20240.appspot.com");
    StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());

// создаем uploadTask посредством вызова метода putFile(), в качестве аргумента идет созданная нами ранее Uri
    UploadTask uploadTask = riversRef.putFile(file);

// устанавливаем 1-й слушатель на uploadTask, который среагирует, если произойдет ошибка, а также 2-й слушатель, который сработает в случае успеха операции
    uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
// Ошибка
        }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
// Успешно! Берем ссылку прямую https-ссылку на файл
            Uri downloadUrl = taskSnapshot.getDownloadUrl();

            Log.d(Cnst.TAG,"Succes");
        }
    });

}

public void downloadFile(String file,final ImageView iv)
{

    StorageReference storageRef = storage.getReferenceFromUrl("gs://koreaprobnik-20240.appspot.com");
    StorageReference islandRef = storageRef.child("images/"+file);

    try {
        final File localFile = File.createTempFile(file, "jpg");

    islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            // Local temp file has been created
            try {
                FileInputStream fis = new FileInputStream(localFile);
                BufferedInputStream bis = new BufferedInputStream(fis);

                Bitmap img = BitmapFactory.decodeStream(bis);

                iv.setImageBitmap(img);
                fis.close();
                bis.close();
               // Log.d(Cnst.TAG,"GetFile");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(Cnst.TAG,"Exception:"+e.getMessage());
            }
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle any errors
        }
    });
    }
    catch (IOException ioe)
    {
        ioe.printStackTrace();
        Log.d(Cnst.TAG,"Exception:"+ioe.getMessage());
    }
}


    public void signOut() {
        // Firebase sign out
        mAuth.signOut();


        // Log.d(TAG, "Sign Out complete");
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                        Log.d(Cnst.TAG, "Sign OUT complete");
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // updateUI(null);
                    }
                });
    }


    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(Cnst.TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        ((MainActivity)activity).showProgress();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Log.d(TAG, "signInWithCredential:success"+user.getEmail());
                            Snackbar.make(activity.findViewById(R.id.root), "Authentication success.", Snackbar.LENGTH_SHORT).show();

                            // SharedPreferences.Editor sPEditor= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                            //sPEditor.putString(Cnst.Email,user.getEmail());
                            //sPEditor.commit();
                            Log.d(Cnst.TAG,"Editor put email: "+user.getEmail());



                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(activity.findViewById(R.id.root), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        ((MainActivity)activity).hideProgress();
                        // [END_EXCLUDE]
                    }
                });
    }
}
