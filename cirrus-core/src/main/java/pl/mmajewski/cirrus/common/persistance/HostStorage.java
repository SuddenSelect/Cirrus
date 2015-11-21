package pl.mmajewski.cirrus.common.persistance;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.Host;

import java.util.Set;

/**
 * Interface for general utility class caring for the host information preservance and accessibility.
 * It is expected for implementer to automatically perform backups in the runtime and retrieval upon startup.
 * Created by Maciej Majewski on 09/11/14.
 */
public interface HostStorage {

    /**
     * Updating data about all given hosts and adding if not present.
     * Strongly suggested to trigger backup upon execution.
     * @param hosts set of hosts to be updated
     */
    public void updateHosts(Set<Host> hosts);

    /**
     * Deleting unreachable hosts.
     * Strongly suggested to trigger backup upon execution.
     * @param hosts set of hosts to be deleted
     */
    public void deleteHosts(Set<Host> hosts);

    /**
     * Retrives Host data of the local machine.
     * @return local host's data
     */
    public Host fetchLocalHost();

    /**
     * Retrieves host by it's CirrusID.
     * @param cirrusId
     * @return Host with given CirrusID if exists, otherwise null
     */
    public Host fetchHost(String cirrusId);

    /**
     * Retrieves hosts that can provide any piece of given Content.
     * @param contentMetadata
     * @return set of hosts providing ContentPieces tied to given ContentMetadata
     * ordered by latency
     */
    public Iterable<Host> fetchSharers(ContentMetadata contentMetadata);

    /**
     * Retrieves all hosts in newly created collection
     * @return set of all hosts
     */
    public Set<Host> fetchAllHosts();
}
