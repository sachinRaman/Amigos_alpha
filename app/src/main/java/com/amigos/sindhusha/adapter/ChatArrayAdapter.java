package com.amigos.sindhusha.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.vo.ChatMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private TextView time;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.chat_right, parent, false);
        }else{
            row = inflater.inflate(R.layout.chat_left, parent, false);
        }
        row.setEnabled(false);
        row.setOnClickListener(null);
        Typeface typeFaceCalibri = Typeface.createFromAsset(context.getAssets(),"fonts/Calibri/Calibri.ttf");
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setTypeface(typeFaceCalibri);
        chatText.setText(chatMessageObj.message);
        time = (TextView) row.findViewById(R.id.time_right);
        time.setTypeface(typeFaceCalibri);
        time.setText(chatMessageObj.time);
        return row;
    }
}