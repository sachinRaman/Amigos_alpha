package com.amigos.sindhusha.vo;

/**
 * Created by Sachin on 7/18/2017.
 */

public class PetPeevesVO {
    String name;
    int pref;
    public PetPeevesVO(String name, int pref){
        this.name = name;
        this.pref = pref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPref() {
        return pref;
    }

    public void setPref(int pref) {
        this.pref = pref;
    }
}
