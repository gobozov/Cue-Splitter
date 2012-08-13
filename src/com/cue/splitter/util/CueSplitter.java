package com.cue.splitter.util;

import android.os.Handler;
import android.os.Message;
import com.cue.splitter.exception.ReadSoundFileException;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Track;
import com.cue.splitter.soundfile.CheapSoundFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 08.08.12
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */
public class CueSplitter {
    private static final char[] ILLEGAL_NAME_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    public boolean splitCue(CueFile cueFile, String targetDir, Handler handler) throws ReadSoundFileException, IOException {
        String target = lookupTargetFile(cueFile);
        CheapSoundFile cheapSoundFile = null;
        try {
            cheapSoundFile = CheapSoundFile.create(target, null);
        } catch (IOException e) {
            throw new ReadSoundFileException();
        }


        Track previousTrack = null;
        int count = 0;
        for (Track t : cueFile.getTracks()) {
            if (previousTrack != null) {
                int startFrame = secondsToFrames(previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds(), cheapSoundFile);
                int endFrame = secondsToFrames(t.getIndex().getPosition().getMinutes() * 60 + t.getIndex().getPosition().getSeconds(), cheapSoundFile);
                cheapSoundFile.WriteFile(getTrackFile(previousTrack, cueFile, targetDir), startFrame, endFrame - startFrame);
                // handler for progress
                if (handler != null) {
                    Message message = new Message();
                    message.arg1 = ++count;
                    handler.handleMessage(message);
                }
            }
            previousTrack = t;
        }

        if (previousTrack != null) {
            int startFrame = secondsToFrames(previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds(), cheapSoundFile);
            int endFrame = cheapSoundFile.getNumFrames();
            cheapSoundFile.WriteFile(getTrackFile(previousTrack, cueFile, targetDir), startFrame, endFrame - startFrame);
        }
        return true;
    }

    public boolean splitCue(CueFile cueFile, String targetDir)  throws ReadSoundFileException, IOException{
        return splitCue(cueFile, targetDir, null);
    }

    private File getTrackFile(Track track, CueFile cueFile, String targetDir) {
        String title = track.getTitle() + (track.getPerformer() != null ? " - " + track.getPerformer() : " - " + cueFile.getPerformer());
        title = cleanFileName(title);
        File file = new File(targetDir + title + "." + cueFile.getExtention());
        return file;
    }

    public String cleanFileName(String fileName) {
        StringBuilder cleanName = new StringBuilder();
        for (int i = 0; i < fileName.length(); i++) {
            char c = fileName.charAt(i);
            if (Arrays.binarySearch(ILLEGAL_NAME_CHARACTERS, c) < 0) {
                cleanName.append(c);
            }
        }
        return cleanName.toString();
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

    public int secondsToFrames(double seconds, CheapSoundFile cheapSoundFile) {
        int secondsToFrames = (int) (1.0 * seconds * cheapSoundFile.getSampleRate() / cheapSoundFile.getSamplesPerFrame() + 0.5);
        return secondsToFrames;
    }

    public int framesToSeconds(CheapSoundFile cheapSoundFile) {
        return cheapSoundFile.getNumFrames() / 75;
    }

}
