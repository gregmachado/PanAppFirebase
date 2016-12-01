package gregmachado.com.panappfirebase.domain;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 23/10/16.
 */
public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private String newPassword;
    private boolean type;
    private boolean sendNotification;
    private boolean firstOpen;
    private String image;
    private String bakeryID;
    private ArrayList<String> favorites;
    private double lastLatitude;
    private double lastLongitude;
    private int distanceForSearchBakery;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameIfNull(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailIfNull(String email) {
        if (this.email == null) {
            this.email = email;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBakeryID() {
        return bakeryID;
    }

    public void setBakeryID(String bakeryID) {
        this.bakeryID = bakeryID;
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public int getDistanceForSearchBakery() {
        return distanceForSearchBakery;
    }

    public void setDistanceForSearchBakery(int distanceForSearchBakery) {
        this.distanceForSearchBakery = distanceForSearchBakery;
    }

    public boolean isFirstOpen() {
        return firstOpen;
    }

    public void setFirstOpen(boolean firstOpen) {
        this.firstOpen = firstOpen;
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = LibraryClass.getFirebase().child("users").child(getId());
        if (completionListener.length == 0) {
            setPassword(null);
            setId(null);
            firebase.setValue(this);
        } else {
            setPassword(null);
            setId(null);
            firebase.setValue(this, completionListener[0]);
        }
    }
}
