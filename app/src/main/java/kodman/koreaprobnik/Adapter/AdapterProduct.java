package kodman.koreaprobnik.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kodman.koreaprobnik.EditActivity;
import kodman.koreaprobnik.Model.Product;
import kodman.koreaprobnik.ProductActivity;
import kodman.koreaprobnik.R;
import kodman.koreaprobnik.Util.Cnst;
import kodman.koreaprobnik.Util.FirebaseImageLoader;
import kodman.koreaprobnik.Util.FirestoreHelper;

/**
 * Created by DI1 on 30.03.2018.
 */

public class AdapterProduct extends FirestoreAdapter<AdapterProduct.ViewHolder> {


    private Context context;
    //   private List<Product> products;
    private FirestoreHelper fb;
    Product product;
    boolean isAdmin;
    String category;
    ArrayList listForRemoved = new ArrayList();
    AppCompatActivity activity;
    // private ViewHolder viewHolder;

    public void delProduct() {
        for (int i = 0; i < listForRemoved.size(); i++) {
            Log.e(Cnst.TAG, "DEL:" + listForRemoved.get(i));
            FirestoreHelper.getInstance(activity).removeProduct(String.valueOf(listForRemoved.get(i)));
        }
    }

    public AdapterProduct(List<Product> products, Context context, AppCompatActivity activity, String category, boolean isAdmin) {

        //fb = FirestoreHelper.getInstance(activity);
        super(FirestoreHelper.getInstance(activity).getQuery(category));
        //  Log.d(Cnst.TAG,"adaoter Constructor");
        this.context = context;
        this.category = category;
        this.activity = activity;
        this.isAdmin = isAdmin;
        // this.products = products;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, viewGroup, false);
        //this.viewHolder=new ViewHolder(v);
        //return this.viewHolder;
        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i));
    }

