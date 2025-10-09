package saneforce.sanzen.activity.reports.missedReport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

public class MissedStatsModel {

    private JSONArray totalCustomers;
    private Set<String> uniqueCustomers;
    private String type;// "Doctor", "Chemist", etc.
    //private String month;


    public MissedStatsModel(String type, JSONArray totalCustomers, Set<String> uniqueCustomers) {

        this.type = type;
        this.totalCustomers = totalCustomers;
        this.uniqueCustomers = uniqueCustomers;
    }


    public JSONArray getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(JSONArray totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Set<String> getUniqueCustomers() {
        return uniqueCustomers;

    }

    public void setUniqueCustomers(Set<String> uniqueCustomers) {
        this.uniqueCustomers = uniqueCustomers;
    }

    public String getType() {
        return type;
    }


    //added
    public JSONArray getMissedCustomers() {
        JSONArray result = new JSONArray();
        try {
            if (uniqueCustomers == null) {
                Log.d("DoctorVisitActivity", "uniqueCustomers is null!");
                return result;
            }

            for (int i = 0; i < totalCustomers.length(); i++) {
                JSONObject custObj = totalCustomers.getJSONObject(i);
                String custCode = custObj.optString("Code").trim(); // "Code" not "CustCode"

                // Normalize: convert all codes to String
                boolean isVisited = false;
                for (String visitedCode : uniqueCustomers) {
                    if (visitedCode.trim().equals(custCode)) {
                        isVisited = true;
                        break;
                    }
                }

                if (!isVisited) {
                    custObj.put("status", "missed");
                    result.put(custObj);
                    Log.d("DoctorVisitActivity", "Added missed doctor: " + custCode);
                } else {
                    //custObj.put("status", "visited");
                    Log.d("DoctorVisitActivity", "Visited doctor: " + custCode);
                }
            }

            Log.d("DoctorVisitActivity", "Total missed doctors: " + result.length());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
//    public JSONArray getMissedCustomers() {
//        JSONArray result = new JSONArray();
//        try {
//            for (int i = 0; i < totalCustomers.length(); i++) {
//                JSONObject custObj = totalCustomers.getJSONObject(i);
//                String custCode = custObj.optString("CustCode");
//
//                if (uniqueCustomers != null && !uniqueCustomers.contains(custCode)) {
//                    custObj.put("status", "missed");
//                    result.put(custObj);
//                    Log.d("DoctorVisitActivity", "Added missed doctor: " + custCode);
//                    Log.d("DoctorVisitActivity", "Current missed count: " + result.length());
//                }else{
//                    Log.d("TAG", "getMissedCustomers:"+ "uniqueCust is null");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//}


//public class MissedStatsModel implements Parcelable {
//
//    private String doctorName;
//
//    // Maps month string (e.g., "2025-09") to list of visited doctors
//    private Map<String, List<String>> monthlyVisitedDoctors;
//
//    // Stores total doctors (same for all months)
//    private List<String> totalDoctors;
//
//    public MissedStatsModel(String doctorName, List<String> totalDoctors, Map<String, List<String>> monthlyVisitedDoctors) {
//        this.doctorName = doctorName;
//        this.totalDoctors = totalDoctors;
//        this.monthlyVisitedDoctors = monthlyVisitedDoctors;
//    }
//
//    // Getters
//    public String getDoctorName() { return doctorName; }
//    public List<String> getTotalDoctors() { return totalDoctors; }
//
//    // Calculate unique visited for a given month
//    public int getVisitedCount(String month) {
//        List<String> visited = monthlyVisitedDoctors.get(month);
//        if (visited == null) return 0;
//        return new HashSet<>(visited).size();
//    }
//
//    // Calculate missed for a given month
//    public int getMissedCount(String month) {
//        int visited = getVisitedCount(month);
//        int missed = totalDoctors.size() - visited;
//        return Math.max(missed, 0);
//    }
//
//    // Optional: get all months available
//    public List<String> getMonths() {
//        return new ArrayList<>(monthlyVisitedDoctors.keySet());
//    }
//
//    // Parcelable implementation
//    protected MissedStatsModel(Parcel in) {
//        doctorName = in.readString();
//        totalDoctors = in.createStringArrayList();
//        // For Map, you may need to serialize as JSON or implement separately
//    }
//
//    public static final Creator<MissedStatsModel> CREATOR = new Creator<MissedStatsModel>() {
//        @Override
//        public MissedStatsModel createFromParcel(Parcel in) {
//            return new MissedStatsModel(in);
//        }
//
//        @Override
//        public MissedStatsModel[] newArray(int size) {
//            return new MissedStatsModel[size];
//        }
//    };
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(doctorName);
//        dest.writeStringList(totalDoctors);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//}

//import android.os.Parcel;
//import android.os.Parcelable;
//import java.util.ArrayList;
//import java.util.HashSet;
//
//public class MissedStatsModel implements Parcelable {
//
//    private ArrayList<String> totalDoctors;   // all doctors
//    private ArrayList<String> visitedDoctors; // visited doctors
//    private int missedDoctors;                // automatically calculated
//    private String average;
//
//    // Constructor
//    public MissedStatsModel(ArrayList<String> totalDoctors, ArrayList<String> visitedDoctors, String average) {
//        this.totalDoctors = totalDoctors;
//        this.visitedDoctors = visitedDoctors;
//        this.average = average;
//        calculateMissedDoctors();
//    }
//
//    // Calculate missed doctors based on total vs unique visited
//    private void calculateMissedDoctors() {
//        HashSet<String> uniqueVisited = new HashSet<>(visitedDoctors);
//        this.missedDoctors = totalDoctors.size() - uniqueVisited.size();
//        if (missedDoctors < 0) missedDoctors = 0;
//    }
//
//    // Parcelable constructor
//    protected MissedStatsModel(Parcel in) {
//        totalDoctors = in.createStringArrayList();
//        visitedDoctors = in.createStringArrayList();
//        missedDoctors = in.readInt();
//        average = in.readString();
//    }
//
//    public static final Creator<MissedStatsModel> CREATOR = new Creator<MissedStatsModel>() {
//        @Override
//        public MissedStatsModel createFromParcel(Parcel in) {
//            return new MissedStatsModel(in);
//        }
//
//        @Override
//        public MissedStatsModel[] newArray(int size) {
//            return new MissedStatsModel[size];
//        }
//    };
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeStringList(totalDoctors);
//        dest.writeStringList(visitedDoctors);
//        dest.writeInt(missedDoctors);
//        dest.writeString(average);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    // Getters and setters
//    public ArrayList<String> getTotalDoctors() {
//        return totalDoctors;
//    }
//
//    public void setTotalDoctors(ArrayList<String> totalDoctors) {
//        this.totalDoctors = totalDoctors;
//        calculateMissedDoctors(); // recalc missed
//    }
//
//    public ArrayList<String> getVisitedDoctors() {
//        return visitedDoctors;
//    }
//
//    public void setVisitedDoctors(ArrayList<String> visitedDoctors) {
//        this.visitedDoctors = visitedDoctors;
//        calculateMissedDoctors(); // recalc missed
//    }
//
//    public int getMissedDoctors() {
//        return missedDoctors;
//    }
//
//    public String getAverage() {
//        return average;
//    }
//
//    public void setAverage(String average) {
//        this.average = average;
//    }
//
