package sharknoon.casey.updater;/*
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


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.aeonbits.owner.ConfigFactory;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import sharknoon.casey.updater.cli.CLIArgs;
import sharknoon.casey.updater.cli.CLIParser;
import sharknoon.casey.updater.ui.ProgressStage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {
    
    private static final Updater UPDATER = ConfigFactory.create(Updater.class);
    private static String newestVersion = null;
    
    public static void main(String[] args) {
        int status = go(args);
        System.exit(status);
    }
    
    public static int go(String[] args) {
        System.out.print("Parsing Command Line...");
        Optional<CLIArgs> cliArgs = CLIParser.parseCommandLine(args);
        if (!cliArgs.isPresent()) {
            return 1;
        }
        System.out.println("done");
        if (cliArgs.get().getOldVersion() != null) {
            System.out.print("Checking for new version...");
            int status = checkForNewerVersion(cliArgs.get().getOldVersion()) ? 100 : 200;
            System.out.println("done");
            return status;
        }
        if (cliArgs.get().getCaseyJarPath() != null) {
            System.out.print("Installing new version...");
            int status = installNewerVersion(cliArgs.get().getCaseyJarPath()) ? 100 : 200;
            System.out.println("done");
            return status;
        }
        return 2;
    }
    
    public static boolean installNewerVersion(String caseyJar) {
        DoubleProperty progressProperty = new SimpleDoubleProperty();
        StringProperty descriptionProperty = new SimpleStringProperty();
        ProgressStage.show(progressProperty, descriptionProperty);
        try {
            Path caseyJarPath = Paths.get(caseyJar);
            boolean success = downloadNewestVersion(caseyJarPath, progressProperty, descriptionProperty);
            if (!success) {
                System.err.println("Could not download newest Casey .jar");
                return false;
            }
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", caseyJarPath.toAbsolutePath().toString(), "-u");
            pb.directory(caseyJarPath.getParent().toFile());
            System.out.println("Executing " + String.join(" ", pb.command()));
            pb.start();
            return true;
        } catch (Exception e) {
            System.err.println("Could not install newest Casey .jar: " + e);
        }
        return false;
    }
    
    public static boolean downloadNewestVersion(Path jarToReplace, DoubleProperty progressProperty, StringProperty descriptionProperty) {
        try {
            descriptionProperty.set("Getting update-URL from .properties file");
            String updateurltag = UPDATER.updateurltag();
            updateurltag = updateurltag.replace("[TAG]", getNewestVersion(descriptionProperty).orElse("0.1"));
            URL updateurltagUrl = new URL(updateurltag);
            descriptionProperty.set("Deleting old Casey .jar");
            Files.deleteIfExists(jarToReplace);
            descriptionProperty.set("Opening Channels to the URL");
            ReadableByteChannel rbc = Channels.newChannel(updateurltagUrl.openStream());
            ProgressTrackableReadableByteChannel ptrbc = new ProgressTrackableReadableByteChannel(rbc, contentLength(updateurltagUrl, descriptionProperty), progressProperty);
            FileOutputStream fos = new FileOutputStream(jarToReplace.toFile());
            descriptionProperty.set("Downloading update file...");
            fos.getChannel().transferFrom(ptrbc, 0, Long.MAX_VALUE);
            return true;
        } catch (Exception e) {
            System.err.println("Could not download newest Casey.jar: " + e);
        }
        return false;
    }
    
    private static long contentLength(URL url, StringProperty descriptionProperty) {
        try {
            descriptionProperty.set("Getting size of the update file");
            return url.openConnection().getContentLengthLong();
        } catch (Exception e) {
            System.err.println("Couldn't get the size of the Casey.jar File: " + e);
        }
        return -1;
    }
    
    public static boolean checkForNewerVersion(String currentVersionString) {
        if (currentVersionString == null || currentVersionString.isEmpty()) {
            System.err.println("Wrong current version string: " + currentVersionString);
            return false;
        }
        Optional<String> newestVersionOptional = getNewestVersion(new SimpleStringProperty());
        if (!newestVersionOptional.isPresent()) {
            System.err.println("Could not get newest version number");
            return false;
        }
        String newestVersionString = newestVersionOptional.get();
        
        DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(currentVersionString);
        DefaultArtifactVersion newestVersion = new DefaultArtifactVersion(newestVersionString);
        
        return newestVersion.compareTo(currentVersion) > 0;
    }
    
    public static Optional<String> getNewestVersion(StringProperty descriptionProperty) {
        if (newestVersion != null) {
            return Optional.of(newestVersion);
        }
        try {
            descriptionProperty.set("Getting newest version number");
            URL updateurllatest = UPDATER.updateurllatest();
            HttpURLConnection con = (HttpURLConnection) (updateurllatest.openConnection());
            con.setInstanceFollowRedirects(false);
            con.connect();
            String location = con.getHeaderField("Location");
            newestVersion = location.substring(location.lastIndexOf("/") + 1);
            return Optional.of(newestVersion);
        } catch (IOException e) {
            System.err.println("Could not retrieve newest version number: " + e);
        }
        return Optional.empty();
    }
    
    private static final class ProgressTrackableReadableByteChannel implements ReadableByteChannel {
        
        private final ReadableByteChannel originalRBC;
        private final DoubleProperty progressProperty;
        private final long expectedSize;
        private long sizeSoFar = 0;
        
        public ProgressTrackableReadableByteChannel(ReadableByteChannel originalRBC, long expectedSize, DoubleProperty progressProperty) {
            this.originalRBC = originalRBC;
            this.progressProperty = progressProperty;
            this.expectedSize = expectedSize;
        }
        
        @Override
        public int read(ByteBuffer dst) throws IOException {
            int n;
            double progress;
            
            if ((n = originalRBC.read(dst)) > 0) {
                sizeSoFar += n;
                progress = expectedSize > 0 ? (double) sizeSoFar / (double) expectedSize : -1.0;
                progressProperty.set(progress);
                //System.out.println(progress + "%");
            }
            
            return n;
        }
        
        @Override
        public boolean isOpen() {
            return originalRBC.isOpen();
        }
        
        @Override
        public void close() throws IOException {
            originalRBC.close();
        }
    }
    
}
