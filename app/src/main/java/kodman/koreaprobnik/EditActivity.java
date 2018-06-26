package kodman.koreaprobnik;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.koreaprobnik.Model.Product;
import kodman.koreaprobnik.Util.Cnst;
import kodman.koreaprobnik.Util.FirestoreHelper;
import kodman.koreaprobnik.databinding.ActivityEditBinding;

/**
 * Created by DI1 on 30.03.2018.
 */

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

//    @BindView(R.id.drawer_layout)
//    DrawerLayout mDrawerLayout;

    //    @BindView(R.id.navigation)
//    NavigationView navigationView;
    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.iv)
    ImageView imageView;


    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPrice)
    EditText etPrice;
    @BindView(R.id.etDescription)
    EditText etDescription;
    Product product ;
    final int RESULT_GALLERY = 1;


    ActivityEditBinding binding;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv:

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_GALLERY);
                // Intent intent=new Intent(EditActivity.this,)
                break;
        }
    }

    private Product getProduct(){

        product =(Product) getIntent().getParcelableExtra(Cnst.PRODUCT);

       // product.setTitle("крем №1");
       // product.setDescription("от морщин");
       // product.setPrice(100.25f);
        return product;}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_edit);
        //  ButterKnife.bind(this);

        getProduct();


      binding  = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        binding.setProduct(product);
        //  binding.setProduct(Demo.getUser());


        String[] data = getResources().getStringArray(R.array.category);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        //spinner.setPrompt("TITLE CATEGORY");


        //binding.imageView.setOnClickListener(this);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAdd: {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

               StorageReference riversRef = storageRef.child("images/"+Uri.parse(product.getUri()).getLastPathSegment());

                    UploadTask  uploadTask = riversRef.putFile(Uri.parse(product.getUri()));
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads

                            Toast.makeText(getBaseContext(),getResources().getString(R.string.failure_upload),Toast.LENGTH_SHORT).show();
                            Log.d(Cnst.TAG,"onFailure upload:"+exception);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getBaseContext(),getResources().getString(R.string.succes_upload),Toast.LENGTH_SHORT).show();
                            Log.d(Cnst.TAG,"onSucces upload");

                        }
                    });


                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RESULT_GALLERY:
                Bitmap bitmap = null;


                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                  //   data.getData().getEncodedPath();
                        product.setUri(data.getData().toString());
                       // product.setUri(selectedImage);
                        Log.d(Cnst.TAG, "setUri = " );
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("---", "setUri = " + selectedImage);
                    }

                    Log.d("---", "setBitmap = " + bitmap);
                   //  binding.iv.setImageBitmap(bitmap);
                }


//

            default:
                break;
        }
    }


    private void addFileToFirestorage(Uri file) {
        FirestoreHelper.getInstance(this).uploadFile(file);

    }
}