//    @Override
//    public int getItemCount() {
//        return products.size();
//    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvDescription)
        TextView tvDescription;

        @BindView(R.id.tvPrice)
        TextView tvPrice;


        @BindView(R.id.iv)
        ImageView iv;

        private Product currentProduct;
        private Uri uri;

        public void bind(final DocumentSnapshot snapshot) //,final OnRestaurantSelectedListener listener) {
        {


            currentProduct = snapshot.toObject(Product.class);
            currentProduct.setId(snapshot.getId());

            if (!listForRemoved.contains(currentProduct.getId())) {
                itemView.setClickable(true);
                itemView.setBackgroundResource(R.drawable.item_recycler);
            } else {
                itemView.setClickable(false);
                itemView.setBackgroundResource(R.drawable.item_recycler_selected);
            }
            Log.d(Cnst.TAG, "bind:" + currentProduct + "\n----------------------------");
            // currentProduct.setPathImage(snapshot.g.getData().get(Cnst.IMAGES).toString());
            //Log.d(Cnst.TAG,"bind : "+snapshot.getId());
            Resources resources = itemView.getResources();


            // Log.d(Cnst.TAG, "onBindViewHolder event = " + product.getTitle());
            tvTitle.setText(currentProduct.getTitle());
            //  viewHolder.tvDescription.setText(product.getDescription());
            tvPrice.setText(String.valueOf(currentProduct.getPrice()));
            tvDescription.setText(String.valueOf(currentProduct.getDescription()));

            FirebaseStorage storage = FirebaseStorage.getInstance();
            // final StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/koreaprobnik-20240.appspot.com/o/gold_nand.jpg?alt=media&token=5e2dbf5e-369f-4705-8688-14a49d290bb8");

            // Log.d(Cnst.TAG,"Bind Product  pathImage= "+currentProduct);

            //StorageReference gsReference = storage.getReferenceFromUrl("gs://koreaprobnik-20240.appspot.com/images/171045");
            // Загружаем изображения

            iv.setImageDrawable(context.getResources().getDrawable(R.drawable.gift));

            try {
                final StorageReference gsReference = storage.getReferenceFromUrl(currentProduct.getUri());
                final File localFile = File.createTempFile("images", ".jpg");

                gsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        currentProduct.setPathImage(localFile.getAbsolutePath());
                        currentProduct.setUri(gsReference.toString());
                        Log.d(Cnst.TAG, "adapter set URI " + currentProduct.getUri());
                        Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());


                        Glide.with(context)
                                .load(localFile)
                                // .using(new FirebaseImageLoader())
                                //   .load( "gs://koreaprobnik-20240.appspot.com/gold_nand.jpg")
                                .into(iv);

                        //  iv.setImageBitmap(myBitmap);
                        if (myBitmap == null) {

                        }
                        //   Log.d(Cnst.TAG,"onSucces : "+taskSnapshot.getStorage().getName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //   iv.setImageDrawable(context.getResources().getDrawable(R.drawable.default_product));
                        Log.d(Cnst.TAG, "Faillure : " + e.getMessage() + ": " + currentProduct.getTitle());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();

                // iv.setImageDrawable(context.getResources().getDrawable(R.drawable.default_product));
            } catch (Exception e) {
                e.printStackTrace();
                //  iv.setImageDrawable(context.getResources().getDrawable(R.drawable.gift));
            }


        }


        public ViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

            //Log.d(Cnst.TAG, " tvTitle = "+tvTitle);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isAdmin) {
                        Toast.makeText(context, "CLICK id = " + tvTitle.getText(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, EditActivity.class);
                        intent.putExtra(Cnst.PRODUCT, currentProduct);
                        //Log.d(Cnst.TAG, " put product :" + currentProduct.hashCode());
                        context.startActivity(intent);
                    } else {

                        Intent intent = new Intent(context, ProductActivity.class);
                        intent.putExtra(Cnst.PRODUCT, currentProduct);
                        //Log.d(Cnst.TAG, " put product :" + currentProduct.hashCode());
                        context.startActivity(intent);
                     //   Toast.makeText(context, "not admin CLICK id = " + tvTitle.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (isAdmin)
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        Log.d(Cnst.TAG, "onLongClick");
                        if (itemView.isClickable()) {
                            listForRemoved.add(currentProduct.getId());
                            itemView.setClickable(false);
                            itemView.setBackgroundResource(R.drawable.item_recycler_selected);
                            //itemView.setBackgroundColor(context.getResources().getColor(R.color.colorSelected));
                        } else {
                            listForRemoved.remove(currentProduct.getId());
                            itemView.setClickable(true);
                            itemView.setBackgroundResource(R.drawable.item_recycler);
                        }

                        return true;
                    }


                });

        }
    }

    public void setClickAdmin(boolean bool) {

//        if (bool) {
//            Log.d(Cnst.TAG, "Adapter setAdmin");
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (isAdmin) {
//                        Toast.makeText(context, "CLICK id = " + viewHolder.tvTitle.getText(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, EditActivity.class);
//
//                        //currentProduct.setCategory("Патчи");
//                        intent.putExtra(Cnst.PRODUCT, viewHolder.currentProduct);
//                        Log.d(Cnst.TAG, " put product :" + viewHolder.currentProduct.hashCode());
//
//                        context.startActivity(intent);
//                    } else {
//                        Toast.makeText(context, "not admin CLICK id = " + viewHolder.tvTitle.getText(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//
//            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    Log.d(Cnst.TAG, "onLongClick");
//                    if (viewHolder.itemView.isClickable()) {
//                        listForRemoved.add(viewHolder.currentProduct.getId());
//                        viewHolder.itemView.setClickable(false);
//                        viewHolder.itemView.setBackgroundResource(R.drawable.item_recycler_selected);
//                        //itemView.setBackgroundColor(context.getResources().getColor(R.color.colorSelected));
//                    } else {
//                        listForRemoved.remove(viewHolder.currentProduct.getId());
//                        viewHolder.itemView.setClickable(true);
//                        viewHolder.itemView.setBackgroundResource(R.drawable.item_recycler);
//                    }
//
//                    return true;
//                }
//
//
//            });
//        }
    }
}

