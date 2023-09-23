package saneforce.sanclm.activity.map;

public class ViewTagModel {
    String code, name, lat, lng;
    String reference, height, width, phno;
    String address;
    String imageName;
    String townName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public ViewTagModel(String code, String name, String lat, String lng, String address, String imageName, String townName, String townCode) {
        this.code = code;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.imageName = imageName;
        this.townName = townName;
        this.townCode = townCode;
    }

    String townCode;


    @Override
    public boolean equals(Object obj) {
        ViewTagModel dt = (ViewTagModel) obj;
        if (name == null)
            return false;
        return dt.name.equals(name);
    }
}
