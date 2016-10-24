package gregmachado.com.panappfirebase.domain;

import android.content.Context;

import com.firebase.client.Firebase;

import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 23/10/16.
 */
public class User {

    private static final String TOKEN = "gregmachado.com.panappfirebase.domain.User.TOKEN";
    private String id;
    private String name;
    private String email;
    private String password;
    private String newPassword;
    private boolean type;
    private boolean sendNotification;
    private String image;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void saveDB(){
        Firebase firebase = LibraryClass.getFirebase();
        firebase = firebase.child("users").child( getId() );

        setPassword(null);
        setId(null);
        firebase.setValue(this);
    }

    public String getTokenSP(Context context ){
        String token = LibraryClass.getSP( context, TOKEN );
        return( token );
    }

    public void saveTokenSP(Context context, String token ){
        LibraryClass.saveSP( context, TOKEN, token );
    }
}
