package kodman.koreaprobnik.Model;

/**
 * Created by DI1 on 23.03.2018.
 */

public class Product {

    private String id;
    private String title;
    private String category;
    private String description;
    private String pathImage;
    private float price;

    public Product(String title) {
        this.title = title;
    }

    public Product() {
        this.title="unknown product";
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }
}
