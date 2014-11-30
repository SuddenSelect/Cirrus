package pl.mmajewski.cirrus.common.model;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public class CirrusIdGenerator {
    private static SecureRandom secureRandom = new SecureRandom();
    private static int CONTENT_ID_LENGTH = 10;//arbitrary number for small setups
    private static int HOST_ID_LENGTH = 5;

    public static String generateContentId(String owner){
        BigInteger id = new BigInteger(CONTENT_ID_LENGTH *5, secureRandom);
        BigInteger ts = BigInteger.valueOf(System.currentTimeMillis());
        return owner+"/"+ts.toString(32)+":"+id.toString(32);
    }

    public static String generateHostId(){
        BigInteger id = new BigInteger(HOST_ID_LENGTH *5, secureRandom);
        BigInteger ts = BigInteger.valueOf(System.currentTimeMillis());
        return ts.toString(32)+":"+id.toString(32);
    }
}
