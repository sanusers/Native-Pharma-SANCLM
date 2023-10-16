package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection;

public class FilterDataList {
    String name;
    int pos;

    public FilterDataList(String name, int pos) {
        this.name = name;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
