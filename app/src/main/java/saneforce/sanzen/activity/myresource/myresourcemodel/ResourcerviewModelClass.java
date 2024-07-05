package saneforce.sanzen.activity.myresource.myresourcemodel;

public class ResourcerviewModelClass {

private String  Custname ,Custcode, Lat, Long, Adds, Towmname,ImageName;


    public ResourcerviewModelClass(String custname, String custcode, String lat, String aLong, String Adds, String towmname, String imageName) {
        Custname = custname;
        Custcode = custcode;
        Lat = lat;
        Long = aLong;
        this.Adds = Adds;
        Towmname = towmname;
        ImageName = imageName;
    }

    public String getCustname() {
        return Custname;
    }

    public void setCustname(String custname) {
        Custname = custname;
    }

    public String getCustcode() {
        return Custcode;
    }

    public void setCustcode(String custcode) {
        Custcode = custcode;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getAdds() {
        return Adds;
    }

    public void setAdds(String adds) {
        Adds = adds;
    }

    public String getTowmname() {
        return Towmname;
    }

    public void setTowmname(String towmname) {
        Towmname = towmname;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }
}
