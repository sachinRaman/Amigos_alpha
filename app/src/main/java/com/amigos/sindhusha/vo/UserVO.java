package com.amigos.sindhusha.vo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sindhusha on 12/5/17.
 */
public class UserVO  {
    String uuid;
    String nickName;
    String info = "Hello.";
    String status;
    int matchCount;
    Map<String,Integer> topicsPrefs;

    String age = "";
    String sex = "";
    String place = "";

    ArrayList<String> topicsOfInterest = new ArrayList<String>();

    int chooseList = 0;

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public Map<String, Integer> getTopicsPrefs() {
        return topicsPrefs;
    }

    public void setTopicsPrefs(Map<String,Integer> topicsPrefs) {
        this.topicsPrefs = topicsPrefs;
    }

    public UserVO(String uuid,String nickName,String info,String status,Map<String,Integer> topicsPrefs){
        this.uuid=uuid;
        this.nickName=nickName;
        this.info=info;
        this.status=status;
        this.topicsPrefs=topicsPrefs;
    }



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getInfo() { return info; }

    public void setInfo(String nickName) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public ArrayList<String> getTopicsOfInterest(){
        return topicsOfInterest;
    }
    public void setTopicsOfInterest(ArrayList<String> topicsOfInterest){
        this.topicsOfInterest = topicsOfInterest;
    }
}
