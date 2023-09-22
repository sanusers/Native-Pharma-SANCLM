package saneforce.sanclm.activity.homeScreen.call.pojo.detailing;

public class CallDetailingList {
    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    String brand_name;
    String rating;

    public CallDetailingList(String brand_name, String rating, String timeline) {
        this.brand_name = brand_name;
        this.rating = rating;
        this.timeline = timeline;
    }
    String timeline;
}
