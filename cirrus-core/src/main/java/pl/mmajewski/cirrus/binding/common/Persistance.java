package pl.mmajewski.cirrus.binding.common;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.impl.persistance.MemoryAvailabilityStorage;
import pl.mmajewski.cirrus.impl.persistance.MemoryContentStorage;
import pl.mmajewski.cirrus.impl.persistance.MemoryHostStorage;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public class Persistance {
    public static AvailabilityStorage newAvailabilityStorage() {
        return new MemoryAvailabilityStorage();//binding stub
    }

    public static ContentStorage newContentStorage() {
        return new MemoryContentStorage();//binding stub
    }

    public static HostStorage newHostStorage(Host localhost) {
        return new MemoryHostStorage(localhost);//binding stub
    }
}
