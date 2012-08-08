package com.cue.splitter.soundfile;

import com.cue.splitter.CueParser;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Track;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 08.08.12
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */
public class CueSplitter {

    public void splitCue(CueFile cueFile, String targetDir) throws IOException {
        CueParser parser = new CueParser();
        String target = cueFile.getCuePath().replace("cue", cueFile.getExtention());
        CheapSoundFile cheapSoundFile = null;
        try {
            cheapSoundFile = CheapSoundFile.create(target, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Track previousTrack = null;
        for (Track t : cueFile.getTracks()) {

            if (previousTrack != null) {
                int startFrame = parser.secondsToFrames(previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds(), cheapSoundFile);
                int endFrame = parser.secondsToFrames(t.getIndex().getPosition().getMinutes() * 60 + t.getIndex().getPosition().getSeconds(), cheapSoundFile);
                cheapSoundFile.WriteFile(getTrackFile(previousTrack, cueFile, targetDir), startFrame, endFrame - startFrame);
            }
            previousTrack = t;
        }

        if (previousTrack != null){
            int startFrame = parser.secondsToFrames(previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds(), cheapSoundFile);
            int endFrame = cheapSoundFile.getNumFrames();
            cheapSoundFile.WriteFile(getTrackFile(previousTrack, cueFile, targetDir), startFrame, endFrame - startFrame);
        }

    }

    private File getTrackFile(Track track, CueFile cueFile, String targetDir){
        String title = track.getTitle() + (track.getPerformer() != null ? " - " + track.getPerformer(): " - " + cueFile.getPerformer());
        File file = new File(targetDir + title + "." + cueFile.getExtention());
        return file;
    }

}
