package gregmachado.com.panappfirebase.domain;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 25/10/16.
 */
public class Bakery {

    private String id;
    private String userID;
    private String corporateName;
    private String fantasyName;
    private String fone;
    private String email;
    private String cnpj;
    private String bakeryImage;
    private Adress adress;
    private boolean favorite;
    private boolean hasDelivery;
    private String startTime, finishTime;
    private List<Product> productList;

    public Bakery() {}

    public Bakery(String id, String userID, String corporateName, String fantasyName, String fone, String email,
                  String cnpj, String bakeryImage, Adress adress, boolean favorite, boolean hasDelivery,
                  String startTime, String finishTime, List<Product> productList) {
        this.id = id;
        this.userID = userID;
        this.corporateName = corporateName;
        this.fantasyName = fantasyName;
        this.fone = fone;
        this.email = email;
        this.cnpj = cnpj;
        this.bakeryImage = bakeryImage;
        this.adress = adress;
        this.favorite = favorite;
        this.hasDelivery = hasDelivery;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.productList = productList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getFantasyName() {
        return fantasyName;
    }

    public void setFantasyName(String fantasyName) {
        this.fantasyName = fantasyName;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getBakeryImage() {
        return bakeryImage;
    }

    public void setBakeryImage(String bakeryImage) {
        this.bakeryImage = bakeryImage;
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public boolean isHasDelivery() {
        return hasDelivery;
    }

    public void setHasDelivery(boolean hasDelivery) {
        this.hasDelivery = hasDelivery;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = LibraryClass.getFirebase().child("bakeries").child(getUserID());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }
    }
}
