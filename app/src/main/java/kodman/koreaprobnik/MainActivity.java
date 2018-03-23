package kodman.koreaprobnik;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.koreaprobnik.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAdd: {

                Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();

                return true;
            }
            case R.id.actionUpdate: {

                Toast.makeText(this, "Update", Toast.LENGTH_SHORT).show();

                return true;
            }
            case R.id.actionDelete: {

                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
