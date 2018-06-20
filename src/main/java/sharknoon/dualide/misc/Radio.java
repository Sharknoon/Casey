/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sharknoon.dualide.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
import sharknoon.dualide.ui.MainApplication;
import sharknoon.dualide.misc.Exitable;
import sharknoon.dualide.utils.settings.Logger;

/**
 * This class can decode an AAC stream from an Shoutcast/Icecast server. This is
 * useful for webradios like DUFM
 *
 */
public class Radio implements Exitable {

    private final static ExecutorService SERVICE = Executors.newCachedThreadPool();

    /**
     * Starts a new radio, which starts playing rightaway
     *
     * @param station The station to be played
     * @return A instance of {@link Radio} to control the volume, etc
     */
    public static Radio start(RadioStations station) {
        return Radio.start(station.getURL());
    }

    /**
     * Starts a new radio, which starts playing rightaway
     *
     * @param url The url of a shoutcast or icecast server, from which the radio
     * should be played
     * @return A instance of {@link Radio} to control the volume, etc
     */
    public static Radio start(String url) {
        Radio r = new Radio(url);
        r.start();
        return r;
    }

    final String url;
    SourceDataLine line = null;
    //keeps the radio running while true
    boolean radioRunning = true;

    private Radio(String url) {
        this.url = url;
        MainApplication.registerExitable(this);
    }

    /**
     * Stops the radio immediatly
     */
    public void stop() {
        radioRunning = false;
    }

    /**
     * Controls the volume of the radio
     *
     * @param vol Range from 0.0 to 1.0
     */
    public void setVolume(float vol) {
        if (vol > 1 || vol < 0) {
            return;
        }
        if (line != null) {
            FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(((control.getMaximum() - control.getMinimum()) * vol) + control.getMinimum());
        }
    }

    /**
     * Starts this radio immediatly
     */
    public void start() {
        radioRunning = true;
        SERVICE.submit(() -> {
            line = null;
            final SampleBuffer buf = new SampleBuffer();

            byte[] b;
            try {
                //read response (skip header)
                final InputStream in = new URL(url).openStream();
                final BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String x;
                do {
                    x = br.readLine();
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
            } catch (LineUnavailableException ex) {
                Logger.error("Cant iconToNodeProperty sourcedataline to play back the radio", ex);
            } catch (NullPointerException ex) {
                Logger.error("Nullpointer during radio playback", ex);
            } catch (SecurityException ex) {
                Logger.error("Securityerror during radio playback", ex);
            } catch (IllegalArgumentException ex) {
                Logger.error("Illagal Argument during radio playback", ex);
            } catch (ArrayIndexOutOfBoundsException ex) {
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

    /**
     * Shuts down the radio threadpool (radio needs extra threads because it is
     * always playing at the background)
     */
    @Override
    public void exit() {
        if (!SERVICE.isShutdown()) {
            SERVICE.shutdown();
        }
    }
}
