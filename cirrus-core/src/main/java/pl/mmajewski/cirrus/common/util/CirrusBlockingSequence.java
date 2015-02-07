package pl.mmajewski.cirrus.common.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Maciej Majewski on 2015-02-07.
 */
public class CirrusBlockingSequence<E extends Object> implements Iterable<E> {
    private final HashMap<Integer,E> sequence = new HashMap<>();
    private Integer currentElement = 0;
    private Integer totalElements;
    private final Object lock = new Object();

    public CirrusBlockingSequence() {
    }

    public CirrusBlockingSequence(Integer totalElements) {
        reset(totalElements);
    }

    public void push(Integer position, E element){
        synchronized (lock) {
            sequence.put(position, element);
            if(position.equals(currentElement)){
                lock.notify();
            }
        }
    }

    public E pop() throws InterruptedException {
        E ret = null;
        synchronized (lock){
            if(currentElement<totalElements) {
                if (!sequence.containsKey(currentElement)) {
                    lock.wait();
                }
                ret = sequence.get(currentElement);
                currentElement += 1;
            }
        }
        return ret;
    }

    public boolean isFinished(){
        return currentElement.equals(totalElements);
    }

    public void reset(Integer totalElements){
        this.currentElement = 0;
        this.totalElements = totalElements;
        this.sequence.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return this.new BlockingIterator();
    }

    private class BlockingIterator implements Iterator<E> {

        @Override
        public boolean hasNext() {
            return !isFinished();
        }

        @Override
        public E next() {
            try {
                return pop();
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}
