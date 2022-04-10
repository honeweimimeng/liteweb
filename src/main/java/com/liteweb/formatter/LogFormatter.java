package com.liteweb.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
    // ANSI escape code
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[38;5;252m";

    private String BODY=ANSI_WHITE;

    private static final HashMap<String,String> LevelColorMap=new HashMap<>();
    static {
        LevelColorMap.put("INFO",ANSI_GREEN);
        LevelColorMap.put("WARNING",ANSI_YELLOW);
        LevelColorMap.put("SEVERE",ANSI_RED);
    }

    public void RestColor(String body){
        BODY=body;
    }

    // Here you can configure the format of the output and
    // its color by using the ANSI escape codes defined above.

    // format is called for every console log message
    @Override
    public String format(LogRecord record)
    {
        // This example will print date/time, class, and log level in yellow,
        // followed by the log message and it's parameters in white .
        StringBuilder builder = new StringBuilder();
        builder.append(ANSI_WHITE);

        builder.append("[");
        builder.append(calcDate(record.getMillis()));
        builder.append("]");

        builder.append(ANSI_RESET);
        builder.append(ANSI_PURPLE);
        builder.append(" #");
        builder.append("ForLiteWeb");
        builder.append("#");

        builder.append(ANSI_RESET);
        builder.append(ANSI_BLUE);

        builder.append(" ------[");
        builder.append(Thread.currentThread().getName());
        builder.append(Thread.currentThread().getId());
        builder.append("]");

        builder.append(ANSI_RESET);
        builder.append(ANSI_CYAN);

        builder.append(" [");
        builder.append(record.getSourceClassName());
        builder.append("]");

        builder.append(ANSI_RESET);
        String levelColor=LevelColorMap.get(record.getLevel().getName());
        builder.append(levelColor==null? ANSI_GREEN :levelColor);

        builder.append(" [");
        builder.append(record.getLevel().getName());
        builder.append("]");

        builder.append(ANSI_RESET);
        builder.append(BODY);
        builder.append(" - ");
        builder.append(record.getLoggerName()).append(" # ").append(record.getMessage());

        Object[] params = record.getParameters();

        if (params != null)
        {
            builder.append("\t");
            for (int i = 0; i < params.length; i++)
            {
                builder.append(params[i]);
                if (i < params.length - 1)
                    builder.append(", ");
            }
        }

        builder.append(ANSI_RESET);
        builder.append("\n");
        return builder.toString();
    }

    private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }
}
