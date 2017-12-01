package omnikryptec.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFromInternet {

    /**
     * From http://www.torsten-horn.de/techdocs/java-net.htm#TimeFromInternet
     *
     * @param args Test
     */
    public static void main(String[] args) {
        final String DEFAULT_TIME_SERVER = "ptbtime1.ptb.de";
        final SimpleDateFormat DATUMFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        final long SEKUNDEN_1900_1970 = 2208988800L;
        Socket so = null;
        InputStream in = null;
        long time = 0;
        try {
            so = new Socket((args.length > 0) ? args[0] : DEFAULT_TIME_SERVER, 37);
            in = so.getInputStream();
            for (int i = 3; i >= 0; i--) {
                time ^= (long) in.read() << i * 8;
            }
            // Der Time Server gibt die Sekunden seit 1900 aus, Java erwartet Millisekunden seit 1970:
            System.out.println(DATUMFORMAT.format(new Date((time - SEKUNDEN_1900_1970) * 1000)));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
            if (so != null) {
                try {
                    so.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}
