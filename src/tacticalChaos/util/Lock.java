package tacticalChaos.util;

/**
 * Locks are used in threads synchronization
 */

public class Lock {

    public boolean locked = true;

    public void lock(){
        locked=true;
    }

    public void unlock(){
        locked=false;
    }

}
