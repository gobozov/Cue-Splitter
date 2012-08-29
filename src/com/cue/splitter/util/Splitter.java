package com.cue.splitter.util;

import android.os.Message;
import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Track;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 29.08.12
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class Splitter {
    private static final char[] ILLEGAL_NAME_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};


    public static void main(String[] args) throws IOException {
        CueParser parser = new CueParser();
        CueFile cueFile = null;
        cueFile = parser.parse(new File("C:\\temp\\01. Sunbird - Emitter (Promo) - 2011.cue"));

        Splitter splitter = new Splitter();
        splitter.splitCue(cueFile, "c:/temp/");

    }

    public void splitCue(CueFile cueFile, String targetDir) {

        String path = lookupTargetFile(cueFile);
        try {
            MP3File mp3 = new MP3File(path);
            MP3AudioHeader header = (MP3AudioHeader) mp3.getAudioHeader();
            if (header.isVariableBitRate()) {
                System.out.println("Not support VBR mp3 now!");
                return;
            } else {
                Track previousTrack = null;
                for (Track t : cueFile.getCheckedTracks()) {

                    if (previousTrack != null) {

                        int startSec = previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds();
                        int endSec = t.getIndex().getPosition().getMinutes() * 60 + t.getIndex().getPosition().getSeconds();
                        byte[] data = readChunk(new File(path), header, startSec * 1000, endSec * 1000);
                        File targetFile = getTrackFile(previousTrack, cueFile, targetDir);
                        writeFile(targetFile, data);
                        //setID3Tags(cueFile, previousTrack, targetFile);
                    }
                    previousTrack = t;

                }
                if (previousTrack != null) {
                    int startSec = previousTrack.getIndex().getPosition().getMinutes() * 60 + previousTrack.getIndex().getPosition().getSeconds();
                    int endSec = header.getTrackLength();
                    byte[] data = readChunk(new File(path), header, startSec * 1000, endSec * 1000);
                    File targetFile = getTrackFile(previousTrack, cueFile, targetDir);
                    writeFile(targetFile, data);
                   // setID3Tags(cueFile, previousTrack, targetFile);

                }

            }
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


    private void setID3Tags(CueFile cueFile, Track t, File targetFile) {
        try {
            MP3File mp3 = (MP3File) AudioFileIO.read(targetFile);
            ID3v1Tag tag = new ID3v1Tag();
            tag.setField(FieldKey.TRACK, String.valueOf(t.getPosition()));
            tag.setField(FieldKey.ARTIST, t.getPerformer() == null ? cueFile.getPerformer() : t.getPerformer());
            tag.setField(FieldKey.TITLE, t.getTitle());
            tag.setField(FieldKey.ALBUM,cueFile.getTitle());

            ID3v24Tag tag2 = new ID3v24Tag();
            tag2.setField(FieldKey.TRACK, String.valueOf(t.getPosition()));
            tag2.setField(FieldKey.ARTIST, t.getPerformer() == null ? cueFile.getPerformer() : t.getPerformer());
            tag2.setField(FieldKey.TITLE, t.getTitle());
            tag2.setField(FieldKey.ALBUM,cueFile.getTitle());

            mp3.setID3v1Tag(tag);
            mp3.setID3v2Tag(tag2);
            mp3.commit();
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
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }

    }


    private byte[] readChunk(File sourceFile, MP3AudioHeader header, int startTime, int endTime) {
        byte[] result = null;
        RandomAccessFile randomAccessFile = null;
        long mp3StartIndex = header.getMp3StartByte();
        int trackLengthMs = header.getTrackLength() * 1000;
        long bitRate = header.getBitRateAsNumber();
        long beginIndex = bitRate * 1024 / 8 / 1000 * startTime + mp3StartIndex;
        long endIndex = beginIndex + bitRate * 1024 / 8 / 1000 * (endTime - startTime);
        if (endTime > trackLengthMs)
            endIndex = sourceFile.length() - 1;


        System.out.println("mp3StartIndex = " + mp3StartIndex + " trackLengthMs = " + trackLengthMs + " bitRate = " + bitRate);
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
