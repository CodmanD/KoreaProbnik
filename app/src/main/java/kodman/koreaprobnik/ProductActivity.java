package kodman.koreaprobnik;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.koreaprobnik.Model.Product;
import kodman.koreaprobnik.Util.Cnst;

/**
 * Created by DI1 on 30.03.2018.
 */

public class ProductActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv)
    ImageView iv;

    @BindView(R.id.tvPrice)
    TextView tvPrice;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @BindView(R.id.tvCategory)
    TextView tvCategory;

    Product product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        product = (Product) getIntent().getParcelableExtra(Cnst.PRODUCT);
        getSupportActionBar().setTitle(product.getTitle());
        this.tvDescription.setText(product.getDescription());
        this.tvCategory.setText(product.getCategory());
        this.tvPrice.setText(String.valueOf(product.getPrice()));
        this.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                ImageView imageView=new ImageView(ProductActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);// imageView.getLayoutParams();
               // params.height=LinearLayout.LayoutParams.MATCH_PARENT;
               // params.width=LinearLayout.LayoutParams.MATCH_PARENT;


                try {
                  //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(product.getPathImage()));
                    //final File localFile = new File(product.getPathImage());
                    Bitmap bitmap= BitmapFactory.decodeFile(product.getPathImage());
                    imageView.setImageBitmap(bitmap);
                    imageView.setLayoutParams(params);


                } catch (Exception e) {
                    Toast.makeText(ProductActivity.this,"Bitemap error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                     e.printStackTrace();
                }
               // imageView.setImageDrawable(ProductActivity.this.getResources().getDrawable(R.drawable.gift));
                dialogBuilder.setView(imageView)
                        .setCancelable(true);

                dialogBuilder.create().show();
            }
        });
        try {
            final File localFile = new File(product.getPathImage());
            Glide.with(this)
                    .load(localFile)
                    .into(iv);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
}

