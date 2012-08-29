package com.cue.splitter.util;

import com.cue.splitter.data.CueFile;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 29.08.12
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class FlacSplitter {


    public void splitCue(CueFile cueFile, String targetDir){
        String path = lookupTargetFile(cueFile);
        FlacFileReader flacReader = new FlacFileReader();

    }

    private byte[] readChunk(File sourceFile, AudioHeader header, int startTime, int endTime) {
        byte[] result = null;
        RandomAccessFile randomAccessFile = null;
        //long startIndex = header.getMp3StartByte();
        long startIndex = 0;
        int trackLengthMs = header.getTrackLength() * 1000;
        long bitRate = header.getBitRateAsNumber();
        long beginIndex = bitRate * 1024 / 8 / 1000 * startTime + startIndex;
        long endIndex = beginIndex + bitRate * 1024 / 8 / 1000 * (endTime - startTime);
        if (endTime > trackLengthMs)
            endIndex = sourceFile.length() - 1;


        System.out.println("mp3StartIndex = " + startIndex + " trackLengthMs = " + trackLengthMs + " bitRate = " + bitRate);
        System.out.println("beginIndex = " + beginIndex + " endIndex = " + endIndex);
        try {
            randomAccessFile = new RandomAccessFile(sourceFile, "r");
            randomAccessFile.seek(beginIndex);
            int size = (int) (endIndex - beginIndex);
            result = new byte[size];
            randomAccessFile.read(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    private String lookupTargetFile(CueFile cueFile) {
        String file = cueFile.getFile();
        if (file != null) {
            file = cueFile.getCueDir() + "/" + file;
        } else {
            file = cueFile.getCuePath().replace("cue", cueFile.getExtention());
        }
        return file;
    }
}
