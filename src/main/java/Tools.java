import javax.swing.*;

public class Tools {

    public static void LogException(Throwable ex, DefaultListModel dlm) {
        StackTraceElement [] trace;
        int i;
        dlm.addElement(String.format("Exception:"));
        dlm.addElement(ex.getClass().toString());
        dlm.addElement(ex.getMessage());
        dlm.addElement("Stack Trace:");
        trace = ex.getStackTrace();
        i = 0;
        for (StackTraceElement se: trace) {
            dlm.addElement(String.format("Class:%s Method: %s Line: %d",
                    se.getClassName(), se.getMethodName(), se.getLineNumber()));
            i++;
            if (i > 10) break;
        }
    }
}
