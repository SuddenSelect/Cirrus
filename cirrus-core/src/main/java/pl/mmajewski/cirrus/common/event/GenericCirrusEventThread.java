package pl.mmajewski.cirrus.common.event;

/**
 * Created by Maciej Majewski on 2015-02-07.
 */
public abstract class GenericCirrusEventThread implements CirrusEventThread {
    private Integer progress;
    private String message;
    private boolean run = true;

    @Override
    public String getStatusMessage() {
        return message;
    }

    @Override
    public Integer getProgress() {
        return progress;
    }

    protected void setMessage(String message){
        this.message = message;
    }

    protected void setProgress(Integer progress) {
        this.progress = progress;
    }

    protected boolean running(){
        return run;
    }

    @Override
    public void terminate() {
        run = false;
    }
}
