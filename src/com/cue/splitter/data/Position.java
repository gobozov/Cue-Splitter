package com.cue.splitter.data;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.08.12
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public class Position {


    private int minutes = 0;
    private int seconds = 0;
    private int frames = 0;


    public Position(int minutes, int seconds, int frames) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.frames = frames;
    }

    public int getTotalFrames() {
        int result = frames + (75 * (seconds + 60 * minutes));
        return result;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    @Override
    public String toString() {
        return minutes + ":" + seconds + ":" + frames;
    }
}
