package saneforce.sanzen.activity.BusinessEntry.ModelClass;

public class AddDoctorEntryProducts {
    public String getSample_Price() {
        return Sample_Price;
    }

    public void setSample_Price(String sample_Price) {
        Sample_Price = sample_Price;
    }

    public String getNSR_Price() {
        return NSR_Price;
    }

    public void setNSR_Price(String NSR_Price) {
        this.NSR_Price = NSR_Price;
    }

    public String getTarget_Price() {
        return Target_Price;
    }

    public void setTarget_Price(String target_Price) {
        Target_Price = target_Price;
    }

    public String getDistributor_Price() {
        return Distributor_Price;
    }

    public void setDistributor_Price(String distributor_Price) {
        Distributor_Price = distributor_Price;
    }

    public String getProduct_Detail_Name() {
        return Product_Detail_Name;
    }

    public void setProduct_Detail_Name(String product_Detail_Name) {
        Product_Detail_Name = product_Detail_Name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRetailor_Price() {
        return Retailor_Price;
    }

    public void setRetailor_Price(String retailor_Price) {
        Retailor_Price = retailor_Price;
    }

    public String getProduct_Code() {
        return Product_Code;
    }

    public void setProduct_Code(String product_Code) {
        Product_Code = product_Code;
    }

    public String getProduct_Sale_Unit() {
        return Product_Sale_Unit;
    }

    public void setProduct_Sale_Unit(String product_Sale_Unit) {
        Product_Sale_Unit = product_Sale_Unit;
    }

    public String getMRP_Price() {
        return MRP_Price;
    }

    public void setMRP_Price(String MRP_Price) {
        this.MRP_Price = MRP_Price;
    }

    public String getProduct_Quantity() {
        return Product_Quantity;
    }

    public void setProduct_Quantity(String product_Quantity) {
        Product_Quantity = product_Quantity;
    }

    String NSR_Price,Sample_Price,Target_Price,Distributor_Price,Product_Detail_Name,value;
    String Retailor_Price;
    String Product_Code;
    String Product_Sale_Unit;
    String MRP_Price;
    String Product_Quantity;

    public String getDetailcode() {
        return Detailcode;
    }

    public void setDetailcode(String detailcode) {
        Detailcode = detailcode;
    }

    String Detailcode;

    public AddDoctorEntryProducts(String prdname, String prdcode, String prdqty, String prdsaleunit, String mrp, String retailor, String distributor,
                                  String nsr,String sample,String target,String value,String detailcode) {
        this.Product_Detail_Name = prdname;
        this.Product_Code = prdcode;
        this.Product_Quantity = prdqty;
        this.Product_Sale_Unit = prdsaleunit;
        this.MRP_Price = mrp;
        this.Retailor_Price = retailor;
        this.Distributor_Price = distributor;
        this.NSR_Price = nsr;
        this.Sample_Price = sample;
        this.Target_Price = target;
        this.value = value;
        this.Detailcode=detailcode;
    }
}
