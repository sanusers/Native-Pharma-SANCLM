package saneforce.sanclm.activity.map;

import java.util.Collections;
import java.util.Comparator;

public class TaggedMapList {
    String name;String addr;
    String type;
    String code;
    boolean clicked;
    double meters;

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

    public TaggedMapList(String name, String type,String addr,String code,boolean clicked,double meters) {
        this.name = name;
        this.type = type;
        this.addr = addr;
        this.code = code;
        this.clicked = clicked;
        this.meters = meters;
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
