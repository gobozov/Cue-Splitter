package com.cue.splitter;

import com.cue.splitter.data.CueFile;
import com.cue.splitter.data.Index;
import com.cue.splitter.data.Track;
import com.cue.splitter.soundfile.CheapSoundFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.08.12
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class CueParser {


    private static final String CUE_TITLE = "TITLE";
    private static final String CUE_PERFORMER = "PERFORMER";
    private static final String CUE_FILE = "FILE";
    private static final String CUE_TRACK = "TRACK";
    private static final String CUE_INDEX = "INDEX";


    public CueFile parse(File file) throws FileNotFoundException {
        InputStream stream = new FileInputStream(file);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(stream, "cp1251"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CueFile cueFile = new CueFile();
        cueFile.setCuePath(file.getAbsolutePath());
        cueFile.setCueDir(file.getParent());
        try {

            String line = trim(br.readLine());
            boolean isTrack = false;

            while (line != null) {

                if (line.startsWith(CUE_TITLE) && !isTrack)
                    cueFile.setTitle(line.replace(CUE_TITLE, "").trim());

                if (line.startsWith(CUE_FILE) && !isTrack) {
                    cueFile.setFile(replaceCueTypes(line.replace(CUE_FILE, "").trim()));
                    cueFile.setExtention(getExtention(cueFile.getFile()));
                }

                if (line.startsWith(CUE_PERFORMER) && !isTrack)
                    cueFile.setPerformer(line.replace(CUE_PERFORMER, "").trim());

                if (line.startsWith(CUE_TRACK)) {
                    isTrack = true;
                    Track track = new Track();
                    line = trim(br.readLine());
                    while (line != null && !line.startsWith(CUE_TRACK)) {
                        if (line.startsWith(CUE_TITLE))
                            track.setTitle(line.replace(CUE_TITLE, "").trim());
                        if (line.startsWith(CUE_PERFORMER))
                            track.setPerformer(line.replace(CUE_PERFORMER, "").trim());
                        if (line.startsWith(CUE_INDEX))
                            track.setIndex(new Index(line.split("\\s")[1], line.split("\\s")[2]));
                        line = trim(br.readLine());
                    }
                    cueFile.getTracks().add(track);
                    continue;
                }
                line = trim(br.readLine());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cueFile;

    }

    private String getExtention(String fileName) {
        fileName = replaceCueTypes(fileName);
        String[] components = fileName.split("\\.");
        if (components.length < 2)
            return "mp3";
        else
            return components[components.length - 1];

    }

    private String replaceCueTypes(String s) {
        List<String> types = Arrays.asList(new String[]{"BINARY", "MOTOROLA", "AIFF", "WAVE", "MP3"});
        for (String type : types) {
            if (s.contains(type))
                s = s.replace(type, "").trim();
        }
        return s;
    }

    private String trim(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("\\u0000", "").replaceAll("\"", "");

    }


    public static void main(String[] args) throws IOException {
        CueParser parser = new CueParser();
        CueFile cueFile = null;
        cueFile = parser.parse(new File("C:\\temp\\01. Sunbird - Emitter (Promo) - 2011.cue"));
       // System.out.println("cueFile = " + cueFile);
        //cueFile = parser.parse(new File("C:\\temp\\VA - Record Трансмиссия Vol 1 - Mixed by DJ Feel.cue"));
        // System.out.println("cueFile = " + cueFile);
        // cueFile = parser.parse(new File("C:\\temp\\[VA] Hard Dance Mania Vol 13 Mixed by Pulsedriver.cue"));
        // System.out.println("cueFile = " + cueFile);
        //cueFile = parser.parse(new File("C:\\temp\\Various - DJ Anna Lee - 7 Days Of Love.cue"));
        //System.out.println("cueFile = " + cueFile);
      // cueFile = parser.parse(new File("C:\\temp\\7Б - Молодые ветра.flac.cue"));
        System.out.println("cueFile = " + cueFile);


        CueSplitter splitter = new CueSplitter();
        splitter.splitCue(cueFile, "c:/temp/");


    }

    public int secondsToFrames(double seconds, CheapSoundFile cheapSoundFile) {
        int secondsToFrames = (int) (1.0 * seconds * cheapSoundFile.getSampleRate() / cheapSoundFile.getSamplesPerFrame() + 0.5);
        return secondsToFrames;
    }
}
