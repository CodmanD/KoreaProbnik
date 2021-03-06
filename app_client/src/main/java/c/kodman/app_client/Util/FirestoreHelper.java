package c.kodman.app_client.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c.kodman.app_client.MainActivity;
import c.kodman.app_client.Model.Product;
import c.kodman.app_client.R;

import static c.kodman.app_client.Util.Cnst.TAG;

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

    static FirebaseAnalytics mFbAnalytics;//=FirebaseAnalytics.getInstance();

    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private String user = "";


    final private Context context;


    public static FirestoreHelper getInstance(AppCompatActivity activity) {
        if (instance == null) {

            instance = new FirestoreHelper(activity);
            storage = FirebaseStorage.getInstance();
            mFbAnalytics = FirebaseAnalytics.getInstance(activity.getBaseContext());
            /// mFbAnalytics.logEvent();
        }
        return instance;

    }

    private FirestoreHelper(AppCompatActivity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();

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
        activity.startActivityForResult(signInIntent, Cnst.RC_SIGN_IN);
    }


    public void uploadFile(Uri file) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://koreaprobnik-20240.appspot.com");
        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());

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

                Log.d(TAG, "Succes");
            }
        });

    }

    public void uploadProduct(Product p, String user) {
        Map<String, Object> product = new HashMap<>();
        //event.put("owner", user);
        product.put("title", p.getTitle());
        product.put("category", p.getCategory());
        product.put("description", p.getDescription());
        //event.put("uid",currentAE.getUID());
        product.put("uid", p.getId());
        product.put("price", p.getPrice());
        product.put("uri", String.valueOf(p.getUri()));


        mFirestore.collection(user).document(p.getId())

                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Save to Firestore", Toast.LENGTH_SHORT).show();
                        // Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error saving to Firestore", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    public void downloadFile(String file, final ImageView iv) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://koreaprobnik-20240.appspot.com");
        StorageReference islandRef = storageRef.child("images/" + file);

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
                        Log.d(TAG, "Exception:" + e.getMessage());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d(TAG, "Exception:" + ioe.getMessage());
        }
    }


    public void show() {

        Log.d(Cnst.TAG, mAuth.getCurrentUser().toString());
    }

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();


        // Log.d(TAG, "Sign Out complete");
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        // Google sign out
        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(activity.findViewById(R.id.root), "Logout success", Snackbar.LENGTH_SHORT).show();


                SharedPreferences.Editor sPEditor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                sPEditor.remove(Cnst.Email);
                sPEditor.commit();
                ((MainActivity) activity).setMenuItem(2, activity.getResources().getString(R.string.action_login));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(activity.findViewById(R.id.root), "Logout failure : " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //updateUI(null);
                        Log.d(TAG, "Sign OUT complete");
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
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        ((MainActivity) activity).showProgress();
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
                            Snackbar.make(activity.findViewById(R.id.root), "Authentication success." + mAuth.getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();


                            SharedPreferences.Editor sPEditor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                            sPEditor.putString(Cnst.Email, user.getEmail());
                            sPEditor.commit();
                            ((MainActivity) activity).setMenuItem(2, user.getEmail());
                            Log.d(TAG, "Editor put email: " + user.getEmail());


                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(activity.findViewById(R.id.root), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        ((MainActivity) activity).hideProgress();
                        // [END_EXCLUDE]
                    }
                });
    }

    public void getAbout(){
        Log.d(TAG, "read from server= ");
        // List listE=mFirestore.document("events");
        final StringBuilder about=new StringBuilder();

        mFirestore.collection("about").get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Cnst.TAG, "FailureListener");
            }
        });
        mFirestore.collection("about").get().addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.d(Cnst.TAG, "CanceledListener");
            }
        });
        mFirestore.collection("about").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                Log.d(Cnst.TAG, "CompleteListener");
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();


                    if (document != null) {

                        String name;
                        String address;
                        String tel1;
                        String tel2;
                        float price;
                        String uri;
                        //long  lastModified;
                        // DatabaseHelper dbHelper=DatabaseHelper.getInstance(context);


                          name = String.valueOf(document.getDocuments().get(0).getData().get("name"));
                           address = String.valueOf(document.getDocuments().get(0).getData().get("address"));
                            tel1 = String.valueOf(document.getDocuments().get(0).getData().get("tel1"));
                        tel2 = String.valueOf(document.getDocuments().get(0).getData().get("tel2"));
                            //title=String.valueOf(document.getDocuments().get(i).getData().get("title"));
                          about.append(name+":"+address);
                     //  tv.setText(name);
                        //((MainActivity) activity).updateData();
                        Log.d(Cnst.TAG, "Document data COMPLETE "+name);
                    } else {
                       // Log.d(TAG, "No such document");
                    }
                } else {
                    Toast.makeText(context, "Download ERROR", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
        Log.d(TAG, "return = " + about.toString());
    }

    public void downloadListFirestore(String user, final List<Product> list) {

// Remove the 'capital' field from the document

        //  final ArrayList<Product> list= new ArrayList<>();
        Log.d(TAG, "read from server= ");
        // List listE=mFirestore.document("events");


        mFirestore.collection(user).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Cnst.TAG, "FailureListener");
            }
        });
        mFirestore.collection(user).get().addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.d(Cnst.TAG, "CanceledListener");
            }
        });
        mFirestore.collection(user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                Log.d(Cnst.TAG, "CompleteListener");
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();


                    if (document != null) {

                        String title;
                        String id;
                        String category;
                        String description;
                        float price;
                        String uri;
                        //long  lastModified;
                        // DatabaseHelper dbHelper=DatabaseHelper.getInstance(context);
                        for (int i = 0; i < document.getDocuments().size(); i++) {

                            title = String.valueOf(document.getDocuments().get(i).getData().get("title"));
                            description = String.valueOf(document.getDocuments().get(i).getData().get("description"));
                            category = String.valueOf(document.getDocuments().get(i).getData().get("category"));
                            //title=String.valueOf(document.getDocuments().get(i).getData().get("title"));
                            id = document.getDocuments().get(i).getId();
                            price = Float.parseFloat(String.valueOf(document.getDocuments().get(i).getData().get("price")));
                            uri = String.valueOf(document.getDocuments().get(i).getData().get("uri"));

                            Product p = new Product();
                            p.setId(id);
                            p.setCategory(category);
                            p.setDescription(description);
                            p.setTitle(title);
                            //  p.setUri(Uri.parse(uri));
                            p.setPrice(price);


                            list.add(p);


                            // Log.d(Cnst.TAG, "Document data: " + document.getDocuments().get(i).getId());
                        }

                        ((MainActivity) activity).updateData();
                        Log.d(Cnst.TAG, "Document data COMPLETE ");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Toast.makeText(context, "Download ERROR", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
        Log.d(TAG, "return list size= " + list.size());
        //return list;
    }

    public void removeProduct(String id) {
        DocumentReference reference = mFirestore.collection(Cnst.PRODUCT).document(id);
        reference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Cnst.TAG, "Failure delete: " + e.getMessage());
            }
        });
    }

    public void addProduct(final Context context, Product p) {
//        if(p.getUri()!=null) {
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            StorageReference storageRef = storage.getReference();
//            StorageReference riversRef = storageRef.child(Cnst.IMAGES + Uri.parse(p.getUri()).getLastPathSegment());
//            p.setUri(riversRef.toString());
//        }

        Log.d(TAG, "Firestore ADD  =" + p);
        // p.setPathImage();
        Map<String, Object> product = new HashMap<>();
        //event.put("owner", user);
        product.put(Cnst.ID, p.getId());
        product.put(Cnst.TITLE, p.getTitle());
        product.put(Cnst.URI_IMAGE, p.getUri());
        product.put(Cnst.CATEGORY, p.getCategory());
        product.put(Cnst.DESCRIPTION, p.getDescription());
        product.put(Cnst.PRICE, p.getPrice());

        String id = p.getId();
        if (id == null)
            id = getId();
        DocumentReference reference = mFirestore.collection(Cnst.PRODUCT).document(id);

        //reference.getId();
        Log.d(Cnst.TAG, "add product =" + reference.getId());

        // mFirestore.collection("kodman.dev@gmail.com").document(p.getCategory())

        reference.set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, context.getResources().getString(R.string.succes_add), Toast.LENGTH_SHORT).show();
                        // Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, context.getResources().getString(R.string.failure_add), Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public Query getQuery(String category, String[] params) {
        Query mQuery = mFirestore.collection(Cnst.PRODUCT);
        if (!category.equals(context.getResources().getString(R.string.category0))) {
            mQuery = mQuery.whereEqualTo(Cnst.CATEGORY, category);
        }
        if (params != null) {
            if (params[0] != null) {

            }
            if (params[1] != null) {
                mQuery = mQuery.whereGreaterThanOrEqualTo(Cnst.PRICE, Integer.parseInt(params[1]));
            }
            if (params[2] != null) {
                mQuery = mQuery.whereLessThanOrEqualTo(Cnst.PRICE, Integer.parseInt(params[2]));
            }

        }
        // Log.d(Cnst.TAG,"---All:"+mFirestore.collection(Cnst.PRODUCT).document());
