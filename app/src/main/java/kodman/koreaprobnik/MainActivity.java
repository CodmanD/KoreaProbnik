package kodman.koreaprobnik;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.koreaprobnik.Adapter.AdapterProduct;
import kodman.koreaprobnik.Model.Product;
import kodman.koreaprobnik.Util.Cnst;
import kodman.koreaprobnik.Util.FirestoreHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation)
    NavigationView navigationView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    private FirebaseFirestore mFirestore;
    FirestoreHelper mFirestoreHelper;

    List<Product> products = new ArrayList<>(100);
    AdapterProduct adapter=null;

    String category="all";



    public void updateData(){

        Log.d(Cnst.TAG," update recycler");
      //  adapter = new AdapterProduct(products, this, this);
      //  recyclerView.setAdapter(adapter);
       // recyclerView.invalidate();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Cnst.TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
      //  mFirestore=FirebaseFirestore.getInstance();
       // mFirestoreHelper = FirestoreHelper.getInstance(MainActivity.this);



       // addListToFireStore();
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(navigationView))
                    mDrawerLayout.closeDrawers();

                else
                    mDrawerLayout.openDrawer(navigationView);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        category=menuItem.getTitle().toString();
                        adapter = new AdapterProduct(products, MainActivity.this, MainActivity.this,category){

                            @Override
                            protected void onDataChanged() {

                                // Покажи/спрячь данные в UI если запрос возвращается пустым
                                if (getItemCount() == 0) {
                                    recyclerView.setVisibility(View.GONE);
                                    //mEmptyView.setVisibility(View.VISIBLE);

                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    //mEmptyView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            protected void onError(FirebaseFirestoreException e) {
                                // Покажи снакбар в случаи ошибки
                                Snackbar.make(findViewById(android.R.id.content),
                                        "Ошибка: смотрите логи", Snackbar.LENGTH_LONG).show();
                            }
                        };
                        adapter.startListening();
                        recyclerView.setAdapter(adapter);
                        // close drawer when item is tapped
                        // mDrawerLayout.closeDrawers();

                        //Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


       adapter = new AdapterProduct(products, this, this,category){

           @Override
           protected void onDataChanged() {

               // Покажи/спрячь данные в UI если запрос возвращается пустым
               if (getItemCount() == 0) {
                   recyclerView.setVisibility(View.GONE);
                   //mEmptyView.setVisibility(View.VISIBLE);

               } else {
                   recyclerView.setVisibility(View.VISIBLE);
                   //mEmptyView.setVisibility(View.GONE);
               }
           }

           @Override
           protected void onError(FirebaseFirestoreException e) {
               // Покажи снакбар в случаи ошибки
               Snackbar.make(findViewById(android.R.id.content),
                       "Ошибка: смотрите логи", Snackbar.LENGTH_LONG).show();
           }
       };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void showCategory(String category){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Cnst.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                FirestoreHelper.getInstance(this).firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(Cnst.TAG, "Google sign in failed", e);
                Snackbar.make(findViewById(R.id.root), "Google sign in failed", Snackbar.LENGTH_SHORT).show();
                // [START_EXCLUDE]
                // updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }


    public void showProgress() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

        progressBar.setLayoutParams(params);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.invalidate();

    }

    public void hideProgress() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

        progressBar.setLayoutParams(params);

        progressBar.setVisibility(ProgressBar.INVISIBLE);
        progressBar.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void addListToFireStore() {
        mFirestoreHelper.downloadListFirestore("kodman.dev@gmail.com",products);
//        if (products.size() == 0){
//            //UidGenerator ug = new UidGenerator(null, "uidGen");
//            for (int i = 0; i < 10; i++) {
//                products.add(new Product("product " + i));
//                products.get(i).setId(String.valueOf(i));
//              //  products.get(i).setUri(Uri.parse(""));
//                mFirestoreHelper.uploadProduct(products.get(i),"kodman.dev@gmail.com");
//            }
//        }
//        else{
//            Toast.makeText(this,"Reeead from FS",Toast.LENGTH_SHORT).show();
//        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLogin: {

                Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
                FirestoreHelper.getInstance(this).signIn();
                return true;
            }
            case R.id.actionOut: {

                // Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
                FirestoreHelper.getInstance(this).signOut();
                return true;
            }
            case R.id.actionAdd: {

                //Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, EditActivity.class);
                //intent.putExtra(Cnst.PRODUCT, currentProduct);
                intent.putExtra(Cnst.NEXT_ID,1);
                startActivity(intent);
                break;
            }

            case R.id.actionDelete: {

                //Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();

                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }


}
