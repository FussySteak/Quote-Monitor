package Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPSender {
	
	private static DatagramSocket ds = null;  
	String serverHost = "127.0.0.1"; 
	int serverPort = 24325;
	/** 
	 * 测试客户端发包和接收回应信息的方法 
	 */  
//	public static void main(String[] args) throws Exception {  
//		UDPSender client = new UDPSender();  
//
//		String data="3570;320;BBook;4;EURUSD-STD;600;Market OPEN;2014-10-22 03:32:20;1.285000;0.000000;0.000000;";
//		client.send((data).getBytes());  
//
//		while (true){
//
//			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
//			data=br.readLine();
//			if(data.equals("exit"))
//				break;
//
//			client.send( (data).getBytes());  
//		}
//		// 关闭连接  
//		try {  
//			ds.close();  
//		} catch (Exception ex) {  
//			ex.printStackTrace();  
//		}  
//	}  
	public UDPSender(String ip) throws Exception {  
		ds = new DatagramSocket(8899); // 邦定本地端口作为客户端  
		serverHost=ip;
	}  

	/** 
	 * 向指定的服务端发送数据信息 
	 */  
	public final void send(final byte[] bytes) throws IOException {  
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(serverHost), serverPort);  
		ds.send(dp); 
	}  

	/** 
	 * 接收从指定的服务端发回的数据 
	 */  
//	public final byte[] receive()  
//			throws Exception {  
//		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);  
//		ds.receive(dp);       
//		byte[] data = new byte[dp.getLength()];  
//		System.arraycopy(dp.getData(), 0, data, 0, dp.getLength());       
//		return data;  
//	}  

}
