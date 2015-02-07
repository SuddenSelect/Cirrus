package pl.mmajewski.cirrus.common.util;

import java.math.BigInteger;
import java.util.zip.CRC32;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusChecksum extends CRC32 {

    /**
     * Presents CRC32 checksum in Cirrus-acceptable format
     * @return timestamp and crc32 checksum as String
     */
    public String getCirrusChecksum(){
        BigInteger ts = BigInteger.valueOf(System.currentTimeMillis());
        return ts.toString(32)+":"+getStringChecksum();
//        return getStringChecksum();
    }

    /**
     * Presents CRC32 checksum as pretty String
     * @return crc32 checksum as String
     */
    private String getStringChecksum(){
        return BigInteger.valueOf(this.getValue()).toString(32);
    }

    /**
     * Validates data that were feed to the instance
     * @param cirrusChecksum String usually returned from getCirrusChecksum
     * @return true when valid, false otherwise
     */
    public boolean validate(String cirrusChecksum){
        String checksum = cirrusChecksum.split(":")[1];
        return this.getStringChecksum().equals(checksum);
//        return this.getStringChecksum().equals(cirrusChecksum);
    }
}
