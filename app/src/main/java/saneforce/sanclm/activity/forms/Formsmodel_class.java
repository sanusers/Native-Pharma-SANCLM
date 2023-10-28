package saneforce.sanclm.activity.forms;

import android.widget.ImageView;

public class Formsmodel_class {

    String forms_name;
    ImageView viewlist;



    public Formsmodel_class(String forms_name, ImageView viewlist) {
        this.forms_name = forms_name;
        this.viewlist = viewlist;
    }


    public String getForms_name() {
        return forms_name;
    }

    public void setForms_name(String forms_name) {
        this.forms_name = forms_name;
    }

    public ImageView getViewlist() {
        return viewlist;
    }

    public void setViewlist(ImageView viewlist) {
        this.viewlist = viewlist;
    }


}
