package saneforce.sanclm.activity.forms.weekoff;

public class fromsmodelclass {

    String valus,monthname,date,holidayname,weekname,holiday_date,Allclr,dub_coltcode,backgrdclr,pos;
    int image;



    public fromsmodelclass(String monthname, String date, String holidayname, String weekname, String holiday_date, String Allclr, String dub_coltcode,String backgrdclr) {
        this.monthname = monthname;
        this.date = date;
        this.holidayname = holidayname;
        this.weekname = weekname;
        this.holiday_date = holiday_date;
        this.Allclr = Allclr;
        this.dub_coltcode = dub_coltcode;
        this.backgrdclr = backgrdclr;

    }

    public String getBackgrdclr() {
        return backgrdclr;
    }

    public void setBackgrdclr(String backgrdclr) {
        this.backgrdclr = backgrdclr;
    }

    public String getMonthname() {
        return monthname;
    }

    public void setMonthname(String monthname) {
        this.monthname = monthname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHolidayname() {
        return holidayname;
    }

    public void setHolidayname(String holidayname) {
        this.holidayname = holidayname;
    }

    public String getWeekname() {
        return weekname;
    }

    public void setWeekname(String weekname) {
        this.weekname = weekname;
    }

    public String getHoliday_date() {
        return holiday_date;
    }

    public void setHoliday_date(String holiday_date) {
        this.holiday_date = holiday_date;
    }

    public fromsmodelclass(String valus,  String pos) {
        this.valus = valus;

        this.pos = pos;
    }


    public String getValus() {
        return valus;
    }

    public void setValus(String valus) {
        this.valus = valus;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getAllclr() {
        return Allclr;
    }

    public void setAllclr(String allclr) {
        Allclr = allclr;
    }

    public String getDub_coltcode() {
        return dub_coltcode;
    }

    public void setDub_coltcode(String dub_coltcode) {
        this.dub_coltcode = dub_coltcode;
    }
}
