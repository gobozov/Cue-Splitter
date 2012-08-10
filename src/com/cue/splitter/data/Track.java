package com.cue.splitter.data;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.08.12
 * Time: 1:48
 * To change this template use File | Settings | File Templates.
 */
public class Track {

    private String title;
    private String performer;
    private Index index;
    private boolean isChecked = true;

    public Track() {
    }

    public Track(String title, String performer, Index index) {
        this.title = title;
        this.performer = performer;
        this.index = index;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "\n Track{" +
                "title=" + title  +
                ", performer=" + performer  +
                ", index=" + index +
                '}';
    }
}
