package gregmachado.com.panappfirebase.domain;

import java.io.Serializable;

/**
 * Created by gregmachado on 30/10/16.
 */
public class Product implements Serializable{

    private String id;
    private String productName;
    private Double productPrice;
    private String productImage;
    private String type;
    private String bakeryId;
    private int itensSale;
    private int unit;

    public Product() {}

    public Product(String id, String productName, Double productPrice, String productImage,
                   String type, String bakeryId, int itensSale, int unit) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.type = type;
        this.bakeryId = bakeryId;
        this.itensSale = itensSale;
        this.unit = unit;
    }

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

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
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
}
