import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpEchoServer {

	public static void main(String[] args) throws Exception {
		int serverPort = 9021;
		System.out.println("S: Start auf Host desktop am Port " + serverPort);
		
		ServerSocket ss = new ServerSocket(serverPort);
		Socket s = null;
		System.out.println("S: Vor dem accept()");
		s = ss.accept();
		
		BufferedReader sbr = new BufferedReader(
				new InputStreamReader(s.getInputStream()));
		PrintWriter spw = new PrintWriter(s.getOutputStream());
		
		String zeile;
		while(true) {
			System.out.println("S: Vor dem readLine()");
			zeile = sbr.readLine();
			System.out.println("S: Aus dem Socket kommt->" + zeile);
			spw.println(zeile);
			spw.flush();
			if(zeile.equals("quit")){
				break;
			}
		}
		
		spw.close();
		sbr.close();
		ss.close();
		s.close();
	}
}
