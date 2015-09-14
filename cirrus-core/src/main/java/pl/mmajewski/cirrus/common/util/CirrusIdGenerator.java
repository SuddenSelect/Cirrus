package pl.mmajewski.cirrus.common.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Maciej Majewski on 30/11/14.
 */
public class CirrusIdGenerator {
    private static SecureRandom secureRandom = new SecureRandom();
    private static int CONTENT_ID_LENGTH = 10;//arbitrary number for small setups
    private static int HOST_ID_LENGTH = 5;
    private static int EVENT_ID_LENGTH = 5;

    private static String rand(int len){
        return new BigInteger(len *5, secureRandom).toString(32);
    }

    private static String timestamp(){
        return BigInteger.valueOf(System.currentTimeMillis()).toString(32);
    }

    public static String generateContentId(String owner){
        return owner+"-"+timestamp()+"-"+rand(CONTENT_ID_LENGTH);
    }

    public static String generateHostId(){
        return timestamp()+"-"+rand(HOST_ID_LENGTH);
    }

    public static String generateEventId(){
        return "EVT:"+timestamp()+rand(EVENT_ID_LENGTH);
    }

    public static String generateAppEventId(){
        return "APP:"+timestamp()+rand(EVENT_ID_LENGTH);
    }
}
