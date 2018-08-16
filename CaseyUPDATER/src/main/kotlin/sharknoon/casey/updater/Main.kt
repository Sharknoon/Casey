package sharknoon.casey.updater

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


import javafx.beans.property.*
import org.aeonbits.owner.ConfigFactory
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import sharknoon.casey.updater.cli.parseCommandLine
import sharknoon.casey.updater.ui.show
import java.io.*
import java.net.*
import java.nio.ByteBuffer
import java.nio.channels.*
import java.nio.file.*
import java.util.*
import javax.swing.JOptionPane

private val UPDATER = ConfigFactory.create(Updater::class.java)
private var newestVersion: String? = null

private val PROGRESS_PROPERTY = SimpleDoubleProperty()
private val DESCRIPTION_PROPERTY = SimpleStringProperty()

fun main(args: Array<String>) {
    Thread.setDefaultUncaughtExceptionHandler { _, e -> showErrorDialog(e) }
    try {
        System.exit(go(args))
    } catch (e: Exception) {
        showErrorDialog(e)
    }

    System.exit(-1)
}

private fun showErrorDialog(error: Throwable) {
    val sw = StringWriter()
    error.printStackTrace(PrintWriter(sw))
    val exceptionText = sw.toString()

    JOptionPane.showMessageDialog(null, "Could not update Casey\n$exceptionText", "Error during updating", JOptionPane.ERROR_MESSAGE)
}

@Throws(Exception::class)
fun go(args: Array<String>): Int {
    print("Parsing Command Line...")
    val (oldVersion, caseyJarPath) = parseCommandLine(args) ?: return 1
    println("done")
    if (oldVersion != null) {
        print("Checking for new version...")
        val status = if (checkForNewerVersion(oldVersion)) 100 else 200
        println("done")
        return status
    }
    if (caseyJarPath != null) {
        print("Installing new version...")
        val status = if (installNewerVersion(caseyJarPath)) 100 else 200
        println("done")
        return status
    }
    return 2
}

@Throws(Exception::class)
fun installNewerVersion(caseyJar: String?): Boolean {
    show(PROGRESS_PROPERTY, DESCRIPTION_PROPERTY, Runnable { System.exit(-1) })
    try {
        val caseyJarPath = Paths.get(caseyJar)
        val success = downloadNewestVersion(caseyJarPath)
        if (!success) {
            System.err.println("Could not download newest Casey .jar")
            return false
        }
        val pb = ProcessBuilder("java", "-jar", caseyJarPath.toAbsolutePath().toString(), "-u")
        pb.directory(caseyJarPath.parent.toFile())
        println("Executing " + pb.command().joinToString(" "))
        pb.start()
        return true
    } catch (e: Exception) {
        throw Exception("Could not install newest Casey .jar", e)
    }

}

@Throws(Exception::class)
fun downloadNewestVersion(jarToReplace: Path): Boolean {
    try {
        DESCRIPTION_PROPERTY.set("Getting update-URL from .properties file")
        var updateurltag = UPDATER.updateurltag()
        updateurltag = updateurltag.replace("[TAG]", getNewestVersion(DESCRIPTION_PROPERTY).orElse("0.1"))
        val updateurltagUrl = URL(updateurltag)
        DESCRIPTION_PROPERTY.set("Deleting old Casey .jar")
        Files.deleteIfExists(jarToReplace)
        DESCRIPTION_PROPERTY.set("Opening Channels to the URL ($updateurltag)")
        val rbc = Channels.newChannel(updateurltagUrl.openStream())
        val ptrbc = ProgressTrackableReadableByteChannel(rbc, contentLength(updateurltagUrl), PROGRESS_PROPERTY)
        val fos = FileOutputStream(jarToReplace.toFile())
        DESCRIPTION_PROPERTY.set("Downloading update file (" + updateurltagUrl.file.substring(updateurltagUrl.file.lastIndexOf("/") + 1) + ")")
        fos.channel.transferFrom(ptrbc, 0, java.lang.Long.MAX_VALUE)
        return true
    } catch (e: Exception) {
        throw Exception("Could not download newest Casey.jar", e)
    }

}

@Throws(Exception::class)
private fun contentLength(url: URL): Long {
    try {
        DESCRIPTION_PROPERTY.set("Getting size of the update file")
        return url.openConnection().contentLengthLong
    } catch (e: Exception) {
        throw Exception("Couldn't get the size of the Casey.jar File", e)
    }

}

@Throws(Exception::class)
fun checkForNewerVersion(currentVersionString: String?): Boolean {
    try {
        if (currentVersionString == null || currentVersionString.isEmpty()) {
            System.err.println("Wrong current version string: " + currentVersionString!!)
            return false
        }
        val newestVersionOptional = getNewestVersion(SimpleStringProperty())
        if (!newestVersionOptional.isPresent) {
            System.err.println("Could not get newest version number")
            return false
        }
        val newestVersionString = newestVersionOptional.get()

        val currentVersion = DefaultArtifactVersion(currentVersionString)
        val newestVersion = DefaultArtifactVersion(newestVersionString)

        return newestVersion.compareTo(currentVersion) > 0
    } catch (e: Exception) {
        throw Exception("Could not check for newer version", e)
    }

}

@Throws(Exception::class)
fun getNewestVersion(descriptionProperty: StringProperty): Optional<String> {
    if (newestVersion != null) {
        return Optional.of(newestVersion!!)
    }
    try {
        descriptionProperty.set("Getting newest version number")
        val updateurllatest = UPDATER.updateurllatest()
        val con = updateurllatest.openConnection() as HttpURLConnection
        con.instanceFollowRedirects = false
        con.connect()
        val location = con.getHeaderField("Location")
        newestVersion = location.substring(location.lastIndexOf("/") + 1)
        return Optional.of(newestVersion!!)
    } catch (e: Exception) {
        throw Exception("Could not retrieve newest version number", e)
    }

}

private class ProgressTrackableReadableByteChannel(private val originalRBC: ReadableByteChannel, private val expectedSize: Long, private val progressProperty: DoubleProperty) : ReadableByteChannel {
    private var sizeSoFar: Long = 0

    @Throws(IOException::class)
    override fun read(dst: ByteBuffer): Int {
        val n = originalRBC.read(dst)
        val progress: Double

        if (n > 0) {
            sizeSoFar += n.toLong()
            progress = if (expectedSize > 0) sizeSoFar.toDouble() / expectedSize.toDouble() else -1.0
            progressProperty.set(progress)
            //System.out.println(progress + "%");
        }

        return n
    }

    override fun isOpen(): Boolean {
        return originalRBC.isOpen
    }

    @Throws(IOException::class)
    override fun close() {
        originalRBC.close()
    }
}
