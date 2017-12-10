import java.io.*;      // Fuer den Reader
import java.net.*;     // Fuer den Socket

class GetFilter {
  public static void main(String[] args) throws Exception {

    // Programmstart und Portbelegung
    // Zuerst eigenen Hostname holen
    // ---------------------------------------------------------
    String host = InetAddress.getLocalHost().getHostName();
    int port = 9876;
    System.out.println("Server startet auf "+host+" an "+port);

    // ServerSocket einrichten und in einer Schleife auf 
    // Requests warten.
    // ---------------------------------------------------------
    ServerSocket ss = new ServerSocket(port);
    String zeile = null;

    while(true) {
      System.out.println("Warte im accept()");
      Socket cs = ss.accept();        // <== Auf Requests warten
      BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                              cs.getInputStream()));
      PrintWriter pw = new PrintWriter(cs.getOutputStream());

      // Den Request lesen (Hier nur die erste Zeile)
      // -------------------------------------------------------
      zeile = br.readLine();
      System.out.println("Kontrollausgabe: "+zeile);

      // Favicon-Requests ignorieren
      // -------------------------------------------------------
      if(zeile.startsWith("GET /favicon")) {
        System.out.println("Favicon-Request");
        br.close();
        continue;                       // Zum naechsten Request
      }

      // Nur GET-Requests bearbeiten
      // -------------------------------------------------------
      if(! zeile.startsWith("GET ")) {
        System.out.println("Kein GET-Request");
        br.close();
        continue;                       // Zum naechsten Request
      }

      // Den GET-Request bearbeiten (Hier: nur zuruecksenden)
      // -------------------------------------------------------
      System.out.println("Request wird bearbeitet");

      pw.println("HTTP/1.1 200 OK");               // Der Header
      pw.println("Content-Type: text/html");
      pw.println();
      pw.println("<html>");              // Die HTML-Seite
      pw.println("<body>");
      pw.println("<h1><font color=green>");
      pw.println(zeile);
      pw.println("</font></h1>");
      pw.println("</body>");
      pw.println("</html>");
      pw.println();
      pw.flush();
      pw.close();
      br.close();
    }  // end while
  }  // end main()
}  // end class
