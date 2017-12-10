import java.io.*; // Fuer den Reader
import java.net.*; // Fuer den Socket

class GetFilter {
	public static void main(String[] args) throws Exception {

		// Programmstart und Portbelegung
		// Zuerst eigenen Hostname holen
		// ---------------------------------------------------------
		String host = InetAddress.getLocalHost().getHostName();
		String ip = InetAddress.getLocalHost().getHostAddress();
		int port = 9876;
		System.out.println("Server startet auf " + host + " / " + ip + " an " + port);

		// ServerSocket einrichten und in einer Schleife auf
		// Requests warten.
		// ---------------------------------------------------------
		ServerSocket ss = new ServerSocket(port);
		String zeile = null;

		while (true) {
			System.out.println("Warte im accept()");
			Socket cs = ss.accept(); // <== Auf Requests warten
			BufferedReader br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			PrintWriter pw = new PrintWriter(cs.getOutputStream());

			// Den Request lesen (Hier nur die erste Zeile)
			// -------------------------------------------------------
			zeile = br.readLine();
			System.out.println("Kontrollausgabe: " + zeile);

			// Favicon-Requests ignorieren
			// -------------------------------------------------------
			if (zeile.startsWith("GET /favicon")) {
				System.out.println("Favicon-Request");
				br.close();
				continue; // Zum naechsten Request
			}

			// Nur GET-Requests bearbeiten
			// -------------------------------------------------------
			if (!zeile.startsWith("GET ")) {
				System.out.println("Kein GET-Request");
				br.close();
				continue; // Zum naechsten Request
			}

			// Den GET-Request bearbeiten (Hier: nur zuruecksenden)
			// -------------------------------------------------------
			System.out.println("Request wird bearbeitet");

			pw.println("HTTP/1.1 200 OK"); // Der Header
			pw.println("Content-Type: text/html");
			pw.println();
			pw.println("<html>");
			pw.println("<body>");
			pw.println("<h2 align=center>Telefonverzeichnis</h2>");
			pw.println("<h3>Sie können nach Name oder nach Telefonnummer oder nach beiden (nebenläufig) suchen.</h3>");
			pw.println("<form method=get action='http://" + ip + ":" + port + "'>");
			pw.println("<table>");
			pw.println("<tr> <td valign=top>Name:</td>    <td><input name=A></td>    <td></td> </tr>");
			pw.println("<tr> <td valign=top>Nummer:</td> <td><input name=B></td>    <td></td> </tr>");
			pw.println("<tr> <td valign=top><input type=submit name=C value=Suchen></td>");
			pw.println("<td><input type=reset></td>");
			pw.println("<td><input type=submit name=D value='Server beenden' ></td> </tr>");
			pw.println("</table>");
			pw.println("</form>");
			pw.println("</body>");
			pw.println("<html>");
			pw.println();
			pw.flush();
			pw.close();
			br.close();
		} // end while
	} // end main()
} // end class
