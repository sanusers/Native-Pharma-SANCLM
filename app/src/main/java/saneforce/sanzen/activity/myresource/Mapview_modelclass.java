package saneforce.sanzen.activity.myresource;

public class Mapview_modelclass {

    String strlat, strlong, straddr,strname,str_townname,imgs;



    public Mapview_modelclass(String strlat, String strlong, String straddr, String strname, String str_townname, String imgs) {
        this.strlat = strlat;
        this.strlong = strlong;
        this.straddr = straddr;
        this.strname = strname;
        this.str_townname = str_townname;
        this.imgs = imgs;
    }



    public String getStrlat() {
        return strlat;
    }

    public void setStrlat(String strlat) {
        this.strlat = strlat;
    }

    public String getStrlong() {
        return strlong;
    }

    public void setStrlong(String strlong) {
        this.strlong = strlong;
    }

    public String getStraddr() {
        return straddr;
    }

    public void setStraddr(String straddr) {
        this.straddr = straddr;
    }

    public String getStrname() {
        return strname;
    }

    public void setStrname(String strname) {
        this.strname = strname;
    }

    public String getStr_townname() {
        return str_townname;
    }

    public void setStr_townname(String str_townname) {
        this.str_townname = str_townname;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

}
