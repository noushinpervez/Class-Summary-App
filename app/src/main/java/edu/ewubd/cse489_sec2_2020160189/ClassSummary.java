package edu.ewubd.cse489_sec2_2020160189;

public class ClassSummary {
    String id = "";
    String course = "";
    String type = "";
    long date = 0;
    int lecture = 0;
    String topic = "";
    String summary = "";

    public ClassSummary(String id, String course, String type, long date, int lecture, String topic, String summary) {
        this.id = id;
        this.course = course;
        this.topic = topic;
        this.type = type;
        this.date = date;
        this.lecture = lecture;
        this.summary = summary;
    }

    public String getId() {
        return id;
    }
}