package pl.mmajewski.cirrus.binding.network;

import pl.mmajewski.cirrus.network.client.CirrusEventPropagationStrategy;
import pl.mmajewski.cirrus.network.client.ClientContentFetcher;
import pl.mmajewski.cirrus.network.client.ClientDataConnection;
import pl.mmajewski.cirrus.network.client.ClientEventConnection;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public class Client extends Network {
public static ClientContentFetcher newContentFetcher(){
        return null;//binding stub
    }

    public static ClientDataConnection newDataConnection(){
        return null;//binding stub
    }

    public static ClientEventConnection newEventConnection(){
        return null;//binding stub
    }

    public static CirrusEventPropagationStrategy newEventPropagationStrategy(){
        return null;//binding stub
    }
}
