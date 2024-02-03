package saneforce.santrip.activity.map.custSelection;

public class CustList {
    String name;
    String type;
    String code;
    String town_code;
    String category;
    String categoryCode;
    String specialist;

    public String getSpecialistCode() {
        return specialistCode;
    }

    public void setSpecialistCode(String specialistCode) {
        this.specialistCode = specialistCode;
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    String specialistCode;
    String position;
    String town_name;
    String maxTag;
    String Tag;
    String latitude;
    String longitude;
    String address;
    boolean isClusterAvailable;
    String geoTagStatus;
    String wedding_date;
    String mobile;
    String email;
    String qualification;
    String dob;
    String phone;
    String PriorityPrdCode;
    String dummy;
    String totalVisitCount;

    public String getTotalVisitCount() {
        return totalVisitCount;
    }

    public void setTotalVisitCount(String totalVisitCount) {
        this.totalVisitCount = totalVisitCount;
    }

    public String getMappedBrands() {
        return MappedBrands;
    }

    public void setMappedBrands(String mappedBrands) {
        MappedBrands = mappedBrands;
    }

    public String getMappedSlides() {
        return MappedSlides;
    }

    public void setMappedSlides(String mappedSlides) {
        MappedSlides = mappedSlides;
    }

    String MappedBrands;
    String MappedSlides;
    String TransNo;
    String jsonArray;

    public String getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(String jsonArray) {
        this.jsonArray = jsonArray;
    }

    public String getTransNo() {
        return TransNo;
    }

    public void setTransNo(String transNo) {
        TransNo = transNo;
    }

    public String getADetSlNo() {
        return ADetSlNo;
    }

    public void setADetSlNo(String ADetSlNo) {
        this.ADetSlNo = ADetSlNo;
    }

    public boolean isExtra() {
        return IsExtra;
    }

    public void setExtra(boolean extra) {
        IsExtra = extra;
    }

    String ADetSlNo;
    boolean IsExtra;

    public String getTotalrcpa() {
        return totalrcpa;
    }

    public void setTotalrcpa(String totalrcpa) {
        this.totalrcpa = totalrcpa;
    }

    String totalrcpa;

    public String getPriorityPrdCode() {
        return PriorityPrdCode;
    }

    public void setPriorityPrdCode(String priorityPrdCode) {
        PriorityPrdCode = priorityPrdCode;
    }

    public CustList(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public CustList(String name, String code,String type,String Trans_Slno,String ADetSLNo,String jsonArray) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.TransNo = Trans_Slno;
        this.ADetSlNo = ADetSLNo;
        this.jsonArray = jsonArray;
    }

    public CustList(String name, String SpecialityName,String SpecialityCode,String mappedBrands,String mappedSlides,boolean isExtra) {
        this.name = name;
        this.specialist = SpecialityName;
        this.specialistCode = SpecialityCode;
        this.MappedBrands = mappedBrands;
        this.MappedSlides = mappedSlides;
        this.IsExtra = isExtra;
    }


    public CustList(String name, String code, String totalrcpa, String dummy) {
        this.name = name;
        this.code = code;
        this.totalrcpa = totalrcpa;
        this.dummy = dummy;
    }

    public CustList(String name, String lat, String lon, String address, String dummy) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.dummy = dummy;
    }


    public CustList(String town_name) {
        this.town_name = town_name;
    }

    public CustList(String latitude, String longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public CustList(String name, String code, String type, String category, String specialist, String town_name, String towncode, String tag, String maxTag, String position) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.specialist = specialist;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
    }

