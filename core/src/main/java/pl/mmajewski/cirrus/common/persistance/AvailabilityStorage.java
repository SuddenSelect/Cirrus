package pl.mmajewski.cirrus.common.persistance;

import pl.mmajewski.cirrus.common.model.ContentAvailability;

import java.util.Set;

/**
 * Interface for general utility class holding information about ContentPieces placement among the network.
 * It is NOT expected to be persistent.
 * Created by Maciej Majewski on 09/11/14.
 */
public interface AvailabilityStorage {

    /**
     * Stores availabilities in the runtime.
     * @param availabilities
     */
    public void addAvailability(Set<ContentAvailability> availabilities);

    /**
     * Retrieves ContentAvailability by contentID.
     * @param contentId
     * @return ContentAvailability
     */
    public ContentAvailability getContentAvailability(String contentId);

    /**
     * Retrieves ContentAvailability of the Host with given cirrusID.
     * @param cirrusId
     * @return ContentAvailability
     */
    public ContentAvailability getHostContentAvailability(String cirrusId);

    /**
     * Retrieves ContentAvailability of the Content with given contentID shared by Host with given cirrusID.
     * @param cirrusId
     * @return Set of SequenceNumbers available from Host of Content
     */
    public Set<Integer> getHostContentAvailabilityPieces(String cirrusId, String contentId);

}