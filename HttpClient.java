import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
public class HttpClient{
	static int guesses = 0;
	public static void main(String[] args){
		for(int i= 0; i<1000; i++) {
			new HttpClient();
		}
		System.out.println(String.format("Average %d guesses to get the correct number", guesses/1000));

	}

	public HttpClient() {
		URL url = null;
		try{
			url = new URL("http://localhost:2999");
		}
		catch(MalformedURLException e){
			System.out.println(e.getMessage());
		
		}
		HttpURLConnection con = null;
		try{
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = null;
			con = (HttpURLConnection)url.openConnection();
			con.setRequestProperty("User-Agent","Mozilla");
			con.connect();
			BufferedReader infil = null;
			infil = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String rad = null;

			String cookieField = con.getHeaderField("Set-Cookie");
			//	int clientId = Integer.parseInt(cookieField.substring(cookieField.length()-1));
			//System.out.println(String.format("clientId is %d", clientId));
			int counter = 0;
			int[] boundArray = new int[2];
			while( (rad=infil.readLine()) != null) {
				if(rad.contains("<h>")) {
					System.out.println(rad);
					m = p.matcher(rad);
					while(m.find()) {
						boundArray[counter] = Integer.parseInt(m.group(1));
						counter++;
					}		
				}
			}
			int guess = (boundArray[1] - boundArray[0])/2 + boundArray[0];
			boolean finished = false;
			while(!finished) {
				guesses++;
				System.out.println(guess);
				url = new URL(String.format("http://localhost:2999/?number=%d", guess));
				con = (HttpURLConnection)url.openConnection();
				con.setRequestProperty("User-Agent","Mozilla");
				con.addRequestProperty("Cookie", cookieField);
				con.connect();
				infil = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				rad = null;
				counter = 0;
				boundArray = new int[2];
				while( (rad=infil.readLine()) != null) {

					if(rad.contains("<h>")) {
						System.out.println(rad);
						if(rad.contains("Congratulations")) {
							finished = true;						
						}
						m = p.matcher(rad);
						while(m.find()) {
							boundArray[counter] = Integer.parseInt(m.group(1));
							counter++;
						}		
					}
				}
				guess = (boundArray[1] - boundArray[0])/2 + boundArray[0];
			}


		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

}