package net.sourceforge.jaad;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;
import sharknoon.dualide.utils.settings.Logger;

/**
 * Command line example, that can decode an AAC stream from an Shoutcast/Icecast
 * server.
 *
 * @author in-somnia
 */
public class Radio {

    private final static ExecutorService SERVICE = Executors.newCachedThreadPool();

    public static Radio start(String url) {
        Radio r = new Radio(url);
        r.start();
        return r;
    }

    private Radio(String url) {
        this.url = url;
    }

    public void stop() {
        radioRunning = false;
    }
    
    public void setVolume(double vol){
        if (line != null) {
            FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            
        }
    }
    
    boolean radioRunning = true;
    final String url;
    SourceDataLine line = null;

    public void start() {
        radioRunning = true;
        SERVICE.submit(() -> {
            line = null;
            final SampleBuffer buf = new SampleBuffer();

            byte[] b;
            try {
                //read response (skip header)
                final DataInputStream in = new DataInputStream(new URL(url).openStream());
                String x;
                do {
                    x = in.readLine();
                } while (x != null && !x.trim().equals(""));

                final ADTSDemultiplexer adts = new ADTSDemultiplexer(in);
                AudioFormat aufmt = new AudioFormat(adts.getSampleFrequency(), 16, adts.getChannelCount(), true, true);
                final Decoder dec = new Decoder(adts.getDecoderSpecificInfo());

                while (radioRunning) {
                    b = adts.readNextFrame();
                    dec.decodeFrame(b, buf);

                    if (line != null && formatChanged(line.getFormat(), buf)) {
                        //format has changed (e.g. SBR has started)
                        line.stop();
                        line.close();
                        line = null;
                        aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
                    }
                    if (line == null) {
                        line = AudioSystem.getSourceDataLine(aufmt);
                        line.open();
                        line.start();
                    }
                    b = buf.getData();
                    line.write(b, 0, b.length);
                }
            } catch (MalformedURLException ex) {
                Logger.error("URL of the Radio Stream is invalid: " + url, ex);
            } catch (IOException ex) {
                Logger.error("Could not stream radio from network", ex);
            } catch (Exception ex) {
                Logger.error("Error during radio playback", ex);
            } finally {
                if (line != null) {
                    line.stop();
                    line.close();
                }
            }
        });
    }

    private static boolean formatChanged(AudioFormat af, SampleBuffer buf) {
        return af.getSampleRate() != buf.getSampleRate()
                || af.getChannels() != buf.getChannels()
                || af.getSampleSizeInBits() != buf.getBitsPerSample()
                || af.isBigEndian() != buf.isBigEndian();
    }
}
