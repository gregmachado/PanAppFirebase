package gregmachado.com.panappfirebase.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by gregmachado on 30/10/16.
 */
public class Product implements Serializable, Parcelable{

    private String id;
    private String productName;
    private double productPrice;
    private double oldPrice;
    private String productImage;
    private String type;
    private String bakeryId;
    private int itensSale;
    private int unit;
    private boolean inOffer;

    public Product() {}

    protected Product(Parcel in) {
        id = in.readString();
        productName = in.readString();
        productImage = in.readString();
        type = in.readString();
        bakeryId = in.readString();
        itensSale = in.readInt();
        unit = in.readInt();
        productPrice = in.readDouble();
        oldPrice = in.readDouble();
        inOffer = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBakeryId() {
        return bakeryId;
    }

    public void setBakeryId(String bakeryId) {
        this.bakeryId = bakeryId;
    }

    public int getItensSale() {
        return itensSale;
    }

    public void setItensSale(int itensSale) {
        this.itensSale = itensSale;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public boolean isInOffer() {
        return inOffer;
    }

    public void setInOffer(boolean inOffer) {
        this.inOffer = inOffer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(productName);
        dest.writeString(productImage);
        dest.writeString(type);
        dest.writeString(bakeryId);
        dest.writeInt(itensSale);
        dest.writeInt(unit);
        dest.writeDouble(productPrice);
        dest.writeDouble(oldPrice);
        dest.writeByte((byte) (inOffer ? 1 : 0));
    }
}
