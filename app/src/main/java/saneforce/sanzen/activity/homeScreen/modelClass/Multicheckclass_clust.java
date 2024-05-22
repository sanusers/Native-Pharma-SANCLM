package saneforce.sanzen.activity.homeScreen.modelClass;



public class Multicheckclass_clust {

    String strid,strname;
    String teritry_visit;
    boolean checked;

    public Multicheckclass_clust(String strid, String strname, String teritry_visit, boolean checked) {
        this.strid = strid;
        this.strname = strname;
        this.teritry_visit = teritry_visit;
        this.checked = checked;
    }

    public String getStrid() {
        return strid;
    }

    public void setStrid(String strid) {
        this.strid = strid;
    }

    public String getStrname() {
        return strname;
    }

    public void setStrname(String strname) {
        this.strname = strname;
    }

    public String getTeritry_visit() {
        return teritry_visit;
    }

    public void setTeritry_visit(String teritry_visit) {
        this.teritry_visit = teritry_visit;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
