package saneforce.sanclm.activity.presentation.createPresentation;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BrandModelClass {

    String brandName = "";
    String brandCode = "";
    String priority = "";
    int selectedSlideCount = 0;
    boolean brandSelected = false;
    ArrayList<Product> productArrayList;

    public BrandModelClass () {
    }

    public BrandModelClass (String brandName, String brandCode, String priority, int selectedSlideCount, boolean brandSelected, ArrayList<Product> productArrayList) {
        this.brandName = brandName;
        this.brandCode = brandCode;
        this.priority = priority;
        this.selectedSlideCount = selectedSlideCount;
        this.brandSelected = brandSelected;
        this.productArrayList = productArrayList;
    }

    public String getBrandName () {
        return brandName;
    }

    public void setBrandName (String brandName) {
        this.brandName = brandName;
    }

    public String getBrandCode () {
        return brandCode;
    }

    public void setBrandCode (String brandCode) {
        this.brandCode = brandCode;
    }

    public String getPriority () {
        return priority;
    }

    public void setPriority (String priority) {
        this.priority = priority;
    }

    public int getSelectedSlideCount () {
        return selectedSlideCount;
    }

    public void setSelectedSlideCount (int selectedSlideCount) {
        this.selectedSlideCount = selectedSlideCount;
    }

    public boolean isBrandSelected () {
        return brandSelected;
    }

    public void setBrandSelected (boolean brandSelected) {
        this.brandSelected = brandSelected;
    }

    public ArrayList<Product> getProductArrayList () {
        return productArrayList;
    }

    public void setProductArrayList (ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    public static class Presentation {
        String presentationName = "";
        ArrayList<Product> products = new ArrayList<>();

        public Presentation () {
        }

        public Presentation (String presentationName, ArrayList<Product> products) {
            this.presentationName = presentationName;
            this.products = products;
        }

        public String getPresentationName () {
            return presentationName;
        }

        public void setPresentationName (String presentationName) {
            this.presentationName = presentationName;
        }

        public ArrayList<Product> getProducts () {
            return products;
        }

        public void setProducts (ArrayList<Product> products) {
            this.products = products;
        }
    }

    public static class Product implements Comparable<Product>{
        String brandCode = "";
        String brandName = "";
        String slideId = "";
        String fileName = "";
        String priority = "";
        boolean imageSelected = false;
        int draggedPosition = -1;

        public Product () {
        }

        public Product(String brandCode, String brandName, String slideId, String fileName, String priority, boolean imageSelected) {
            this.brandCode = brandCode;
            this.brandName = brandName;
            this.slideId = slideId;
            this.fileName = fileName;
            this.priority = priority;
            this.imageSelected = imageSelected;
        }

        public String getBrandCode () {
            return brandCode;
        }

        public void setBrandCode (String id) {
            this.brandCode = id;
        }

        public String getBrandName () {
            return brandName;
        }

        public void setBrandName (String brandName) {
            this.brandName = brandName;
        }

        public String getSlideId () {
            return slideId;
        }

        public void setSlideId (String slideId) {
            this.slideId = slideId;
        }

        public String getFileName () {
            return fileName;
        }

        public void setFileName (String fileName) {
            this.fileName = fileName;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public boolean isImageSelected () {
            return imageSelected;
        }

        public void setImageSelected (boolean imageSelected) {
            this.imageSelected = imageSelected;
        }

        public int getDraggedPosition () {
            return draggedPosition;
        }

        public void setDraggedPosition (int draggedPosition) {
            this.draggedPosition = draggedPosition;
        }

        @Override
        public int compareTo (Product product) {
            return Integer.compare(this.draggedPosition,product.getDraggedPosition());
        }
    }
}
