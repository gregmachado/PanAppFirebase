package gregmachado.com.panappfirebase.domain;

import java.util.List;

/**
 * Created by gregmachado on 30/10/16.
 */
public class Request {

    private String requestID;
    private String bakeryID, userID;
    private String requestCode;
    private String creationDate, scheduleDate;
    private String method, scheduleHour;
    private String  adress;
    private Boolean delivered;
    private List<Product> productList;

    public Request() {}

    public Request(String bakeryID, String userID, String creationDate, String scheduleDate,
                   String method, String scheduleHour, String adress, Boolean delivered, List<Product> productList) {
        this.bakeryID = bakeryID;
        this.userID = userID;
        this.creationDate = creationDate;
        this.scheduleDate = scheduleDate;
        this.method = method;
        this.scheduleHour = scheduleHour;
        this.adress = adress;
        this.delivered = delivered;
        this.productList = productList;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getBakeryID() {
        return bakeryID;
    }

    public void setBakeryID(String bakeryID) {
        this.bakeryID = bakeryID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getScheduleHour() {
        return scheduleHour;
    }

    public void setScheduleHour(String scheduleHour) {
        this.scheduleHour = scheduleHour;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }
}
