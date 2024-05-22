package saneforce.sanzen.activity.reports.dayReport;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {

    private final MutableLiveData<String> summaryData = new MutableLiveData<>();
    private final MutableLiveData<String> date = new MutableLiveData<>();
    private final MutableLiveData<String> detailedData = new MutableLiveData<>();

    public void saveSummaryData(String data){
        this.summaryData.setValue(data);
    }

    public LiveData<String> getSummaryData(){
        return summaryData;
    }

    public void saveDate(String date){
        this.date.setValue(date);
    }

    public LiveData<String> getDate(){
        return date;
    }

    public void saveDetailedData(String data){
        detailedData.setValue(data);
    }

    public MutableLiveData<String> getDetailedData() {
        return detailedData;
    }


}
