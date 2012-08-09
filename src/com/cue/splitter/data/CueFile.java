package com.cue.splitter.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.08.12
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class CueFile {


    private String file;
    private String title;
    private String performer;
    private String cuePath;
    private String cueDir;
    private String extention;
    private List<Track> tracks;


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Track> getTracks() {
        if (tracks == null)
            tracks = new LinkedList<Track>();
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getCuePath() {
        return cuePath;
    }

    public void setCuePath(String cuePath) {
        this.cuePath = cuePath;
    }

    public String getExtention() {
        return extention;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public String getCueDir() {
        return cueDir;
    }

    public void setCueDir(String cueDir) {
        this.cueDir = cueDir;
    }

    @Override
    public String toString() {
        String s = "CueFile{" + '\n' +
                "file=" + file + '\n' +
                ", title=" + title + '\n' +
                ", cuePath=" + cuePath + '\n' +
                '}';

        for (Track t: tracks){
            s += t;
        }
        return s;
    }
}
