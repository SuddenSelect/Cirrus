package pl.mmajewski.cirrus.content;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public interface ContentAccessor {

    /**
     * Sets the metadata of content for streaming and/or saving as simple file
     * @param metadata metadata of the content
     */
    public void setContentMetadata(ContentMetadata metadata);

    /**
     * Assembles content and saves it as file
     * @param filename file to which content will be saved
     * @throws FileNotFoundException thrown when problem with saving occurred
     */
    public void saveAsFile(String filename) throws FileNotFoundException, EventHandlerClosingCirrusException;

    /**
     * Returns output stream to which data will be written asynchronously
     * piece by piece in proper order (by different thread).
     * @return stream to which pieces will be written
     * @TODO may not be so simple or even possible
     */
    public ByteArrayOutputStream stream();

}
