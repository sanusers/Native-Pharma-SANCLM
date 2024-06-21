package saneforce.sanzen.activity.myresource.Categoryview;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityCateChemistviewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class Cate_Chemistview extends Fragment {

    ArrayList<callstatus_model> categoryList = new ArrayList<>();

    Category_adapter categoryAdapter;


    RoomDB roomDB;
    MasterDataDao masterDataDao;
    ActivityCateChemistviewBinding binding;
    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityCateChemistviewBinding.inflate(inflater);
        View v = binding.getRoot();
        initialization();
        return v;
    }
    private void initialization(){
        roomDB= RoomDB.getDatabase(requireContext());
        masterDataDao=roomDB.masterDataDao();
        adapterSetUp();

    }
    private void adapterSetUp(){
        JSONArray chemistJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
        if (chemistJsonArray.length() > 0) {
            for (int i = 0; i < chemistJsonArray.length(); i++) {
                try {
                    JSONObject chemistJsonObject = chemistJsonArray.getJSONObject(i);
                    String name = (chemistJsonObject.getString("Name"));
                    String chemistCategoryName = (chemistJsonObject.getString("Chem_Cat_Name"));
                    categoryList.add(new callstatus_model(name, chemistCategoryName, "", "", "", "",
                            "", "", "", "", "", "", "", "", "","",""));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            categoryAdapter= new Category_adapter(requireActivity(), categoryList);
            LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            binding.chemCatRecyclerView.setLayoutManager(manager);
            binding.chemCatRecyclerView.setAdapter(categoryAdapter);
            categoryAdapter.notifyDataSetChanged();
            }else {
            binding.textNoData.setVisibility(View.VISIBLE);
            binding.chemCatRecyclerView.setVisibility(View.GONE);
        }
        }
    }
