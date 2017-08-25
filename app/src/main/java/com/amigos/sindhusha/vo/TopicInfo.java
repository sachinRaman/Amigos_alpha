package com.amigos.sindhusha.vo;

/**
 * Created by sindhusha on 12/5/17.
 */

public class TopicInfo {
    String topicName;
    String topicDesc;
    int topicId;


    public TopicInfo(int topicId, String topicName, String topicDesc){
        this.topicId=topicId;
        this.topicName=topicName;
        this.topicDesc=topicDesc;
    }
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDesc() {
        return topicDesc;
    }

    public void setTopicDesc(String topicDesc) {
        this.topicDesc = topicDesc;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }
    @Override
    public String toString(){
        return getTopicName()+"\n\n"+getTopicDesc()+"\n";
    }
}
