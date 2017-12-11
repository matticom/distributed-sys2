// Autor: 			Matthias Kugler
// Erstelldatum: 	11.12.2017
// 
// Funktion der Klasse:
// - Server der mit Telefonbuchservice arbeitet

import java.io.*; // Fuer den Reader
import java.net.*; // Fuer den Socket
import java.util.List;
import java.util.regex.Pattern;

class PBServer {

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

			String[] segs = zeile.split(Pattern.quote("/"));
			String content = segs[1].substring(0, segs[1].length() - 5);

			if (!content.startsWith("?")) {
				generateFormHtmlCode(pw, content, ip, port);
			}
			if (content.startsWith("?")) {
				System.out.println(content);
				int delimiterPos = content.lastIndexOf("&");
				String params = content.substring(0, delimiterPos);
				String control = content.substring(delimiterPos + 1);
				if (control.startsWith("C")) {
					Repository repo = new Repository();
					PhonebookSearchService service = new PhonebookSearchService(repo);
					List<PN_Entry> resultList = service.searchWithParams(params);
					System.out.println("Params: " + params);
					if (params.length() < 7) {
						generateFormHtmlCode(pw, content, ip, port);
					} else {
						if (!resultList.isEmpty()) {
							generateResultHtmlCode(pw, resultList, ip, port);
						} else {
							generateNoFoundsHtmlCode(pw, ip, port);
						}
					}
				}
				if (control.startsWith("D")) {
					generateServerShutdownHtmlCode(pw);
					pw.close();
					br.close();
					break;
				}
			}

		} // end while
	} // end main()

	private static void generateServerShutdownHtmlCode(PrintWriter pw) {
		pw.println("HTTP/1.1 200 OK"); // Der Header
		pw.println("Content-Type: text/html");
		pw.println();
		pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta charset='utf-8'>");
		pw.println("<title>Telefonsuchservice</title>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<h1>Server wurde wie gewünscht beendet</h1>");
		pw.println("</body>");
		pw.println("<html>");
		pw.println();
	}
	
	private static void generateNoFoundsHtmlCode(PrintWriter pw, String ip, int port) {
		pw.println("HTTP/1.1 200 OK"); // Der Header
		pw.println("Content-Type: text/html");
		pw.println();
		pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta charset='utf-8'>");
		pw.println("<title>Telefonsuchservice</title>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<h1>Es wurde leider nichts gefunden</h1>");
		pw.println("<br>");
		pw.println("<a href='http://" + ip + ":" + port + "'><button>Zurück</button></a>");
		pw.println("</body>");
		pw.println("<html>");
		pw.println();
		pw.flush();
	}
	
	private static void generateResultHtmlCode(PrintWriter pw, List<PN_Entry> resultList, String ip, int port) {
		pw.println("HTTP/1.1 200 OK"); // Der Header
		pw.println("Content-Type: text/html");
		pw.println();
		pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta charset='utf-8'>");
		pw.println("<title>Telefonsuchservice</title>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<h1>Ergebnisse</h1>");
		pw.println("<table>");
		pw.println("<tr> <td valign=top>Name:</td><td valign=top>Telefonnummer:</td> </tr>");
		for (int i = 0; i < resultList.size(); i++) {
			pw.println(
					"<tr> <td>" + resultList.get(i).name + "</td><td>" + resultList.get(i).phoneNumber + "</td> </tr>");
		}
		pw.println("</table>");
		pw.println("<br>");
		pw.println("<a href='http://" + ip + ":" + port + "'><button>Zurück</button></a>");
		pw.println("</body>");
		pw.println("<html>");
		pw.println();
		pw.flush();
	}

	private static void generateFormHtmlCode(PrintWriter pw, String content, String ip, int port) {
		pw.println("HTTP/1.1 200 OK"); // Der Header
		pw.println("Content-Type: text/html");
		pw.println();
		pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta charset='utf-8'>");
		pw.println("<title>Telefonsuchservice</title>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<h2 align=center>Telefonverzeichnis</h2>");
		pw.println("<h3>Sie können nach Name oder nach Telefonnummer oder nach beiden (nebenläufig) suchen.</h3>");
		pw.println("<form method=get action='http://" + ip + ":" + port + "' accept-charset='UTF-8'>");
		pw.println("<table>");
		pw.println(
				"<tr> <td valign=top>Name:</td>    <td><input name=A pattern='\\S([A-Za-züäößÜÄÖ]|\\s)*'></td>    <td></td> </tr>");
		pw.println("<tr> <td valign=top>Nummer:</td> <td><input name=B pattern='[0-9]*'></td>    <td></td> </tr>");
		pw.println("<tr> <td valign=top><input type=submit name=C value=Suchen></td>");
		pw.println("<td><input type=reset></td>");
		pw.println("<td><input type=submit name=D value='Server beenden' ></td> </tr>");
		pw.println("</table>");
		pw.println("</form>");
		pw.println("</body>");
		pw.println("<html>");
		pw.println();
		pw.flush();
	}
} // end class