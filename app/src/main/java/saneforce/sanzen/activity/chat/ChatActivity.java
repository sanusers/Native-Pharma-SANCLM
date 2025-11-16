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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import saneforce.sanzen.activity.chat.adapter.ChatAdapter;
import saneforce.sanzen.activity.chat.adapter.ChatListAdapter;
import saneforce.sanzen.activity.chat.model.ChatMessage;
import saneforce.sanzen.activity.chat.model.ChatUserModel;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityChatBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
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
        getRequiredData();
        setupAdapter();
        setupSearch();
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
        chatAdapter = new ChatAdapter(this, messages);
        activityChatBinding.recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        activityChatBinding.recyclerChat.setAdapter(chatAdapter);
        activityChatBinding.recyclerChat.post(() ->
                activityChatBinding.recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1)
        );

        activityChatBinding.recyclerChat.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                activityChatBinding.recyclerChat.postDelayed(() ->
                        activityChatBinding.recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1), 100);
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
                        // Find previous date separator
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

        // Dummy initial messages
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "15 Nov 2025", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "15 Nov 2025", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "15 Nov 2025", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "15 Nov 2025", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "15 Nov 2025", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "15 Nov 2025", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "15 Nov 2025", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "15 Nov 2025", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Yesterday", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Yesterday", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Yesterday", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Yesterday", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Yesterday", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Yesterday", ChatMessage.TYPE_RIGHT);


        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Today", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Today", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Today", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Today", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Today", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Today", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Today", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Today", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Today", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Today", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Today", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Today", ChatMessage.TYPE_RIGHT);
        addMessageWithDateCheck("Hi Vishnu, I need your support for my next call, Are you available now?", "5:00 PM", "Today", ChatMessage.TYPE_LEFT);
        addMessageWithDateCheck("Sorry, I am not available now.", "5:02 PM", "Today", ChatMessage.TYPE_RIGHT);
        chatAdapter.notifyDataSetChanged();

        activityChatBinding.btnSend.setOnClickListener(v -> {
            String text = activityChatBinding.etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                addMessage(text);
                // TODO: 17-11-2025 implement send chat 
                /*
                 * http://edetailing.sanffa.info/iOSServer/db_api.php/?axn=get%2Fchat
                 * {"AppName":"SAN ZEN","Appver":"V.1.0.4","Mod":"Android-ZEN","sf_emp_id":"RNXPDM01","sfname":"Demo Manager","Device_version":"14","Device_name":"samsung - SM-X205","language":"en","sf_type":"2","Designation":"ASM","state_code":"4","subdivision_code":"328,","key":"reva2025","Configurl":"http:\/\/edetailing.sanffa.info\/","battery":"77","tableName":"svconversation","sfcode":"MR9024","SF_Name":"Kalpana","SF_To_Code":"MR9008","SF_To_Name":"GIGI HADID","SF_Type":"1","Message_Subject":"Notification Message","Message_Content":"hello","Reference_Id":"","Reference_Name":"","Reference_Type":"","FileAttachmentPath":"","division_code":"15,","Rsf":"MR9024","Message_Date":"2025-11-05 00:00:00"}
                 * */
            }
        });
    }

    private String lastDate = "";

    private void addMessageWithDateCheck(String msg, String time, String date, int type) {
        if (!date.equals(lastDate)) {
            messages.add(new ChatMessage("", "", date, ChatMessage.TYPE_DATE));
            lastDate = date;
        }
        messages.add(new ChatMessage(msg, time, date, type));
    }

    private void addMessage(String message) {
        long now = System.currentTimeMillis();
        String friendlyDate = getFriendlyDate(now);
        String time = TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_43);

        if (messages.isEmpty() || !messages.get(messages.size() - 1).getDate().equals(friendlyDate)) {
            messages.add(new ChatMessage("", "", friendlyDate, ChatMessage.TYPE_DATE));
        }

        messages.add(new ChatMessage(message, time, friendlyDate, ChatMessage.TYPE_RIGHT));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        activityChatBinding.recyclerChat.scrollToPosition(messages.size() - 1);
        activityChatBinding.etMessage.setText("");
    }

    private String getFriendlyDate(long timeMillis) {
        Calendar msgCal = Calendar.getInstance();
        msgCal.setTimeInMillis(timeMillis);

        Calendar today = Calendar.getInstance();

        // Check today
        if (today.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)) {
            return "Today";
        }

        // Check yesterday
        today.add(Calendar.DAY_OF_YEAR, -1);
        if (today.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday";
        }

        // Otherwise return full date
        return new SimpleDateFormat(TimeUtils.FORMAT_19, Locale.getDefault())
                .format(new Date(timeMillis));
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