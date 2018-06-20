package kodman.koreaprobnik.Model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import kodman.koreaprobnik.BR;

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
        //dest.writeString();
        dest.writeDouble(price);
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
        price = (float)in.readDouble();

    }

    private String id;
    private String title;
    private String category;
    private String description;
    private String pathImage;
    private Uri uri;
    private float price;

    public Product(String title) {
        this.title = title;
        this.pathImage="content://media/external/images/media/136728";
        uri=Uri.parse(this.pathImage);
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Product() {
        this.title="unknown product";
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
}
