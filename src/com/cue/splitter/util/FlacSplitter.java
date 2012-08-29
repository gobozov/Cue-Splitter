package com.cue.splitter.util;

import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Track;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 29.08.12
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class FlacSplitter {

    private static final char[] ILLEGAL_NAME_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    public void splitCue(CueFile cueFile, String targetDir) throws InvalidAudioFrameException, IOException, ReadOnlyFileException, TagException, CannotReadException {
        //String path = "C:\\temp\\7Б - Молодые ветра.flac";
        String path = lookupTargetFile(cueFile);
        FlacFileReader flacReader = new FlacFileReader();
        AudioFile audioFile = flacReader.read(new File(path));
        System.out.println("audioFile) = " +audioFile.displayStructureAsPlainText());
        Track previousTrack = null;
        for (Track t : cueFile.getCheckedTracks()) {

            if (previousTrack != null) {

                int startSec = previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds();
                int endSec = t.getIndex().getPosition().getMinutes() * 60 + t.getIndex().getPosition().getSeconds();
                byte[] data = readChunk(new File(path), audioFile.getAudioHeader(), startSec , endSec);
                File targetFile = getTrackFile(previousTrack, cueFile, targetDir);
                writeFile(targetFile, data);

            }
            previousTrack = t;

        }
        if (previousTrack != null) {
            int startSec = previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds();
            int endSec = audioFile.getAudioHeader().getTrackLength();
            byte[] data = readChunk(new File(path), audioFile.getAudioHeader(), startSec , endSec );
            File targetFile = getTrackFile(previousTrack, cueFile, targetDir);
            writeFile(targetFile, data);

        }

    }

    private byte[] readChunk(File sourceFile, AudioHeader header, int startTime, int endTime) {
        byte[] result = null;
        RandomAccessFile randomAccessFile = null;
        //long startIndex = header.getMp3StartByte();
        long startIndex = 0;
        int trackLengthMs = header.getTrackLength() * 1000;
        long bitRate = header.getBitRateAsNumber();

        long beginIndex =  startTime * 75 * 588;
        System.out.println("beginIndex = " + beginIndex);
        long endIndex = beginIndex +  ((endTime - startTime)  * 75 * 588);
        System.out.println("endIndex = " + endIndex);

        if (endTime > trackLengthMs)
            endIndex = sourceFile.length() - 1;


     //  System.out.println("mp3StartIndex = " + startIndex + " trackLengthMs = " + trackLengthMs + " bitRate = " + bitRate);
     //  System.out.println("beginIndex = " + beginIndex + " endIndex = " + endIndex);
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

    public static void main(String[] args) throws IOException, InvalidAudioFrameException, ReadOnlyFileException, CannotReadException, TagException {
        CueParser parser = new CueParser();
        CueFile cueFile = null;
        cueFile = parser.parse(new File("C:\\temp\\7Б - Молодые ветра.cue"));

        FlacSplitter splitter = new FlacSplitter();
        splitter.splitCue(cueFile, "c:/temp/");

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

    private boolean writeFile(File file, byte[] data) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
