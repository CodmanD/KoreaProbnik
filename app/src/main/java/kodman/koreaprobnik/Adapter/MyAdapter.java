package kodman.koreaprobnik.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kodman.koreaprobnik.EditActivity;
import kodman.koreaprobnik.MainActivity;
import kodman.koreaprobnik.Model.Product;
import kodman.koreaprobnik.R;
import kodman.koreaprobnik.Util.Cnst;

/**
 * Created by DI1 on 30.03.2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    private Context context;
    private List<Product> products;

    public MyAdapter(List<Product> products,Context context) {

        this.context=context;
        this.products = products;
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

        Product product = products.get(i);
          Log.d(Cnst.TAG,"onBindViewHolder event = "+product.getTitle());
        viewHolder.tv.setText(product.getTitle());
        //viewHolder.alarmName.setText(event.getAlarmName());
        viewHolder.iv.setImageResource(R.drawable.google_sign_in);




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
        private TextView tv;

        private ImageView iv;


        public ViewHolder(View itemView) {
            super(itemView);
            //this.id = (TextView) itemView.findViewById(R.id.tvItemID);
            this.tv = (TextView) itemView.findViewById(R.id.tv);
            // this.alarmName = (TextView) itemView.findViewById(R.id.tvItemAlarmName);
            this.iv = (ImageView) itemView.findViewById(R.id.iv);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                       Toast.makeText(context, "CLICK id = " + tv.getText(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, EditActivity.class);
                    //intent.putExtra("ID", Integer.parseInt(id.getText().toString()));

                    context.startActivity(intent);
                }
            });
        }
    }
}

