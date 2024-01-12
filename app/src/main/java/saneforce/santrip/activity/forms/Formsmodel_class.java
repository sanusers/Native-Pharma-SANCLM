package saneforce.santrip.activity.forms;

public class Formsmodel_class {

    String forms_name;
    int viewlist;



    public Formsmodel_class(String forms_name, int viewlist) {
        this.forms_name = forms_name;
        this.viewlist = viewlist;
    }


    public String getForms_name() {
        return forms_name;
    }

    public void setForms_name(String forms_name) {
        this.forms_name = forms_name;
    }

    public int getViewlist() {
        return viewlist;
    }

    public void setViewlist(int viewlist) {
        this.viewlist = viewlist;
    }


}
