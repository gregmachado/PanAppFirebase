package gregmachado.com.panappfirebase.domain;

import com.google.firebase.database.DatabaseReference;

import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 25/10/16.
 */
public class Adress {

    private String id;
    private String userID;
    private String complement, country;
    private String street;
    private String district;
    private String city;
    private int number;
    private Double latitude, longitude, distance;

    public Adress() {}

    public Adress(String userID, String complement, String country, String street,
                  String district, String city, int number, Double latitude, Double longitude, Double distance) {
        this.userID = userID;
        this.complement = complement;
        this.country = country;
        this.street = street;
        this.district = district;
        this.city = city;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
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

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void saveDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = LibraryClass.getFirebase().child("adresses").child(getId());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }
    }
}

