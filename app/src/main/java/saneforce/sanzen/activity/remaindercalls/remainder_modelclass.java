package saneforce.sanzen.activity.remaindercalls;

public class remainder_modelclass {
    String doc_code,doc_name,doc_cat,doc_spec,doc_town,doc_catcode,doc_speccode,doc_towncode,doc_sftype;
    String jw_code,jw_name;

    public remainder_modelclass(String jw_code, String jw_name) {
        this.jw_code = jw_code;
        this.jw_name = jw_name;
    }

    public remainder_modelclass(String doc_code, String doc_name, String doc_cat, String doc_spec, String doc_town, String doc_catcode, String doc_speccode, String doc_towncode, String doc_sftype) {
        this.doc_code = doc_code;
        this.doc_name = doc_name;
        this.doc_cat = doc_cat;
        this.doc_spec = doc_spec;
        this.doc_town = doc_town;
        this.doc_catcode = doc_catcode;
        this.doc_speccode = doc_speccode;
        this.doc_towncode = doc_towncode;
        this.doc_sftype = doc_sftype;
    }
    public remainder_modelclass(String doc_code, String doc_name, String doc_cat) {
        this.doc_code = doc_code;
        this.doc_name = doc_name;
        this.doc_cat = doc_cat;

    }

    public String getDoc_catcode() {
        return doc_catcode;
    }

    public void setDoc_catcode(String doc_catcode) {
        this.doc_catcode = doc_catcode;
    }

    public String getDoc_speccode() {
        return doc_speccode;
    }

    public void setDoc_speccode(String doc_speccode) {
        this.doc_speccode = doc_speccode;
    }

    public String getDoc_towncode() {
        return doc_towncode;
    }

    public void setDoc_towncode(String doc_towncode) {
        this.doc_towncode = doc_towncode;
    }

    public String getDoc_code() {
        return doc_code;
    }

    public void setDoc_code(String doc_code) {
        this.doc_code = doc_code;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDoc_cat() {
        return doc_cat;
    }

    public void setDoc_cat(String doc_cat) {
        this.doc_cat = doc_cat;
    }

    public String getDoc_spec() {
        return doc_spec;
    }

    public void setDoc_spec(String doc_spec) {
        this.doc_spec = doc_spec;
    }

    public String getDoc_town() {
        return doc_town;
    }

    public void setDoc_town(String doc_town) {
        this.doc_town = doc_town;
    }

    public String getDoc_sftype() {
        return doc_sftype;
    }

    public void setDoc_sftype(String doc_sftype) {
        this.doc_sftype = doc_sftype;
    }

    public String getJw_code() {
        return jw_code;
    }

    public void setJw_code(String jw_code) {
        this.jw_code = jw_code;
    }

    public String getJw_name() {
        return jw_name;
    }

    public void setJw_name(String jw_name) {
        this.jw_name = jw_name;
    }
}
