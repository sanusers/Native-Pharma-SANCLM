package saneforce.sanzen.activity.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import saneforce.sanzen.activity.chat.adapter.ChatListAdapter;
import saneforce.sanzen.activity.chat.model.ChatUserModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityChatBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding activityChatBinding;
    private MasterDataDao masterDataDao;
    private List<ChatUserModel> chatUserModelList;
    private ChatListAdapter chatListAdapter;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());
        activityChatBinding.backArrow.setOnClickListener(v -> finish());
        RoomDB roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        getRequiredData();
        setupAdapter();
        setupSearch();
    }

    private void getRequiredData() {
        chatUserModelList = new ArrayList<>();
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHAT_LIST).getMasterSyncDataJsonArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            String[] nameSplit = jsonObject.optString("Name").split(" - ");
            String name = "", designation = "", hq = "";
            if (nameSplit.length == 4) {
                continue;
//                hq = nameSplit[3];
//                designation = nameSplit[2];
//                name = nameSplit[0] + " - " + nameSplit[1];
            } else if (nameSplit.length == 3) {
                hq = nameSplit[2];
                designation = nameSplit[1];
                name = nameSplit[0];
            } else if (nameSplit.length == 2) {
                continue;
//                designation = jsonObject.optString("Designation");
//                name = nameSplit[0] + " - " + nameSplit[1];
            } else if (nameSplit.length == 1) {
                designation = jsonObject.optString("Designation");
                name = nameSplit[0];
            }
            ChatUserModel chatUserModel = new ChatUserModel(name, jsonObject.optString("Code"), jsonObject.optString("SF_Name"), jsonObject.optString("Division_Code"), jsonObject.optString("SF_Type"), jsonObject.optString("Designation"), designation, hq);
            chatUserModelList.add(chatUserModel);
        }
    }

    private void setupAdapter() {
        chatUserModelList.sort(Comparator.comparingInt(ChatUserModel::getRank).thenComparing(ChatUserModel::getName));
        chatListAdapter = new ChatListAdapter(this, chatUserModelList, userClickListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        activityChatBinding.userRecyclerview.setLayoutManager(mLayoutManager);
        activityChatBinding.userRecyclerview.setAdapter(chatListAdapter);
    }

    private final ChatListAdapter.UserClickListener userClickListener = (chatUserModel, position) -> {
        Log.d("user selected", position + " -> " + chatUserModel.getName());
        for (int i = 0; i < chatUserModelList.size(); i++) {
            chatUserModelList.get(i).setSelected(i == position);
        }
        chatListAdapter.notifyDataSetChanged();
    };

    private void setupSearch() {
        activityChatBinding.userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

    }

    private void filter(String text) {
        List<ChatUserModel> filteredList = new ArrayList<>();
        for (ChatUserModel s : chatUserModelList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getDesignation().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(s);
            }
        }
        if (chatListAdapter != null) {
            filteredList.sort(Comparator.comparingInt(ChatUserModel::getRank).thenComparing(ChatUserModel::getName));
            chatListAdapter.filterList(filteredList);
        }
    }

}