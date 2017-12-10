import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpEchoClient {
	public static void main(String[] args) throws Exception {
		System.out.println("S: Start");
		
		System.out.println("S: Socket einrichten und verbinden");
		Socket s = new Socket("laptopname", 9021);
				
		BufferedReader sbr = new BufferedReader(
				new InputStreamReader(s.getInputStream()));
		PrintWriter spw = new PrintWriter(s.getOutputStream());
		
		String echo = null;
		String back = null;
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		while(true) {
			System.out.println("\nGib Echostring (Ende mit quit)");
			System.out.println("->");
			echo = br.readLine();
			echo = echo.trim();
			System.out.println("C: Vor dem println() mit:" + echo);
			spw.println(echo);
			spw.flush();
			System.out.println("C: Vor dem readLine()");
			back = sbr.readLine();
			System.out.println("Zurueck kommt:" + back);
			if(back.equals("quit")){
				break;
			}
		}
		
		spw.close();
		sbr.close();
		br.close();
		s.close();
	}
}
