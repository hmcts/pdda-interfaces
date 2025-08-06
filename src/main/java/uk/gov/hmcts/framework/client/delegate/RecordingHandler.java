package uk.gov.hmcts.framework.client.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p/>
 * The class provides decoration to record the invocations for later replay Recording is done as a
 * object stream. The format is as follows <code>
 *         Home Class     --> Identifies the JNDI name of the remote session bean
 *         Method Name    --> Identifies the method on the remote session bean
 *         Class Array    --> Identifies the method parameter types on the remote session bean
 *         Object Array   --> Identifies the method parameters on the remote session bean
 *         </code>

 * @author pznwc5
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class RecordingHandler extends DecoratingHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RecordingHandler.class);

    private String recordingFile;

    private ObjectOutputStream streamGlobal;

    /*
     * (non-Javadoc)

     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
     * java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {

            Method remoteMethod = this.matchMethod(method, getRemote(getDelegateInfo()));
            if (streamGlobal == null) {
                streamGlobal = reWriteFile();
            }
            streamGlobal.writeObject(getDelegateInfo().getHomeClass());
            streamGlobal.writeObject(remoteMethod.getName());
            streamGlobal.writeObject(remoteMethod.getParameterTypes());
            streamGlobal.writeObject(args);
            streamGlobal.flush();

            return super.invoke(proxy, method, args);
        } catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException th) {
            throw new CsUnrecoverableException(th);
        }
    }

    /**
     * Sets the recording file.

     * @param fileName String
     */
    public void setFileName(String fileName) {
        recordingFile = generateFileName(fileName);
    }

    private String generateFileName(String base) {
        Class cls = this.getClass();
        ProtectionDomain protectionDomain = cls.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL loc = codeSource.getLocation();

        StringTokenizer st = new StringTokenizer(loc.toString(), "/");
        String component = null;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int index = token.indexOf("WebTier");
            if (index != -1) {
                component = token.substring(index + "WebTier".length(), token.length());
                break;
            }
        }
        if (component == null) {
            return base;
        }
        return base + component;
    }

    private ObjectOutputStream reWriteFile() {
        File file = new File(recordingFile);
        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(Paths.get(recordingFile)))) {
            if (!file.exists()) {
                return stream;
            }
            RecordingEntry[] entries = readFile();
            for (RecordingEntry entry : entries) {
                stream.writeObject(entry.getHomeClass());
                stream.writeObject(entry.getRemoteMethodName());
                stream.writeObject(entry.getParams());
                stream.writeObject(entry.getArgs());
                stream.flush();
            }
            return stream;
        } catch (IOException th) {
            throw new CsUnrecoverableException(th);
        }
    }

    private RecordingEntry[] readFile() throws IOException {
        boolean isReading = true;
        List<RecordingEntry> list = new ArrayList<>();
        try (ObjectInputStream stream = getObjectInputStream()) {
            if (stream != null) {
                RecordingEntry entry;
                while (isReading) {
                    try {
                        entry = getRecordingEntry();
                        entry.setHomeClass(stream.readObject());
                        entry.setRemoteMethodName(stream.readObject());
                        entry.setParams(stream.readObject());
                        entry.setArgs(stream.readObject());
                        list.add(entry);
                    } catch (EOFException eofException) {
                        isReading = false;
                    } catch (ClassNotFoundException e) {
                        LOG.error("Error: {}", e.getMessage());
                        isReading = false;
                    }
                }
            }
            RecordingEntry[] entries = new RecordingEntry[list.size()];
            for (int i = 0; i < list.size(); i++) {
                entries[i] = list.get(i);
            }
            return entries;
        }
    }

    private RecordingEntry getRecordingEntry() {
        return new RecordingEntry();
    }

    private ObjectInputStream getObjectInputStream() {
        try (ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(Paths.get(recordingFile)))) {
            return stream;
        } catch (FileNotFoundException e) {
            LOG.error("FileNotFoundException: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException: {}", e.getMessage());
        }
        return null;
    }

    private final class RecordingEntry {
        Object homeClass;

        Object remoteMethodName;

        Object params;

        Object args;

        public Object getHomeClass() {
            return homeClass;
        }

        public void setHomeClass(Object homeClass) {
            this.homeClass = homeClass;
        }

        public Object getRemoteMethodName() {
            return remoteMethodName;
        }

        public void setRemoteMethodName(Object remoteMethodName) {
            this.remoteMethodName = remoteMethodName;
        }

        public Object getParams() {
            return params;
        }

        public void setParams(Object params) {
            this.params = params;
        }

        public Object getArgs() {
            return args;
        }

        public void setArgs(Object args) {
            this.args = args;
        }
    }

}
