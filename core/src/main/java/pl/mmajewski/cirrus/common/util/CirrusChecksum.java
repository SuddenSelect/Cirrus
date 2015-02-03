package pl.mmajewski.cirrus.common.util;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;

import java.math.BigInteger;
import java.util.zip.CRC32;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class CirrusChecksum extends CRC32 {

    /**
     * Presents CRC32 checksum in Cirrus-acceptable format
     * @return
     */
    public String getCirrusChecksum(){
        BigInteger ts = BigInteger.valueOf(System.currentTimeMillis());
        return ts.toString(32)+":"+getStringChecksum();
//        return getStringChecksum();
    }

    /**
     * Presents CRC32 checksum as pretty String
     * @return
     */
    private String getStringChecksum(){
        return BigInteger.valueOf(this.getValue()).toString(32);
    }

    /**
     * Validates data that were feed to the instance
     * @param cirrusChecksum String usually returned from getCirrusChecksum
     * @return
     */
    public boolean validate(String cirrusChecksum){
        String checksum = cirrusChecksum.split(":")[1];
        return this.getStringChecksum().equals(checksum);
//        return this.getStringChecksum().equals(cirrusChecksum);
    }
}
