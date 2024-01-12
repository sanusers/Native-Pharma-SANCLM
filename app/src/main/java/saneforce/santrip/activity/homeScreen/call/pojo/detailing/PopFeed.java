package saneforce.santrip.activity.homeScreen.call.pojo.detailing;

public class PopFeed
{
    String txt;
    boolean isClick;
    String code;
    String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    String qty;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    String mode;

    public int getDummy() {
        return dummy;
    }

    public void setDummy(int dummy) {
        this.dummy = dummy;
    }

    int dummy;

    public PopFeed()
    {
    }

    public PopFeed(String txt, boolean isClick)
    {
        this.txt = txt;
        this.isClick = isClick;
    }

    public PopFeed(String txt, String code, String qty, boolean isClick)
    {
        this.txt = txt;
        this.qty = qty;
        this.isClick = isClick;
        this.code = code;
    }

    public PopFeed(String txt, boolean isClick, String code)
    {
        this.txt = txt;
        this.isClick = isClick;
        this.code=code;
    }

    public PopFeed(String txt, String code)
    {
        this.txt=txt;
        this.code=code;
    }

    public PopFeed(String txt, String mode, boolean isClick)
    {
        this.txt=txt;
        this.mode=mode;
        this.isClick = isClick;
    }

    public PopFeed(String txt, String code, String mode, boolean isClick, String qty)
    {
        this.txt=txt;
        this.code = code;
        this.mode=mode;
        this.isClick = isClick;
        this.qty = qty;
    }

    public PopFeed(String txt, String mode, boolean isClick, String qty, String code)
    {
        this.txt=txt;
        this.mode=mode;
        this.isClick = isClick;
        this.qty = qty;
        this.code = code;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PopFeed(boolean isClick){
        this.isClick=isClick;
    }

    @Override
    public boolean equals(Object obj)
    {
        PopFeed dt = (PopFeed)obj;
        if(String.valueOf(dt.isClick).equals(String.valueOf(isClick)))
            return true;
        return false;
    }
}

