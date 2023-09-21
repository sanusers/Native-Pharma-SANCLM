package saneforce.sanclm.activity.leave;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalendarViewModel extends ViewModel {

    private String from_data; // Example data
    private String to_data; // Example data
    private MutableLiveData<String> fromDate = new MutableLiveData<>();

    public void setFromData(String data) {
        this.from_data = data;
    }

    public String getFromData() {
        return from_data;
    }
    public void setToData (String data1) {
        this.to_data = data1;
    }

    public String getToData() {
        return to_data;
    }

    public MutableLiveData<String> getFromDate () {
        return fromDate;
    }

    public void setFromDate (MutableLiveData<String> fromDate) {
        this.fromDate = fromDate;
    }
}



