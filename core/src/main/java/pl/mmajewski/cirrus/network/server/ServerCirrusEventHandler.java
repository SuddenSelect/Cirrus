package pl.mmajewski.cirrus.network.server;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;

/**
 * Responsible for rerouting internal events back to the PropagationStrategy,
 * and propagating rest to specialized handlers.
 * Passive part of the server.
 * Intercepted Events are expected to behave as visitors.
 * All methods called should be synchronised
 * Created by Maciej Majewski on 30/10/14.
 */
public interface ServerCirrusEventHandler extends CirrusEventHandler{

    /**
     * Server-listen starting method.
     * Expected to start separate thread for this task.
     * Suggested implementation: listening threads adds
     * events to the BlockingQueue for processing in main thread.
     *
     * Processing should be basically calling CirrusEvent's 'event' method.
     */
    public void listen();

    /**
     * Sets host storage for updating hosts data.
     * @param hostStorage
     */
    public void setHostStorage(HostStorage hostStorage);

    /**
     * HostStorage getter.
     * @return host storage if has been set, null otherwise
     */
    public HostStorage getHostStorage();


    /**
     * Sets availability storage for updating ContentAvailability data.
     * @param contentAvailability
     */
    public void setAvailabilityStorage(AvailabilityStorage contentAvailability);

    /**
     * ContentAvailabilityStorage getter.
     * @return content storage if has been set, null otherwise.
     */
    public AvailabilityStorage getAvailabilityStorage();
}
