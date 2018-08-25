package c.kodman.app_client;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import c.kodman.app_client.Adapter.AdapterProduct;
import c.kodman.app_client.Model.Product;
import c.kodman.app_client.Util.Cnst;
import c.kodman.app_client.Util.FirestoreHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
    Toast.makeText(this,"KeyCode = "+keyCode,Toast.LENGTH_SHORT).show();
        return false;
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;

//    @BindView(R.id.tvAbout)
//    TextView tvAbout;

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

    String category;//="all";
    private Menu menu;

    int minPrice=0;
    int maxPrice=0;
    int searchTitle=0;

    boolean isAdmin=false;
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Cnst.CATEGORY,category);
    }

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
    if(savedInstanceState!=null)
            category=savedInstanceState.getString(Cnst.CATEGORY);
        else
        category=getResources().getString(R.string.category0);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirestoreHelper.getInstance(this).getAbout();
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
                       // menuItem.setChecked(true);
                        category=menuItem.getTitle().toString();

                        adapter = new AdapterProduct(products, MainActivity.this, MainActivity.this,category,isAdmin){

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
                                Log.d(Cnst.TAG,"Firestore Exception"+e.getMessage());
                                // Покажи снакбар в случаи ошибки
                              //  Snackbar.make(findViewById(android.R.id.content),
                               //         "Ошибка: смотрите логи", Snackbar.LENGTH_LONG).show();
                            }



                        };

                            mDrawerLayout.closeDrawers();



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


       setAdapter(isAdmin);

      // FirestoreHelper.getInstance(this).show();
    }

    private void setAdapter(boolean rules,String []params){
      adapter=  new AdapterProduct(products, this, this,category,rules,params,params[0]){

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
                //  Snackbar.make(findViewById(android.R.id.content),
                //          "Ошибка: смотрите логи", Snackbar.LENGTH_LONG).show();
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void setAdapter(boolean rules){
        adapter=  new AdapterProduct(products, this, this,category,rules){

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
                //  Snackbar.make(findViewById(android.R.id.content),
                //          "Ошибка: смотрите логи", Snackbar.LENGTH_LONG).show();
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

        this.menu=menu;

        SharedPreferences sP= PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(Cnst.TAG,"shared email:"+sP.getString(Cnst.Email,"ee"));
       String user=sP.getString(Cnst.Email,getResources().getString(R.string.action_login));
        if(user.equals(getResources().getString(R.string.action_login)))
        {
            isAdmin=false;
            menu.getItem(2).setTitle(getResources().getString(R.string.action_login));
        }
        else
        {
            isAdmin=true;
            menu.getItem(2).setTitle(user);
        }

       // setAdmin(isAdmin);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,((SearchView)v).getQuery(),Toast.LENGTH_SHORT).show();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               // Toast.makeText(MainActivity.this,query,Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // Toast.makeText(MainActivity.this,newText,Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return true;
    }

    public void setMenuItem(int pos,String title)
    {
        menu.getItem(pos).setTitle(title);
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
//            case R.id.actionAdd: {
//
//                //Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(this, EditActivity.class);
//                //intent.putExtra(Cnst.PRODUCT, currentProduct);
//                intent.putExtra(Cnst.NEXT_ID,1);
//                startActivity(intent);
//                break;
//            }

            case R.id.actionDelete: {

                adapter.delProduct();
                //Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();

                return true;
            }
            case R.id.actionFilter:{

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.filter, null);
                dialogBuilder.setView(dialogView).setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      EditText etTitle= dialogView.findViewById(R.id.etTitle);
                        EditText etMinPrice= dialogView.findViewById(R.id.priceMin);
                        EditText etMaxPrice= dialogView.findViewById(R.id.priceMax);
                      String[] params=new String [3];
                      params[0]=etTitle.getText().toString();
                        params[1]=etMinPrice.getText().toString();
                        params[2]=etMaxPrice.getText().toString();
                      setAdapter(isAdmin,params);
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                    }}).setCancelable(true);

               // EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
               // editText.setText("test label");
                AlertDialog alertDialog = dialogBuilder.create();

                alertDialog.show();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }



    private  void showProduct(String []params)
    {


        adapter.setQuery(FirestoreHelper.getInstance(this).getQuery(params));
        adapter.notifyDataSetChanged();
    }
}