//            mQuery=  mFirestore.collection(Cnst.PRODUCT)
//                    .orderBy(Cnst.TITLE, Query.Direction.DESCENDING)
//                    ;
//            if(params[0]!=null)
//                mQuery=  mFirestore.collection(Cnst.PRODUCT)
//                        .orderBy(Cnst.TITLE, Query.Direction.DESCENDING)
//                        ;


        return mQuery;
    }


    public Query getQuery(String[] params) {
        Log.d("---", "Params: " + params[0] + "|" + params[1] + "|" + params[2]);
        Query mQuery = mFirestore.collection(Cnst.PRODUCT)
                // .orderBy(Cnst.PRICE, Query.Direction.DESCENDING)
                //.whereEqualTo(Cnst.TITLE);
                .whereGreaterThanOrEqualTo(Cnst.PRICE, Integer.parseInt(params[1]))
                .whereLessThanOrEqualTo(Cnst.PRICE, Integer.parseInt(params[2]));
        mQuery.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.d("---", "onEvent: " + queryDocumentSnapshots.getDocuments().get(0));
            }
        });

        // .limit(mFirestore.collection(Cnst.PRODUCT).get().getResult().size());
        Log.d("---", "Query: " + mQuery);
        return mQuery;
    }

    public void updateProduct(final Context context, Product p) {
        Map<String, Object> product = new HashMap<>();

        product.put(Cnst.ID, p.getId());
        product.put(Cnst.TITLE, p.getTitle());
        product.put(Cnst.CATEGORY, p.getCategory());
        product.put(Cnst.URI_IMAGE, p.getUri());
        product.put(Cnst.DESCRIPTION, p.getDescription());
        product.put(Cnst.PRICE, p.getPrice());

        // Log.d(Cnst.TAG,"update id ="+p.getId());

        mFirestore.collection(user).document(p.getId()).update(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, context.getResources().getString(R.string.succes_update), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, context.getResources().getString(R.string.failure_update), Toast.LENGTH_SHORT).show();
                        // Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void uploadImage(final Context context, final Product p) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference riversRef = storageRef.child(Cnst.IMAGES + Uri.parse(p.getPathImage()).getLastPathSegment());


        p.setUri(riversRef.toString());

        Log.d(Cnst.TAG, "path: " + p.getPathImage());

        UploadTask uploadTask = riversRef.putFile(Uri.parse(p.getPathImage()));
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Toast.makeText(context, context.getResources().getString(R.string.failure_upload), Toast.LENGTH_SHORT).show();
                Log.d(Cnst.TAG, "onFailure upload:" + exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //addProduct(context,p);
                Toast.makeText(context, context.getResources().getString(R.string.succes_upload), Toast.LENGTH_SHORT).show();
                Log.d(Cnst.TAG, "onSucces upload");

            }
        });

    }

    public String getId() {
        return String.valueOf(System.currentTimeMillis());
    }

}