    public CustList(String name, String code, String type, String category, String categoryCode, String specialist, String town_name, String towncode, String tag, String maxTag, String position, String latitude, String longitude, String address, String dob, String wed_date, String email, String mobile, String phone, String qualification) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.categoryCode = categoryCode;
        this.specialist = specialist;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.dob = dob;
        this.wedding_date = wed_date;
        this.email = email;
        this.mobile = mobile;
        this.phone = phone;
        this.qualification = qualification;
    }

    public CustList(String name, String code, String type, String category, String categoryCode, String specialist, String town_name, String towncode, String tag, String maxTag, String position, String latitude, String longitude, String address, String dob, String wed_date, String email, String mobile, String phone, String qualification, String priorityPrdCode,boolean isClusterAvailable) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.categoryCode = categoryCode;
        this.specialist = specialist;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.dob = dob;
        this.wedding_date = wed_date;
        this.email = email;
        this.mobile = mobile;
        this.phone = phone;
        this.qualification = qualification;
        this.PriorityPrdCode = priorityPrdCode;
        this.isClusterAvailable = isClusterAvailable;
    }

    public CustList(String name, String code, String type, String category, String categoryCode, String specialist,String specialistCode, String town_name, String towncode, String tag, String maxTag, String position, String latitude, String longitude, String address, String dob, String wed_date, String email, String mobile, String phone, String qualification, String priorityPrdCode,String MappedBrandCode,String MappedSlideCode,String totalVisitCount,boolean isClusterAvailable) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.categoryCode = categoryCode;
        this.specialist = specialist;
        this.specialistCode = specialistCode;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.dob = dob;
        this.wedding_date = wed_date;
        this.email = email;
        this.mobile = mobile;
        this.phone = phone;
        this.qualification = qualification;
        this.PriorityPrdCode = priorityPrdCode;
        this.MappedBrands = MappedBrandCode;
        this.MappedSlides = MappedSlideCode;
        this.totalVisitCount = totalVisitCount;
        this.isClusterAvailable = isClusterAvailable;
    }

    public CustList(String name, String code, String type, String category, String categoryCode, String specialist,String specialistCode, String town_name, String towncode, String tag, String maxTag, String position, String latitude, String longitude, String address, String dob, String wed_date, String email, String mobile, String phone, String qualification, String priorityPrdCode,String MappedBrandCode,String MappedSlideCode) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.categoryCode = categoryCode;
        this.specialist = specialist;
        this.specialistCode = specialistCode;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.dob = dob;
        this.wedding_date = wed_date;
        this.email = email;
        this.mobile = mobile;
        this.phone = phone;
        this.qualification = qualification;
        this.PriorityPrdCode = priorityPrdCode;
        this.MappedBrands = MappedBrandCode;
        this.MappedSlides = MappedSlideCode;
    }

    public CustList(String name, String code, String type, String category, String specialist, String latitude, String longitude, String address, String town_name, String towncode, String tag, String maxTag, String position, String geoTagStatus) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.specialist = specialist;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
        this.geoTagStatus = geoTagStatus;
    }


 /*   public CustList(String name, String code, String type, String category, String categoryCode, String specialist, String town_name, String towncode, String tag, String maxTag, String position, String latitude, String longitude, String address, String dob, String wed_date, String email, String mobile, String phone, String qualification, String priorityPrdCode,boolean isClusterAvailable) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.categoryCode = categoryCode;
        this.specialist = specialist;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.dob = dob;
        this.wedding_date = wed_date;
        this.email = email;
        this.mobile = mobile;
        this.phone = phone;
        this.qualification = qualification;
        this.PriorityPrdCode = priorityPrdCode;
        this.isClusterAvailable = isClusterAvailable;
    }*/

 public CustList(String name, String code, String type, String category, String categoryCode, String specialist,String specialistCode, String town_name, String towncode, String tag, String maxTag, String position, String latitude, String longitude, String address, String dob, String wed_date, String email, String mobile, String phone, String qualification, String priorityPrdCode,String MappedBrandCode,String MappedSlideCode,boolean isClusterAvailable) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.category = category;
        this.categoryCode = categoryCode;
        this.specialist = specialist;
        this.specialistCode = specialistCode;
        this.town_name = town_name;
        this.town_code = towncode;
        this.Tag = tag;
        this.maxTag = maxTag;
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.dob = dob;
        this.wedding_date = wed_date;
        this.email = email;
        this.mobile = mobile;
        this.phone = phone;
        this.qualification = qualification;
        this.PriorityPrdCode = priorityPrdCode;
        this.MappedBrands = MappedBrandCode;
        this.MappedSlides = MappedSlideCode;
        this.isClusterAvailable = isClusterAvailable;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWedding_date() {
        return wedding_date;
    }

    public void setWedding_date(String wedding_date) {
        this.wedding_date = wedding_date;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }


    public boolean isClusterAvailable() {
        return isClusterAvailable;
    }

    public void setClusterAvailable(boolean clusterAvailable) {
        isClusterAvailable = clusterAvailable;
    }

    public String getGeoTagStatus() {
        return geoTagStatus;
    }

    public void setGeoTagStatus(String geoTagStatus) {
        this.geoTagStatus = geoTagStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTown_code() {
        return town_code;
    }

    public void setTown_code(String town_code) {
        this.town_code = town_code;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public String getMaxTag() {
        return maxTag;
    }

    public void setMaxTag(String maxTag) {
        this.maxTag = maxTag;
    }
}
