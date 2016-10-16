package com.pdrogfer.onstage.model;

public class Gig {

    // POJO Gig class

    /*
    timestamp is used as gigID, it orders chronologically gigs in the database
     */
    private long gigID;
    private String artist;
    private String venue;
    private String date;
    private String startTime;
    private String price;
    private String description;
    private String address;

    public Gig() {
        // Default constructor required for calls to DataSnapshot.getValue(Gig.class)
    }

    public Gig(long timestamp, String artistName, String venue, String date, String startTime, String price, String description) {
        this.gigID = timestamp;
        this.artist = artistName;
        this.venue = venue;
        this.date = date;
        this.startTime = startTime;
        this.price = price;
        this.description = description;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Gig{" +
                "gigID=" + gigID +
                ", artist='" + artist + '\'' +
                ", venue='" + venue + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public String getCity() {
        return "---";
    }

}
