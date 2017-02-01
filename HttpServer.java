import java.io.*;
import java.net.*; 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class HttpServer{




	public class Numbers {
		private int number, lowerBound, upperBound, guesses;
		public Numbers(int secretNumber) {
			this.number = secretNumber;
			this.lowerBound = 1;
			this.upperBound = 100;
			this.guesses = 0;
		}
		public int getNumber() {
			return number;
		}
		public int getLower() {
			return lowerBound;
		}
		public int getUpper() {
			return upperBound;
		}
		public int getGuesses() {
			return guesses;
		}
		public void incGuesses() {
			this.guesses++;
		}
		public void setLower(int lower) {
			this.lowerBound = lower;
		}
		public void setUpper(int upper) {
			this.upperBound = upper;

		}
	}
	public static void main(String[] args) throws IOException{
		new HttpServer();
	}

	public HttpServer() throws IOException {
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(2999);
		int clientCount = 1;
		Numbers clientNumbers;
		String guessString;
		HashMap<Integer, Numbers> clients = new HashMap<Integer, Numbers>();
		byte[] encoded = Files.readAllBytes(Paths.get("guessing.html"));
		String file = new String(encoded);
		String[] html = file.split("<!-- -->");
		while(true){
			System.out.println("Listening on port 2999");
			Socket s = ss.accept();
			System.out.println("client connected");
			BufferedReader request =
					new BufferedReader(new InputStreamReader(s.getInputStream()));
			String str = request.readLine();
			StringTokenizer tokens = new StringTokenizer(str);
			tokens.nextToken();
			String requestedDocument = tokens.nextToken();
			System.out.println(requestedDocument);
			String str1 = "";
			if(requestedDocument.equals("/")) {
				
				System.out.println(str);
				while( (str = request.readLine()) != null && str.length() > 0){
					//System.out.println(str);
				} 
				   
				System.out.println("GET REQUEST RECIEVED");

				PrintStream response = new PrintStream(s.getOutputStream());     
				response.println("HTTP/1.0 200 OK");     
				response.println("Server : Slask 0.1 Beta");
				response.println("Content-Type: text/html");
				response.println(String.format("Set-Cookie: clientId=%d", clientCount));
				response.println();
				response.print(html[0]);
				response.print("<h>Guess a number between 1 and 100</h>");
				response.print(html[1]);

				Random rng = new Random();
				clientNumbers = new Numbers(rng.nextInt(100)+1);
				clients.put(clientCount, clientNumbers);
				clientCount++;
				s.shutdownInput();
				s.shutdownOutput();
				s.close(); 

				} else if(requestedDocument.contains("number")) {
				System.out.println(str);
				String cookie = null;
				while( (str = request.readLine()) != null && str.length() > 0){
					if(str.contains("Cookie: clientId")) {
						cookie = str;
					}
				}    
				s.shutdownInput();
				Pattern p = Pattern.compile("clientId=(\\d+)");
				Matcher m = p.matcher(cookie);
				int clientId = 0;
				if(m.find()) {
					clientId = Integer.parseInt(m.group(1));
					System.out.println(String.format("clientId is %d",clientId));
				}
				
				
				
				
				int guess = Integer.parseInt(requestedDocument.substring(requestedDocument.indexOf("=")+1));
				System.out.println(guess);
				Numbers clientNumbers1 = clients.get(clientId);
				int number = clientNumbers1.getNumber();
				clientNumbers1.incGuesses();
				
				if(guess==number) {
					guessString = "<h>Congratulations, you guessed the correct number! You made a total of " + clientNumbers1.getGuesses() + " guesses </h> <a href='http://localhost:2999'>PLAY AGAIN</a>";
				} else if(guess>number) {
					clientNumbers1.setUpper(guess-1);
					guessString = String.format("<h>Too high, guess a new number between %d and %d</h>", clientNumbers1.getLower(), clientNumbers1.getUpper());
				} else {
					clientNumbers1.setLower(guess+1);
					guessString = String.format("<h>Too low, guess a new number between %d and %d</h>", clientNumbers1.getLower(), clientNumbers1.getUpper());

				}
				clients.put(clientId, clientNumbers1);
				PrintStream response = new PrintStream(s.getOutputStream()); 
				response.println("HTTP/1.0 200 OK");     
				response.println("Server : Slask 0.1 Beta");     
				response.println("Content-Type: text/html");
				response.println();
				response.print(html[0]);
				response.print(guessString);
				response.print(html[1]);

				s.shutdownOutput();
				s.close(); 

			} else {
				System.out.println("vafan");
				s.close();
			}
			

		}
	}

}