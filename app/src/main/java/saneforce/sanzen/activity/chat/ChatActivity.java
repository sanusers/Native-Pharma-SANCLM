package saneforce.sanzen.activity.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.chat.adapter.ChatAdapter;
import saneforce.sanzen.activity.chat.adapter.ChatListAdapter;
import saneforce.sanzen.activity.chat.model.ChatMessage;
import saneforce.sanzen.activity.chat.model.ChatUserModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityChatBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.ChatTableDetails.ChatDataDao;
import saneforce.sanzen.roomdatabase.ChatTableDetails.ChatDataTable;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding activityChatBinding;
    private MasterDataDao masterDataDao;
    private List<ChatUserModel> chatUserModelList;
    private ChatListAdapter chatListAdapter;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private ApiInterface apiInterface;
    private ChatDataDao chatDataDao;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        activityChatBinding.backArrow.setOnClickListener(v -> finish());
        RoomDB roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        chatDataDao = roomDB.chatDataDao();
        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        chatDataDao.deleteAllLocalData();
        setupSearch();
        syncChat();
        getRequiredData();
        setupAdapter();
    }

    private void syncChat() {
        if (UtilityClass.isNetworkAvailable(ChatActivity.this)) {
            try {
                apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(ChatActivity.this);
                jsonObject.put("tableName", "getconversation");
                jsonObject.put("sfcode", SharedPref.getSfCode(this));
                jsonObject.put("SF_Name", SharedPref.getSfName(this));
                jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                jsonObject.put("Message_Date", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_42));
                Log.d("Chat", "sync Chat: " + jsonObject);
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "get/chat");
                Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
                if (call != null) {
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            boolean success = false;
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject2 = new JSONObject();
                            if (response.isSuccessful()) {
                                Log.e("sync", "response : " + response.body().toString());
                                try {
                                    JsonElement jsonElement = response.body();
                                    if (!jsonElement.isJsonNull()) {
                                        if (jsonElement.isJsonArray()) {
                                            jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                            success = true;
                                        } else if (jsonElement.isJsonObject()) {
                                            jsonObject2 = new JSONObject(jsonElement.getAsJsonObject().toString());
                                            if (!jsonObject2.has("success")) {
                                                jsonArray.put(jsonObject2);
                                                success = true;
                                            } else if (jsonObject2.has("success") && !jsonObject2.getBoolean("success")) {
                                                masterDataDao.saveMasterSyncStatus(Constants.CHAT_CONVERSATION, 1); // only update sync status and no need to overwrite previously saved data when failed
                                            }
                                        }
                                        if (success) {
                                            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CHAT_CONVERSATION, jsonArray.toString(), 2));
                                            insertChatConversation(jsonArray);
                                            getRequiredData();
                                            setupAdapter();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                            t.printStackTrace();
                            masterDataDao.saveMasterSyncStatus(Constants.CHAT_CONVERSATION, 1);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CommonUtilsMethods.showToastMessage(ChatActivity.this, "Kindly Sync to get latest Messages!");
        }
    }

    private void insertChatConversation(JSONArray jsonArray) {
        try {
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    chatDataDao.saveChat(new ChatDataTable(jsonObject.optString("Msg_Id"), jsonObject.optString("MsgSubject"), jsonObject.optString("MsgDt"), jsonObject.optString("Message"), jsonObject.optString("isSender"), jsonObject.optString("MsgRecvDt"), jsonObject.optString("Ref_ID"), jsonObject.optString("Ref_ID_Name"), jsonObject.optString("Ref_IDTyp"), jsonObject.optString("MsgOwnerID"), jsonObject.optString("MsgOwner"), jsonObject.optString("Files")));
                }
            }
        } catch (JSONException e) {
            Log.e("MasterSync Chat", "insert Chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getRequiredData() {
        chatUserModelList = new ArrayList<>();
        ChatUserModel chatUserModel = new ChatUserModel("Admin", "admin", "Admin", SharedPref.getDivisionCode(ChatActivity.this), "", "", "", "");
        chatUserModelList.add(chatUserModel);
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
            chatUserModel = new ChatUserModel(jsonObject.optString("Name"), jsonObject.optString("Code"), jsonObject.optString("SF_Name"), jsonObject.optString("Division_Code"), jsonObject.optString("SF_Type"), jsonObject.optString("Designation"), designation, hq);
            ChatDataTable chatDataTable = chatDataDao.getLastChatData(chatUserModel.getCode());
            if (chatDataTable != null) {
                chatUserModel.setMessage(chatDataTable.getMessage());
                chatUserModel.setDate(getFriendlyDate(chatDataTable.getDate(), TimeUtils.FORMAT_35));
            }
            chatUserModelList.add(chatUserModel);
        }
    }

    private void setupAdapter() {
//        chatUserModelList.sort(Comparator.comparingInt(ChatUserModel::getRank).thenComparing(ChatUserModel::getName));
        chatListAdapter = new ChatListAdapter(this, chatUserModelList, userClickListener);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        activityChatBinding.userRecyclerview.setLayoutManager(mLayoutManager);
        activityChatBinding.userRecyclerview.setAdapter(chatListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activityChatBinding.userRecyclerview.getContext(), mLayoutManager.getOrientation());
        activityChatBinding.userRecyclerview.addItemDecoration(dividerItemDecoration);
    }

    private final ChatListAdapter.UserClickListener userClickListener = (chatUserModel, position) -> {
        Log.d("user selected", position + " -> " + chatUserModel.getName());
        for (int i = 0; i < chatUserModelList.size(); i++) {
            chatUserModelList.get(i).setSelected(i == position);
        }
        chatListAdapter.notifyDataSetChanged();
        setUpChat(chatUserModel);
    };

    private void setUpChat(ChatUserModel chatUserModel) {
        activityChatBinding.userName.setText(chatUserModel.getName());
        activityChatBinding.rlChatMain.setVisibility(View.VISIBLE);
        activityChatBinding.rlNoData.setVisibility(View.GONE);
        messages = new ArrayList<>();
        List<ChatDataTable> chatDataTableList = chatDataDao.getAllChatData(chatUserModel.getCode());
        if (!chatDataTableList.isEmpty()) {
            for (ChatDataTable chatDataTable : chatDataTableList) {
                String date = getFriendlyDate(chatDataTable.getDate(), TimeUtils.FORMAT_19), time = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_43, chatDataTable.getDate());
                if (chatDataTable.getIsSender().equalsIgnoreCase("0")) {
                    addMessageWithDateCheck(chatDataTable.getMessage(), time, date, ChatMessage.TYPE_LEFT);
                } else {
                    addMessageWithDateCheck(chatDataTable.getMessage(), time, date, ChatMessage.TYPE_RIGHT);
                }
            }
        }
        Log.d("Chat", "setUpChat: " + messages);

        chatAdapter = new ChatAdapter(this, messages);
        activityChatBinding.recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        activityChatBinding.recyclerChat.setAdapter(chatAdapter);
        activityChatBinding.recyclerChat.post(() -> {
            if (chatAdapter.getItemCount() > 0) {
                activityChatBinding.recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

        activityChatBinding.recyclerChat.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                activityChatBinding.recyclerChat.postDelayed(() -> {
                    if (chatAdapter.getItemCount() > 0) {
                        activityChatBinding.recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }, 100);
            }
        });

        activityChatBinding.recyclerChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisible = lm.findFirstVisibleItemPosition();
                if (firstVisible >= 0 && firstVisible < messages.size()) {
                    ChatMessage msg = messages.get(firstVisible);
                    if (msg.getMessageType() == ChatMessage.TYPE_DATE) {
                        setStickyHeader(msg.getDate());
                    } else {
                        for (int i = firstVisible; i >= 0; i--) {
                            if (messages.get(i).getMessageType() == ChatMessage.TYPE_DATE) {
                                setStickyHeader(messages.get(i).getDate());
                                break;
                            }
                        }
                    }
                }
            }
        });

        activityChatBinding.btnSend.setOnClickListener(v -> {
            String text = activityChatBinding.etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                if (UtilityClass.isNetworkAvailable(ChatActivity.this)) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(ChatActivity.this);
                        jsonObject.put("tableName", "svconversation");
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("SF_Name", SharedPref.getSfName(this));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                        jsonObject.put("SF_To_Code", chatUserModel.getCode());
                        jsonObject.put("SF_To_Name", chatUserModel.getSfName());
                        jsonObject.put("SF_Type", chatUserModel.getSfTypeCode());
                        jsonObject.put("Message_Subject", "Notification Message");
                        jsonObject.put("Message_Content", text);
                        jsonObject.put("Reference_Id", "");
                        jsonObject.put("Reference_Name", "");
                        jsonObject.put("Reference_Type", "");
                        jsonObject.put("FileAttachmentPath", "");
                        jsonObject.put("Rsf", chatUserModel.getCode());
                        jsonObject.put("Message_Date", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_42));
                        Log.d("Chat", "send Chat: " + jsonObject);
                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/chat");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
                        if (call != null) {
                            call.enqueue(new Callback<>() {
                                @Override
                                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                    Log.d("Chat", "onResponse: " + response.body());
                                    if (response.body() != null) {
                                        try {
                                            JSONObject responseObj = new JSONObject(response.body().toString());
                                            if (responseObj.optBoolean("success")) {
                                                addMessage(chatUserModel, text);
                                            } else {
                                                CommonUtilsMethods.showToastMessage(ChatActivity.this, getString(R.string.please_try_again));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            CommonUtilsMethods.showToastMessage(ChatActivity.this, getString(R.string.please_try_again));
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                    Log.e("test", "failed : " + t);
                                    t.printStackTrace();
                                    CommonUtilsMethods.showToastMessage(ChatActivity.this, getString(R.string.please_check_your_internet_connection));
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonUtilsMethods.showToastMessage(ChatActivity.this, getString(R.string.please_check_your_internet_connection));
                    }
                } else {
                    CommonUtilsMethods.showToastMessage(ChatActivity.this, getString(R.string.no_network));
                }
            }
        });
    }

    private String getFriendlyDate(String dateTime, String format) {
        Calendar msgCal = Calendar.getInstance();
        msgCal.setTimeInMillis(TimeUtils.getMillis(TimeUtils.FORMAT_1, dateTime));

        Calendar today = Calendar.getInstance();

        if (today.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)) {
            return "Today";
        }

        today.add(Calendar.DAY_OF_YEAR, -1);
        if (today.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday";
        }

        return TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, format, dateTime);
    }

    private String lastDate = "";

    private void addMessageWithDateCheck(String msg, String time, String date, int type) {
        if (!date.equals(lastDate)) {
            messages.add(new ChatMessage("", "", date, ChatMessage.TYPE_DATE));
            lastDate = date;
        }
        messages.add(new ChatMessage(msg, time, date, type));
    }

    private void addMessage(ChatUserModel chatUserModel, String message) {
        long now = System.currentTimeMillis();
        String friendlyDate = getFriendlyDate(now);
        String time = TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_43), dateTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_1);

        if (messages.isEmpty() || !messages.get(messages.size() - 1).getDate().equals(friendlyDate)) {
            messages.add(new ChatMessage("", "", friendlyDate, ChatMessage.TYPE_DATE));
        }

        messages.add(new ChatMessage(message, time, friendlyDate, ChatMessage.TYPE_RIGHT));
        String id = String.valueOf(Long.parseLong(chatDataDao.getLastID().getID()) + 1);
        chatDataDao.saveChat(new ChatDataTable(id, "sub", dateTime, message, "1", dateTime, "", "", "", chatUserModel.getCode(), chatUserModel.getSfName(), ""));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        activityChatBinding.recyclerChat.scrollToPosition(messages.size() - 1);
        activityChatBinding.etMessage.setText("");
    }

    private String getFriendlyDate(long timeMillis) {
        Calendar msgCal = Calendar.getInstance();
        msgCal.setTimeInMillis(timeMillis);

        Calendar today = Calendar.getInstance();

        if (today.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)) {
            return "Today";
        }

        today.add(Calendar.DAY_OF_YEAR, -1);
        if (today.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday";
        }

        return new SimpleDateFormat(TimeUtils.FORMAT_19, Locale.getDefault()).format(new Date(timeMillis));
    }

    private void setStickyHeader(String date) {
        if (!activityChatBinding.tvStickyHeader.getText().toString().equals(date)) {
            activityChatBinding.tvStickyHeader.setVisibility(View.VISIBLE);
            activityChatBinding.tvStickyHeader.setText(date);
        }
    }

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
//            filteredList.sort(Comparator.comparingInt(ChatUserModel::getRank).thenComparing(ChatUserModel::getName));
            chatListAdapter.filterList(filteredList);
        }
    }

}