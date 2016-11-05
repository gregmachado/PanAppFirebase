package gregmachado.com.panappfirebase.domain;

import com.google.firebase.database.DatabaseReference;

import gregmachado.com.panappfirebase.util.LibraryClass;

/**
 * Created by gregmachado on 25/10/16.
 */
public class Adress {

    private String id;
    private String userID;
    private String adressName;
    private String Cep;
    private String state;
    private String reference;
    private String complement, country;
    private String street;
    private String district;
    private String city;
    private int number;
    private Double latitude, longitude, distance;

    public Adress() {}

    public Adress(String id, String userID, String adressName, String cep, String state, String reference,
                  String complement, String country, String street, String district, String city, int number,
                  Double latitude, Double longitude, Double distance) {
        this.id = id;
        this.userID = userID;
        this.adressName = adressName;
        Cep = cep;
        this.state = state;
        this.reference = reference;
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

    public Adress(String userID, String street, String district, String city, int number,
                  Double latitude, Double longitude) {
        this.userID = userID;
        this.street = street;
        this.district = district;
        this.city = city;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getAdressName() {
        return adressName;
    }

    public void setAdressName(String adressName) {
        this.adressName = adressName;
    }

    public String getCep() {
        return Cep;
    }

    public void setCep(String cep) {
        Cep = cep;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

