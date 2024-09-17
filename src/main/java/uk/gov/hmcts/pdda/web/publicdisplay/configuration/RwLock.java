package uk.gov.hmcts.pdda.web.publicdisplay.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * <p>
 * Title: Read/Write lock class.
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * This class is intended to handle the locking semantic of data that can be both read from and
 * written to, in order to prevent inconsistent data being returned at read.
 * </p>
 * 
 * <p>
 * The rules behind this lock are:
 * 
 * <ul>
 * <li>Read locks and Write locks are mutually exclusive.</li>
 * <li>There can only ever be one Write lock outstanding, but pending Write locks are queued and
 * handled in turn.</li>
 * <li>There can be arbitrarily many Read locks.</li>
 * <li>One can't get a Read lock until all pending Write locks have been issued and returned.</li>
 * </ul>
 * </p>
 * 00000000000000000000000000000000
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby, Meeraj Kunnumpurath
 * @version 1.0
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public class RwLock {
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RwLock.class);

    // Object on which monitor lock is acquired
    private final Object mutex;

    // Number of locks that are given
    // -1 == a write lock.
    // 0 == no locks.
    // 1..n == number of read locks issued.
    private int givenLocks;

    // Number of writers waiting
    private int waitingWriters;

    /**
     * Creates an instance of the locking class.
     */
    public RwLock() {
        this.mutex = new Object();
        this.givenLocks = 0;
        this.waitingWriters = 0;
    }

    /**
     * Gets the locks given out. Only for debugging purposes, as may change <i>immediately</i> after
     * read.
     * 
     * @return -1 if a write lock given, 0 if no locks, greater than zero is the number of read
     *         locks outstanding.
     */
    public int getGivenLocks() {
        return givenLocks;
    }

    /**
     * Gets a read lock.
     * 
     * @return true if lock successfully achieved, false in the event of a failure.
     */
    public boolean isReadLockObtained() {
        synchronized (mutex) {
            try {
                while (givenLocks == -1 // There is an outstanding write
                    // lock.
                    || // OR
                    waitingWriters != 0 // There are threads waiting for
                // a write lock.
                ) {
                    mutex.wait(); // wait until a lock state has changed
                    // and try again.
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return false;
            }

            givenLocks++; // Increment number of locks for reading.
        }

        LOGGER.debug("Read lock given:{}", givenLocks);

        return true;
    }

    /**
     * Gets the number of threads waiting for a write lock. As there can only ever be one write
     * lock, we have to 'enqueue' writing threads until the lock becomes available. Only for
     * debugging purposes, as may change <i>immediately</i> after read.
     * 
     * @return outstanding threads waiting to lock for writing.
     */
    public int getWaitingWriters() {
        return waitingWriters;
    }

    /**
     * Gets a write lock.
     * 
     * @return true if lock successfully achieved, false in the event of a failure.
     */
    public boolean isWriteLockObtained() {
        synchronized (mutex) {
            waitingWriters++; // Increment number of writers waiting for a
            // lock.

            try {
                while (givenLocks != 0) {
                    mutex.wait(); // wait until a lock state has changed
                    // and try again.
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return false;
            } finally { // Always decrement the number of writers waiting for a lock.
                waitingWriters--;
            }

            givenLocks = -1;
        }

        LOGGER.debug("Write lock given:{}", givenLocks);

        return true;
    }

    /**
     * Release read lock.
     * 
     * @return true if a read lock was present to be released.
     */
    public boolean isReadLockReleased() {
        synchronized (mutex) {
            if (givenLocks > 0) { // There are read locks.
                givenLocks--;
                LOGGER.debug("Read lock released:{}", givenLocks);
                mutex.notifyAll();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Release write lock.
     * 
     * @return true if a write lock was present to be released.
     */
    public boolean isWriteLockReleased() {
        synchronized (mutex) {
            if (givenLocks == -1) { // There is a write lock.
                givenLocks = 0;
                LOGGER.debug("Write lock released:{}", givenLocks);
                mutex.notifyAll();
                return true;
            } else {
                return false;
            }
        }
    }
}
