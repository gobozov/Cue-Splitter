package com.cue.splitter.soundfile;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.FrameListener;
import org.kc7bfi.jflac.PCMProcessor;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.io.RandomFileInputStream;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 09.08.12
 * Time: 1:01
 * To change this template use File | Settings | File Templates.
 */
public class CheapFlac extends CheapSoundFile {

    public static Factory getFactory() {
        return new Factory() {
            public CheapSoundFile create() {
                return new CheapFlac();
            }

            public String[] getSupportedExtensions() {
                return new String[]{"flac"};
            }
        };
    }

    private int sampleRate;
    private int numFrames = 0;
    private int bps = 0;
    FLACDecoder decoder = null;
    ByteArrayOutputStream out = null;

    @Override
    public void ReadFile(File inputFile) throws FileNotFoundException, IOException {
        RandomFileInputStream is = new RandomFileInputStream(inputFile);
        out = new ByteArrayOutputStream();

        decoder = new FLACDecoder(is);
        decoder.addFrameListener(new FrameListener() {
            @Override
            public void processMetadata(Metadata metadata) {

            }

            @Override
            public void processFrame(Frame frame) {
                numFrames++;
            }

            @Override
            public void processError(String msg) {

            }
        });
        decoder.addPCMProcessor(new PCMProcessor() {
            @Override
            public void processStreamInfo(StreamInfo streamInfo) {
                sampleRate = streamInfo.getSampleRate();
                bps = streamInfo.getBitsPerSample();
            }

            @Override
            public void processPCM(ByteData pcm) {
                out.write(pcm.getData(), 0, pcm.getLen());
            }
        });
        decoder.readMetadata();
        decoder.decode();
    }


    @Override
    public void WriteFile(File outputFile, /*int startFrame, int numFrames*/ int startSec, int endSec) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
        bais.skip(startSec * bps);

    }

    public CheapFlac() {
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getNumFrames() {
        return numFrames;
    }
}
