package com.cue.splitter.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.cue.splitter.exception.ReadSoundFileException;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Track;
import com.cue.splitter.soundfile.CheapMP3;
import com.cue.splitter.soundfile.CheapSoundFile;
import com.mpatric.mp3agic.*;

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
    private Context context;

    public CueSplitter(Context context) {
        this.context = context;
    }

    public CheapSoundFile readTargetFile(CueFile cueFile, final Handler handler) throws IOException {
        String target = lookupTargetFile(cueFile);
        CheapSoundFile cheapSoundFile = null;

        cheapSoundFile = CheapSoundFile.create(target, new CheapSoundFile.ProgressListener() {
            @Override
            public boolean reportProgress(double fractionComplete) {
                if (handler != null) {
                    Message message = new Message();
                    message.arg1 = ((int) (fractionComplete * 100));
                    handler.handleMessage(message);
                }
                return true;
            }

        });

        return cheapSoundFile;
    }

    public boolean splitCue(CheapSoundFile cheapSoundFile, CueFile cueFile, String targetDir, final Handler handler) throws IOException {
        int count = 0;
        Track previousTrack = null;
        for (Track t : cueFile.getCheckedTracks()) {
            if (previousTrack != null) {

                int startFrame = secondsToFrames(previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds(), cheapSoundFile);
                int endFrame = secondsToFrames(t.getIndex().getPosition().getMinutes() * 60 + t.getIndex().getPosition().getSeconds(), cheapSoundFile);
                File targetFile = getTrackFile(previousTrack, cueFile, targetDir);
                cheapSoundFile.WriteFile(targetFile, startFrame, endFrame - startFrame);

                // set ID3 tags
                setID3Tags(cheapSoundFile, cueFile, previousTrack, targetFile);
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
            File targetFile = getTrackFile(previousTrack, cueFile, targetDir);
            cheapSoundFile.WriteFile(targetFile, startFrame, endFrame - startFrame);
            setID3Tags(cheapSoundFile, cueFile, previousTrack, targetFile);

        }
        return true;
    }

    private void setID3Tags(CheapSoundFile cheapSoundFile, CueFile cueFile, Track t, File targetFile) {
        // check id3 tags
        if (!Settings.getBoolean(context, Settings.PREF_USE_ID3_TAGS))
            return;
        // set ID3 Tags
        if (cheapSoundFile instanceof CheapMP3) {
            try {
                Mp3File mp3file = new Mp3File(targetFile.getAbsolutePath());
                ID3v1 id3v1Tag = new ID3v1Tag();
                ID3v2 id3v2Tag = new ID3v23Tag();

                mp3file.setId3v1Tag(id3v1Tag);
                mp3file.setId3v2Tag(id3v2Tag);

                id3v1Tag.setTrack(String.valueOf(t.getPosition()));
                id3v2Tag.setTrack(String.valueOf(t.getPosition()));

                id3v1Tag.setArtist(t.getPerformer() == null ? cueFile.getPerformer() : t.getPerformer());
                id3v2Tag.setArtist(t.getPerformer() == null ? cueFile.getPerformer() : t.getPerformer());

                id3v1Tag.setTitle(t.getTitle());
                id3v2Tag.setTitle(t.getTitle());

                id3v1Tag.setAlbum(cueFile.getTitle());
                id3v2Tag.setAlbum(cueFile.getTitle());

                mp3file.save(targetFile.getAbsolutePath());
            } catch (UnsupportedTagException e) {
                e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            } catch (NotSupportedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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
