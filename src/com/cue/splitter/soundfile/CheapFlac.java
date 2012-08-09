package com.cue.splitter.soundfile;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.FrameListener;
import org.kc7bfi.jflac.PCMProcessor;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.io.RandomFileInputStream;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.SeekPoint;
import org.kc7bfi.jflac.metadata.SeekTable;
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
    private long totalSamples;
    private int blockSize;

    private FLACDecoder decoder = null;
    private SeekTable seekTable = null;


    @Override
    public void ReadFile(File inputFile) throws IOException {
        RandomFileInputStream is = new RandomFileInputStream(inputFile);
        decoder = new FLACDecoder(is);
        decoder.addFrameListener(new FrameListener() {
            @Override
            public void processMetadata(Metadata metadata) {
                if (metadata instanceof SeekTable) seekTable = (SeekTable)metadata;
            }

            @Override
            public void processFrame(Frame frame) {
                System.out.println("frame.header.frameNumber = " + frame.header.frameNumber);
                System.out.println("frame.header.sampleNumber = " + frame.header.sampleNumber);
                System.out.println("frame.header.bitsPerSample = " + frame.header.bitsPerSample);
                System.out.println("frame.header.blockSize = " + frame.header.blockSize);
                System.out.println("frame.header.channels = " + frame.header.channels);
                System.out.println("frame.header.sampleRate = " + frame.header.sampleRate);
                System.out.println("===================================================================");
                numFrames++;
            }

            @Override
            public void processError(String msg) {
                System.out.println("FLAC Error: " + msg);
            }
        });
       // decoder.readMetadata();
//        StreamInfo info = decoder.getStreamInfo();
//        if (info != null) {
//            System.out.println("info = " + info);
//            //info = StreamInfo: BlockSize=4608-4608 FrameSize14-16307 SampleRate=44100 Channels=2 BPS=16 TotalSamples=152365500
//            sampleRate = info.getSampleRate();
//            bps = info.getBitsPerSample();
//            totalSamples = info.getTotalSamples();
//            blockSize = info.getMaxBlockSize();
//        }
    }






    @Override
    public void WriteFile(File outputFile, /*int startFrame, int numFrames*/ int startSec, int endSec) {
        BufferedOutputStream buffOut = null;
        try {

            buffOut = new BufferedOutputStream(new FileOutputStream(outputFile));
            decoder.addPCMProcessor(new OutputStreamPCMProcessor(buffOut));


            long startSample = 10 * 75 * 588;
            long endSample = 100 * 75 * 588;

            SeekPoint seekPointFrom = new SeekPoint(startSample, decoder.getTotalBytesRead(), blockSize);
            SeekPoint seekPointTo = new SeekPoint(endSample, 0, blockSize);

            //decoder.decode(seekPointFrom, seekPointTo);
            decoder.decode();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {

            if (buffOut != null) try {buffOut.flush(); buffOut.close();} catch (IOException e) {}
        }


    }


    public CheapFlac() {
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getNumFrames() {
        return numFrames;
    }

    public int getSamplesPerFrame() {
        return 588;
    }

    public class OutputStreamPCMProcessor implements PCMProcessor {

        OutputStream outputStream;
         int offcet = 0;
        public OutputStreamPCMProcessor(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void processStreamInfo(StreamInfo streamInfo) {
        }

        @Override
        public void processPCM(ByteData pcm) {
            try {
                outputStream.write(pcm.getData(), 0, pcm.getLen());
                offcet +=pcm.getLen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public void setOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

    }
}
