package c.kodman.app_client.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import c.kodman.app_client.BR;

/**
 * Created by DI1 on 23.03.2018.
 */

public class Product extends BaseObservable implements Parcelable {
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(pathImage);
        dest.writeString(uri);
        dest.writeDouble(price);
        dest.writeInt(quantity);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Product(Parcel in) {
        id = in.readString();
        title= in.readString();
        category = in.readString();
        description = in.readString();
        pathImage=in.readString();
        uri=in.readString();
        price = (float)in.readDouble();
        quantity=(int)in.readInt();
    }



    private String id;
    private String title;
    private String category;
    private String description;
    private String pathImage;
    private String uri;
    private float price;
    private int quantity;

    public Product(String uid, String title, String category, String description, String uri, String price,String quantity){

        this.id=uid;
        this.title=title;
        this.category=category;
        this.description=description;
        this.uri=uri;
        this.price=Float.parseFloat(price);
        this.quantity=Integer.parseInt(quantity);
    }

    public Product(String title) {
        this.title = title;

        this.description="some description";
        this.price=100.50f;
        this.pathImage="content://media/external/images/media/136728";
       // this.uri=Uri.parse(this.pathImage);
    }

    public String getUri() {
        return uri;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Product() {
        this.title="unknown product";
        this.description="some description";
        this.price=100.50f;

    }

    @Bindable
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
      //  notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        notifyPropertyChanged(BR.category);

    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);

    }

    @Bindable
    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
        notifyPropertyChanged(BR.pathImage);
    }

    @Override
    public String toString() {
        return "Product: id="+this.id+" | "+this.title+"\npath:  "+this.pathImage+"\ncategory: "+this.category+"\nuri:"+this.getUri();
    }
}
