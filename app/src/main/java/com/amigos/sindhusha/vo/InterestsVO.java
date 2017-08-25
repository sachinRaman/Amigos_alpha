package com.amigos.sindhusha.vo;

/**
 * Created by Sachin on 8/14/2017.
 */

public class InterestsVO {
    String interest;
    int pref;
    public InterestsVO(String name, int pref){
        this.interest = name;
        this.pref = pref;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public int getPref() {
        return pref;
    }

    public void setPref(int pref) {
        this.pref = pref;
    }
}
