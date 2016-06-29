package com.pdrogfer.onstage.model;

/**
 * Created by pedrogonzalezferrandez on 06/06/16.
 */
public class Gig {

    // POJO Gig class

    /*
    timestamp is used as gigID, it orders chronologically gigs in the database
     */
    private long gigID;
    private String artist;
    private String date;
    private String local;
    private String price;

    public Gig() {
        // Default constructor required for calls to DataSnapshot.getValue(Gig.class)
    }

    public Gig(long timestamp, String artist, String date, String local, String price) {
        this.gigID = timestamp;
        this.artist = artist;
        this.date = date;
        this.local = local;
        this.price = price;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public long getGigID() {
        return gigID;
    }

    public void setGigID(long gigID) {
        this.gigID = gigID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Gig {" +
                "gigID=" + gigID +
                ", artist='" + artist + '\'' +
                ", date='" + date + '\'' +
                ", local='" + local + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
