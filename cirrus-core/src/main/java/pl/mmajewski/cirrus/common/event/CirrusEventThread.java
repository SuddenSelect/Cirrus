package pl.mmajewski.cirrus.common.event;

/**
 * Created by Maciej Majewski on 2015-02-07.
 *
 * Interface describing functionality of any Thread created by CirrusEvent
 */
public interface CirrusEventThread extends Runnable {

    /**
     * Any message indicating status of processing from thread
     * @return message about processing status
     */
    public String getStatusMessage();

    /**
     * Returns percentage of the processing progression
     * @return number between 0 and 100 or null if unknown
     */
    public Integer getProgress();

    /**
     * Terminates Thread
     */
    public void terminate();
}
