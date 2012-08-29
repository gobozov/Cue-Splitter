package com.cue.splitter.util;

import com.cue.splitter.data.CueFile;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

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
        try {
            AudioFile flacFile = flacReader.read(new File(path));
            AudioHeader flacHeader = flacFile.getAudioHeader();
            long bitRate = flacHeader.getBitRateAsNumber();


        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }


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
