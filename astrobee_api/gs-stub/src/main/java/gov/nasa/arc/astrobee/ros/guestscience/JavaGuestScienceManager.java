package gov.nasa.arc.astrobee.ros.guestscience;

import gov.nasa.arc.astrobee.ros.RobotConfiguration;
import gov.nasa.arc.astrobee.ros.NodeExecutorHolder;
import gov.nasa.arc.astrobee.ros.internal.util.MessageType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ros.node.NodeConfiguration;

/**
 * Construct Manager in your main (JavaGuestScienceManager will make the NodeMain (ie GuestScienceNodeMain)
 * Implement AppInterface
 * Give AppIntImpl to the JavaGuestScienceManager
 * JavaGuestScienceManager calls AppImpl when it needs to tell it something
 * AppImpl has to be able to send commands through this.
 */
public class JavaGuestScienceManager {
    private final Log logger = LogFactory.getLog(JavaGuestScienceManager.class);
    GuestScienceNodeMain m_nodeMain;
    StartGuestScienceService m_app;


    public JavaGuestScienceManager() {
        logger.info("JavaGuestScienceManager() ctor");
        RobotConfiguration robotConfiguration = new RobotConfiguration();
        final NodeConfiguration nodeConfiguration = robotConfiguration.build();
        m_nodeMain = new GuestScienceNodeMain();
        NodeExecutorHolder.getExecutor().execute(m_nodeMain, nodeConfiguration);

        logger.info("JavaGuestScienceManager() ctor finished");

    }

    public boolean acceptApplication(StartGuestScienceService app) {
        m_app = app;
        m_app.acceptManager(this);
        try {
            while (!m_nodeMain.isStarted()) {
                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            return false;
        }

        m_nodeMain.publishGuestScienceConfig(m_app);

        return true;
    }

    public void sendData(MessageType type, String topic, byte[] byteData) {
        if (byteData.length > 2048) {
            throw new RuntimeException("Data passed to sendData function is too big to send to " +
                    "ground. Must be 2K.");
        }
        m_nodeMain.sendGuestScienceData(m_app.getFullName(), topic, byteData, type);
    }

    public void sendData(MessageType type, String topic, String dataString) {
        byte[] byteData = dataString.getBytes();
        if (byteData.length > 2048) {
            throw new RuntimeException("Data passed to sendData function is too big to send to " +
                    "ground. Must be 2K.");
        }
        m_nodeMain.sendGuestScienceData(m_app.getFullName(), topic, byteData, type);
    }
}
