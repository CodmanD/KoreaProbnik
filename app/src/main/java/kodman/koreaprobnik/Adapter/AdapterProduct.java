package kodman.koreaprobnik.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import kodman.koreaprobnik.EditActivity;
import kodman.koreaprobnik.Model.Product;
import kodman.koreaprobnik.R;
import kodman.koreaprobnik.Util.Cnst;
import kodman.koreaprobnik.Util.FirestoreHelper;

/**
 * Created by DI1 on 30.03.2018.
 */

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ViewHolder> {


    private Context context;
    private List<Product> products;
    private FirestoreHelper fb;
    Product product;

    public AdapterProduct(List<Product> products, Context context, AppCompatActivity activity) {

        this.context = context;
        this.products = products;
        fb = FirestoreHelper.getInstance(activity);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //  Log.d(TAG,"onCreateViewHolder");
        // View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item, viewGroup, false);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, viewGroup, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        try {
            viewHolder.currentProduct = products.get(i);
            product = products.get(i);
            Log.d(Cnst.TAG, "onBindViewHolder event = " + product.getTitle());
            viewHolder.tvTitle.setText(product.getTitle());
            viewHolder.tvDescription.setText(product.getDescription());
            viewHolder.tvPrice.setText(String.valueOf(product.getPrice()));
            Log.d(Cnst.TAG, "onBindViewHolder event = " + product.getTitle()+" URI = "+product.getUri());
            viewHolder.uri=product.getUri();
            //viewHolder.alarmName.setText(event.getAlarmName());
            String file = product.getPathImage();


            fb.downloadFile(product.getUri().getLastPathSegment(), viewHolder.iv);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Cnst.TAG, "EException ");
        }
        //   viewHolder.iv.setImageResource(R.drawable.google_sign_in);


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;

        private TextView tvPrice;
        private Uri uri;
        private ImageView iv;
        private Product currentProduct;


        public ViewHolder(View itemView) {
            super(itemView);
            //this.id = (TextView) itemView.findViewById(R.id.tvItemID);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            this.tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            // this.alarmName = (TextView) itemView.findViewById(R.id.tvItemAlarmName);
            this.iv = (ImageView) itemView.findViewById(R.id.iv);



            Bitmap bitmap = null;
            try {
                Log.d(Cnst.TAG,"bitmap  try crweate");
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                Log.d(Cnst.TAG,"bitmap crweate");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Log.d("---", "setBitmap = " + bitmap);
            if (null != bitmap)
            {
                iv.setImageBitmap(bitmap);
                Log.d(Cnst.TAG,"bitmap set : "+bitmap);
            }
            else {

                //iv.setImageDrawable(context.getDrawable(R.drawable.googleg_standard_color_18));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context, "CLICK id = " + tvTitle.getText(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EditActivity.class);
                    intent.putExtra(Cnst.PRODUCT, currentProduct);

                    context.startActivity(intent);
                }
            });
        }
    }
}

