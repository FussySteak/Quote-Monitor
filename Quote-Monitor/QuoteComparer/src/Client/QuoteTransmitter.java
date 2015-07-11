package Client;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import Common.Symbols;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientEventListener;


public class QuoteTransmitter {
	
	// DDE client
	final static Map<String,DDEClientConversation> conversationMap = new HashMap<String,DDEClientConversation>();
	//final static Map<String,String> price=new HashMap<String,String>();
	final static String TOPIC="QUOTE";
	//static String postfix="";

	public static void main(String[] args) throws Exception{
		
		Ini ini=new Ini();
		ini.load(new File("Terminal Config.ini"));
		final String companyId=ini.get("Settings", "Terminal ID");
		String ip=ini.get("Settings", "Server IP");
		String suffix=ini.get("Settings", "suffix");
				
		Section specialSymbols=ini.get("Special Symbols");
				
		final UDPSender udpSender=new UDPSender(ip);

		for (int i=0;i<Symbols.SymbolList.length;i++){

			final String symbol=Symbols.SymbolList[i];
			DDEClientConversation dde=new DDEClientConversation();
			dde.setEventListener(new DDEClientEventListener()
			{
				public void onDisconnect()
				{
					System.out.println("MT4 Client down");
					try {
						conversationMap.get(symbol).disconnect();
					} catch (DDEException e) {
						System.out.println("DDEClientException: " + e.getMessage());
					}
				}

				public void onItemChanged(String topic, String item, String data)
				{

					if(topic.equals("QUOTE")){
						String[] str=data.split(" ");
						String Bid=str[2];
						String Ask=str[3];
						Long now=System.currentTimeMillis();
						String dataStream=companyId+"/"+symbol+"/"+Bid+"/"+Ask+"/"+now;
						try {
							udpSender.send((dataStream).getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					//System.out.println("onItemChanged(" + topic + "," + item + "," + data + ")");
				}

			});
			conversationMap.put(Symbols.SymbolList[i], dde);
			try {
				dde.connect("MT4", TOPIC);
				if(specialSymbols.containsKey(symbol))
					dde.startAdvice(specialSymbols.get(symbol));
				else 
					dde.startAdvice(symbol+suffix);
			} catch (DDEException e) {
				System.out.print(symbol+" is not ready");
				e.printStackTrace();
			}
		}
		while (true){
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
			String cmd=br.readLine();
			if(cmd.equals("exit"))
				break;
		}

	}


}
