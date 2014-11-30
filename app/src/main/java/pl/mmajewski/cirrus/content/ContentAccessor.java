package pl.mmajewski.cirrus.content;

import java.io.ByteArrayOutputStream;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public interface ContentAccessor {

    /**
     * Assembles content and saves it as file
     * @param cirrusId id of the content to be saved
     * @param filename file to which content will be saved
     */
    public void dumpToFile(String cirrusId, String filename);

    /**
     * Returns output stream to which data will be written asynchronously
     * piece by piece in proper order (by different thread).
     * @param cirrusId content to be put in the stream
     * @return stream to which pieces will be written
     * @TODO may not be so simple or even possible
     */
    public ByteArrayOutputStream stream(String cirrusId);

}
