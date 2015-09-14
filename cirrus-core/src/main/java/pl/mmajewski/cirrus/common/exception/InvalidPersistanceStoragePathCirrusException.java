package pl.mmajewski.cirrus.common.exception;

import java.io.File;

/**
 * Created by Maciej Majewski on 14/09/15.
 */
public class InvalidPersistanceStoragePathCirrusException extends CirrusException {
    private File file;

    public InvalidPersistanceStoragePathCirrusException(File file){
        this.file = file;
    }

    @Override
    public String getMessage() {
        return "Invalid PersistanceStorage path: "+file.getPath();
    }
}
