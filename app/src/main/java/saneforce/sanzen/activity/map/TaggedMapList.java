package saneforce.sanzen.activity.map;

public class TaggedMapList {
    String name;
    String code;
    String addr;
    String type;
    String lat, lng;
    boolean clicked;
    double meters;
    String imageName;


    public TaggedMapList(String name, String type, String addr, String code, boolean clicked, String lat, String lng, String imageName, double meters) {
        this.name = name;
        this.type = type;
        this.addr = addr;
        this.code = code;
        this.clicked = clicked;
        this.lat = lat;
        this.lng = lng;
        this.imageName = imageName;
        this.meters = meters;
    }

    public double getMeters() {
        return meters;
    }

    public void setMeters(double meters) {
        this.meters = meters;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
