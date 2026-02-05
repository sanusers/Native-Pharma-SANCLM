package saneforce.sanzen.activity.presentation.createPresentation;

import java.util.ArrayList;

public class BrandMatrixModelClass {
    private String SlideId;
    private String Code;
    private String Priority;
    private String Product_Detail_Code;
    boolean specialitySelected = false;
    ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
    private int selectedSlideCount = 0;

    public BrandMatrixModelClass(String slideId, String code,  String product_Detail_Code,  String priority) {
       this. SlideId = slideId;
        this. Code = code;
        this. Product_Detail_Code = product_Detail_Code;
       this. Priority = priority;


    }

    // ---------------- GETTERS ----------------
    public boolean isSpecialitySelected() {
        return specialitySelected;
    }

    public void setSpecialitySelected(boolean specialitySelected) {
        this.specialitySelected = specialitySelected;
    }
    public ArrayList<BrandModelClass.Product> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(ArrayList<BrandModelClass.Product> list) {
        this.productArrayList = list;
    }

    public String getSlideId() {
        return SlideId;
    }

    public String getCode() {
        return Code;
    }


    public String getProduct_Detail_Code() {
        return Product_Detail_Code;
    }


    public String getPriority() {
        return Priority;
    }

    public int getSelectedSlideCount() { return selectedSlideCount; }
    public void setSelectedSlideCount(int count) { this.selectedSlideCount = count; }
}
