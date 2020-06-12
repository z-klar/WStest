import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.swing.*;

/**
 * Basic Echo Client Socket
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleEchoSocket {

    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;
    private DefaultListModel<String> dlmLog;

    /**
     *
     * @param dlm
     */
    public SimpleEchoSocket(DefaultListModel dlm)
    {
        this.closeLatch = new CountDownLatch(1);
        this.dlmLog = dlm;
    }

    /**
     *
     * @param duration
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
    {
        return this.closeLatch.await(duration, unit);
    }

    /**
     *
     * @param msg
     */
    public void send(String msg) {
        dlmLog.addElement("********************* Sending Data:  **********************");
        dlmLog.addElement(msg);
        try
        {
            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture(msg);
            fut.get(2, TimeUnit.SECONDS); // wait for send to complete.
        }
        catch (Throwable t)
        {
            Tools.LogException(t, dlmLog);
        }

    }

    /**
     *
     * @param statusCode
     * @param reason
     */
    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        dlmLog.addElement(String.format("****************** Connection closed: %d - %s%n *******************",
                statusCode, reason));
        this.session = null;
    }

    /**
     *
     * @param session
     */
    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        dlmLog.addElement("********************* Got connect: ***********************");
        ourDump(session);
        this.session = session;
    }

    private void ourDump(Session session) {
        String spom = session.toString();
        String[] result = spom.split(",");
        for (int x = 0; x < result.length; x++) dlmLog.addElement(result[x]);
    }

    /**
     *
     * @param msg
     */
    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        dlmLog.addElement("******************* Got msg: **********************");
        dlmLog.addElement(String.format("%s%n", msg));
    }

    /**
     *
     * @param cause
     */
    @OnWebSocketError
    public void onError(Throwable cause)
    {
        dlmLog.addElement("WebSocket Error: ");
        Tools.LogException(cause, dlmLog);
    }
}
