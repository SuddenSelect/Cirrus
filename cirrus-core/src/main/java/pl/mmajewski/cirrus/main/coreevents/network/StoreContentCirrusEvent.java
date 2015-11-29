package pl.mmajewski.cirrus.main.coreevents.network;

import pl.mmajewski.cirrus.common.event.CirrusEvent;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.network.server.ServerCirrusEventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Maciej Majewski on 29/11/15.
 */
public class StoreContentCirrusEvent extends CirrusEvent<ServerCirrusEventHandler> {

    private Map<ContentMetadata, Set<ContentPiece>> contentMap = new HashMap<>();

    public void setContentMap(Map<ContentMetadata, Set<ContentPiece>> contentMap) {
        this.contentMap = contentMap;
    }

    public void addContent(ContentMetadata contentMetadata, Set<ContentPiece> contentPieces) {
        if(contentMap.containsKey(contentMetadata)) {
            contentMap.get(contentMetadata).addAll(contentPieces);
        }else{
            contentMap.put(contentMetadata, contentPieces);
        }
    }

    public Map<ContentMetadata, Set<ContentPiece>> getContentMap() {
        return contentMap;
    }

    @Override
    public void event(ServerCirrusEventHandler handler) {



    }
}
