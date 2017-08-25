package com.amigos.sindhusha.adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amigos.sindhusha.R;
import com.amigos.sindhusha.dao.UserPrefsDAO;
import com.amigos.sindhusha.vo.TopicInfo;
import com.firebase.client.Firebase;

import java.util.ArrayList;

public class TopicPrefAdapter extends BaseAdapter{
    ArrayList<TopicInfo> topicNameList;
    Context context;

    private static LayoutInflater inflater=null;


    //String[] fixedColors={"#8E0D13","#0C6EB0","#E06323","#FF00FF","#006400","#5B3D8C","#EB113B","#0000FF"};


    private String TAG="TopicPrefAdapter";
    public TopicPrefAdapter(Context ctx, ArrayList<TopicInfo> topicNameList) {

        //Collections.shuffle(Arrays.asList(fixedColors));
        this.topicNameList=topicNameList;
        context=ctx;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i("TopicPrefAdapter", "list is::" + topicNameList);

    }
    @Override
    public int getCount() {
        return topicNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        TextView tvDesc;
        ImageView img;
        CheckBox cb;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.topic_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.tv_name);
        holder.tvDesc=(TextView) rowView.findViewById(R.id.tv_desc);
        holder.cb=(CheckBox)rowView.findViewById(R.id.cb_topic);

        //holder.img=(ImageView) rowView.findViewById(R.id.iv_profile);
        Log.i("TopicPrefAdapter", "setting text::" + topicNameList.get(position).getTopicName());

//        int colIndex=position;
//        if(colIndex>=fixedColors.length){
//            colIndex=fixedColors.length-1;
//        }
        Typeface typeFaceCalibri = Typeface.createFromAsset(context.getAssets(),"fonts/Calibri/Calibri.ttf");
        holder.tv.setTypeface(typeFaceCalibri);
        holder.tvDesc.setTypeface(typeFaceCalibri);
        holder.tv.setText(Html.fromHtml("<b><font color=\""+"#000000"+"\">" + topicNameList.get(position).getTopicName()+"</font></b>"));
        holder.tvDesc.setText(topicNameList.get(position).getTopicDesc());
        UserPrefsDAO userPrefsDAO=new UserPrefsDAO(context);
        if(userPrefsDAO.getPrefFromTopicName(topicNameList.get(position).getTopicName())!=null) {
            holder.tv.setTextColor(0xff006400);
            holder.tvDesc.setTextColor(0xff3b5998);
            holder.cb.setChecked(true);
        }
        else{
            holder.tv.setTextColor(0xff000000);
            holder.tvDesc.setTextColor(0xff000000);
            holder.cb.setChecked(false);
        }


        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    UserPrefsDAO userPrefsDAO = new UserPrefsDAO(context);
                    if (userPrefsDAO.getPrefFromTopicName(topicNameList.get(position).getTopicName()) == null) {
                        userPrefsDAO.addPref(position, topicNameList.get(position).getTopicName(), topicNameList.get(position).getTopicDesc());
                        notifyDataSetChanged();
                        changeFirebaseTopicsInfo(topicNameList.get(position).getTopicName(), 1);

                    }
                } else {
                    UserPrefsDAO userPrefsDAO = new UserPrefsDAO(context);
                    userPrefsDAO.removePrefFromName(topicNameList.get(position).getTopicName());
                    notifyDataSetChanged();
                    changeFirebaseTopicsInfo(topicNameList.get(position).getTopicName(), 0);
                }
            }
        });


        return rowView;
    }
    private void changeFirebaseTopicsInfo(String topicName,int addRemovFlag){
        SharedPreferences sp=context.getSharedPreferences("com.example.sindhusha", Context.MODE_PRIVATE);
        String uuid=sp.getString("uuid", "");
        Firebase curUser_FR= new Firebase("https://amigos-d1502.firebaseio.com/users/"+uuid+"/topics_prefs/" );
        curUser_FR.child(topicName).setValue(addRemovFlag);
    }

}
