package com.cue.splitter.data;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.08.12
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class Index {

    private int number;
    private Position position = null;


    public Index(int number, Position position) {
        this.number = number;
        this.position = position;
    }

    public Index(String number, String position) {
        String[] array = position.split(":");
        this.number = Integer.parseInt(number);
        this.position = new Position(Integer.parseInt(array[0]), Integer.parseInt(array[1]), Integer.parseInt(array[2]));
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return  number + " " + position ;
    }
}
