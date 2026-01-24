package saneforce.sanzen.activity.presentation.createPresentation;

import android.transition.Slide;

import java.util.ArrayList;

public class SpecialityModelClass {
    private String code;
    private String name;
    private String docSpecialName;
    private String divisionCode;
    boolean specialitySelected = false;
   // private ArrayList<Slide> slideList;
    private int selectedSlideCount = 0;

   // private ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();

    public SpecialityModelClass(String code, String name, String docSpecialName, String divisionCode) {
        this.code = code;
        this.name = name;
        this.docSpecialName = docSpecialName;
        this.divisionCode = divisionCode;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDocSpecialName() { return docSpecialName; }
    public String getDivisionCode() { return divisionCode; }

    public boolean isSpecialitySelected() {
        return specialitySelected;
    }

    public void setSpecialitySelected(boolean specialitySelected) {
        this.specialitySelected = specialitySelected;
    }

//    public ArrayList<Slide> getSlideList() {
//        return slideList;
//    }

    public int getSelectedSlideCount() { return selectedSlideCount; }
    public void setSelectedSlideCount(int count) { this.selectedSlideCount = count; }

//    public ArrayList<BrandModelClass.Product> getProductArrayList() { return productArrayList; }
//    public void setProductArrayList(ArrayList<BrandModelClass.Product> list) { this.productArrayList = list; }

}
