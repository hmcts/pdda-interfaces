package uk.gov.hmcts.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * Title: FileUtil.
 * </p>
 * <p>
 * Description: This class is used as a repositiry for common functions and utilities relating to
 * file
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: Electronic Dathreads Systems
 * </p>
 * 
 * @author Will Fardel
 * @version $Id: FileUtil.java,v 1.3 2006/06/05 12:30:21 bzjrnl Exp $
 */
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidThreadGroup", "PMD.AvoidSynchronizedStatement",
    "PMD.NullAssignment"})
public final class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Stop creation of this utility class.
     */
    private FileUtil() {
        // Change Permisions
    }

    /**
     * Make this dir and all the dirs above this path. Use this instead of File.mkdirs to work
     * around 4742723.
     * 
     * @param path the dir to create
     * @throws IOException if cant create the dir for any reason
     */
    public static boolean mkdirs(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path: null");
        }
        return mkdirs(new File(path));
    }

    /**
     * Make this dir and all the dirs above this path. Use this instead of File.mkdirs to work
     * around 4742723.del.
     * 
     * @param dir path the dir to create
     * @throws IOException if cant create the dir for any reason
     */
    public static boolean mkdirs(File dir) {
        synchronized (FileUtil.class) {
            if (dir == null) {
                throw new IllegalArgumentException("dir: null");
            }
            return dir.mkdirs();
        }
    }

    /**
     * Test method.
     * 
     * @param args the command line arguments, these are not used.
     * @throws InterruptedException Exception
     */
    @SuppressWarnings("removal")
    public static void main(final String[] args) throws InterruptedException {
        final Object lock = new Object();

        final String rootDir = getStringProperty("fileutil.root", "./FileUtilTest");
        final int threadCount = getIntProperty("fileutil.threadcount", 10);
        final int parentCount = getIntProperty("fileutil.parentcount", 20);
        final int dirCount = getIntProperty("fileutil.dircount", 20);

        ThreadGroup tg = new ThreadGroup("runners");

        final Thread[] threads = new Thread[threadCount];
        final int[] makeErrors = new int[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final Object localLock = getObject();
            final int id = i;
            threads[id] = new Thread(tg, "racer-" + id) {
                @Override
                public void run() {
                    synchronized (this) {
                        runThread(id, lock, localLock, rootDir, parentCount, dirCount, makeErrors);

                        threads[id] = null;
                    }
                }
            };

            synchronized (localLock) {
                threads[id].start();
                localLock.wait();
            }
        }

        // Start all threads
        synchronized (lock) {
            out("3");
            lock.wait(1000);
            out("2");
            lock.wait(1000);
            out("1");
            lock.wait(1000);
            out("GO!");
            lock.notifyAll();
        }

        // Wait for all threads to complete
        while (tg.activeCount() > 0) {
            Thread.sleep(2000);
        }
        tg.destroy();

        // Report any Errors
        int totalErrors = 0;
        for (int i = 0; i < makeErrors.length; i++) {
            if (makeErrors[i] != 0) {
                err("mkdirs errors thread #" + i + " = " + makeErrors[i]);
                totalErrors += makeErrors[i];
            }
        }
        out("mkdirs errors total #" + totalErrors);

        // Tidy up
        deltree(new File(rootDir));
    }

    private static void runThread(final int id, final Object lock, final Object localLock,
        final String rootDir, final int parentCount, final int dirCount, final int... makeErrors) {
        synchronized (FileUtil.class) {
            localLock.notifyAll();
            try {
                lock.wait();
            } catch (InterruptedException e) {
                LOG.error("Error: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOG.error("Error: {}", e.getMessage());
            }

            for (int l = 0; l < parentCount; l++) {
                for (int f = 0; f < dirCount; f++) {
                    String fname = rootDir + "\\b\\c\\dir_" + l + "\\" + f + "\\" + id;

                    File file1 = getFile(fname);
                    File file2 = getFile(fname);

                    if (!mkdirs(file1) && !file2.exists()) {
                        makeErrors[id]++;
                    }
                }
            }
        }
    }

    private static Object getObject() {
        return new Object();
    }

    private static File getFile(String fname) {
        return new File(fname);
    }

    private static void deltree(File dir) {
        for (File sub : dir.listFiles()) {
            if (sub.isDirectory()) {
                deltree(sub);
            } else if (!sub.delete()) {
                err("cannot delete " + sub);
            }
        }

        if (!dir.delete()) {
            err("cannot delete " + dir);
        }
    }

    private static void out(Object object) {
        LOG.debug("[" + Thread.currentThread().getName() + "] " + object);
    }

    private static void err(Object object) {
        LOG.debug("[" + Thread.currentThread().getName() + "] " + object);
    }

    private static String getStringProperty(String name, String defaultValue) {
        return System.getProperty(name, defaultValue);
    }

    private static int getIntProperty(String name, int defaultValue) {
        String value = System.getProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                // Do nothing
                LOG.debug("Ignored - NumberFormatException");
            }
        }
        return defaultValue;
    }

}
