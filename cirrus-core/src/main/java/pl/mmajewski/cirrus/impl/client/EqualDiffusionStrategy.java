package pl.mmajewski.cirrus.impl.client;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.main.coreevents.network.StoreContentCirrusEvent;
import pl.mmajewski.cirrus.network.client.CirrusDiffusionStrategy;

import java.util.*;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public class EqualDiffusionStrategy implements CirrusDiffusionStrategy<StoreContentCirrusEvent> {
    private class RoundRobin extends LinkedList<Host>{
        public RoundRobin(Iterable<Host> hosts) {
            for(Host host : hosts){
                this.add(host);
            }
        }

        @Override
        public Host get(int index) {
            return super.get(index%this.size());
        }
    }

    private int redundancy = 1;

    public void setRedundancy(int redundancy){
        if(redundancy < 1){
            throw new RuntimeException("Incorrect redundancy value");
        }
        this.redundancy = redundancy;
    }

    @Override
    public Map<Host, StoreContentCirrusEvent> getTargets(ContentStorage contentStorage, HostStorage hostStorage) {
        Map<Host, StoreContentCirrusEvent> result = new HashMap<>();
        RoundRobin allHosts = new RoundRobin(hostStorage.fetchAllHostsAscendingLatency());

        for(ContentMetadata metadata : contentStorage.getAllContentMetadata()) {
            Integer batchSize;
            if(metadata.getPiecesAmount() > hostStorage.size()){
                batchSize = (int)Math.ceil(metadata.getPiecesAmount() / ((double)hostStorage.size()));
            }else{
                batchSize = 1;
            }
            Integer batchNum = 0;
            ArrayList<ContentPiece> pieces = contentStorage.getAvailablePieces(metadata);
            HashSet<ContentPiece> batch;
            do {
                batch = new HashSet<>(pieces.subList(batchNum * batchSize, (batchNum+1) * batchSize));
                if(batch.size()==0){
                    break;
                }
                batchNum+=1;
                for (int j = 0; j < redundancy; j++) {
                    Host host = allHosts.get(batchNum+j);
                    if(!result.containsKey(host)){
                        result.put(host, new StoreContentCirrusEvent());
                    }
                    result.get(host).addContent(metadata, batch);
                }
            }while(batchSize.equals(batch.size()));

        }
        return result;
    }
}
