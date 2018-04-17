package kodman.koreaprobnik;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.koreaprobnik.Util.Cnst;
import kodman.koreaprobnik.Util.FirestoreHelper;

/**
 * Created by DI1 on 30.03.2018.
 */

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation)
    NavigationView navigationView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawerLayout.isDrawerOpen(navigationView))
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
                        // close drawer when item is tapped
                       // mDrawerLayout.closeDrawers();

                        Toast.makeText(EditActivity.this,menuItem.getTitle(),Toast.LENGTH_SHORT).show();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
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

                //Toast.makeText(this, "NEW EVENT", Toast.LENGTH_SHORT).show();
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , Cnst.RESULT_GALLERY );
                return true;
            }
        }
      return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Cnst.RESULT_GALLERY :
                if (null != data) {
                   Log.d(Cnst.TAG,"Reuslt = "+data.getData());
                    // imageUri = data.getData();
                    //Do whatever that you desire here. or leave this blank
                    addFileToFirestorage(data.getData());
                }
                break;
            default:
                break;
        }
    }


    private void addFileToFirestorage(Uri file){
        FirestoreHelper.getInstance(this).uploadFile(file);

    }
}
