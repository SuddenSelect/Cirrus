package pl.mmajewski.cirrus.content;

import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public interface ContentAdapter {

    /**
     * Returns name of the content being adapted
     * @return filename, url, etc.
     */
    public String getContentSource();

    /**
     * Returns description of the content type handled by given implementation
     * @return file, web resource, etc.
     */
    public String getContentTypeDescription();

    /**
     * Checks if the given ContentSource is supported by given implementation.
     * Does not attempt to access the resource, only verifies if the given
     * argument is in acceptable format.
     * @param contentSource filename, url, etc.
     * @return true when supported, false otherwise
     */
    public boolean isSupported(String contentSource);


    /**
     * Transforms given source to Pieces and Metadata form.
     * Upon the completion, new AppEvent is generated for adding new content to local
     * storages and broadcast new Availability.
     * @param contentSource filename, url, etc.
     * @throws ContentAdapterCirrusException Thrown when source transformation have failed
     */
    public void adapt(String contentSource) throws ContentAdapterCirrusException;

    /**
     * Returns progress of adaptation. Max value can be retrieved using method getMaxProgress()
     * @return current progess
     */
    default public int getProgress(){return 0;}

    /**
     * Returns max value progress will achieve
     * @return max progress value
     */
    default public int getMaxProgress(){return 100;}

}
