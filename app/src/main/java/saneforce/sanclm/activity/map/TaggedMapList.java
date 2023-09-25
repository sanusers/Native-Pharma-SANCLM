package saneforce.sanclm.activity.map;

public class TaggedMapList {
    String name;String addr;
    String code;
    boolean clicked;
    String meters;

    public String getMeters() {
        return meters;
    }

    public void setMeters(String meters) {
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

    public TaggedMapList(String name, String addr,String code,boolean clicked) {
        this.name = name;
        this.addr = addr;
        this.code = code;
        this.clicked = clicked;
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
