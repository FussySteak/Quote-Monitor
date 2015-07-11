package Server;
import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.swing.table.DefaultTableCellRenderer;

import Common.Symbols;


public class Listener {
	int port;
	byte[] buf = new byte[128]; 
	DatagramSocket ds;
	DatagramPacket dp_receive;
	boolean switcher=false;
	final int size=6;     // company number including ourself
	Form form;

	public Listener(int port, Form form){
		this.form=form;
		this.port=port;
	}


	public void startSocket() {
		switcher=true;
		DefaultTableCellRenderer bg=new DefaultTableCellRenderer();
		bg.setForeground(Color.RED);
		while(switcher){  
			try {
				ds.receive(dp_receive);

				String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength());
				String str[]=str_receive.split("/");

				int companyId=Integer.parseInt(str[0]);
				String symbol=str[1];
				float bid=Float.parseFloat(str[2]);
				float ask=Float.parseFloat(str[3]);
				Long time=Long.parseLong(str[4]);

				SymbolRecord sr=form.tbl.get(symbol);

				//if (sr.time[0]==0) continue;
				long delay=0;
				if (companyId!=0){

					sr.bid[companyId]=bid;
					sr.ask[companyId]=ask;

					int digits=form.getPips(symbol);
					int dif=form.getThreadhold(symbol);
					
					if (symbol.contains("JPY"))
						digits=1000;
					else if(symbol.contains("XAG")){
						digits=1000;dif=10;
					}else if(symbol.contains("XAU")){
						digits=1000;dif=30;
					}



					float bidDif=Math.round((bid-sr.bid[0])*digits)/10.0f;
					float askDif=Math.round((sr.ask[0]-ask)*digits)/10.0f;

					form.table.setValueAt(bidDif, sr.index, companyId*2+1);
					form.table.setValueAt(askDif, sr.index, companyId*2+2);	
					boolean t=false;

					if ((Math.abs(bidDif)>dif||Math.abs(askDif)>dif)&&sr.bid[0]!=0)
						t=true;

					if (t){
						if (sr.time[companyId]==-1)					// first time detect delay
							sr.time[companyId]=System.currentTimeMillis();
						else 										// detected delay already
							delay=System.currentTimeMillis()-sr.time[companyId];
						if (delay>3000)
							form.getTextArea().append("#Delay: "+symbol+" "+delay+"ms slower than "+companyId+" dif:"+bidDif+"/"+askDif+"\n");
						if (System.currentTimeMillis()-sr.time[0]>form.sec*1000)
							form.getTextArea().append("#Drop: "+symbol+" hasn't bee received for at least "+form.sec+" seconds\n");
					}else{
						if(sr.time[companyId]!=-1){					//price go back
							Long delay2=System.currentTimeMillis()-sr.time[companyId];
							if (delay2>3000)
								form.getTextArea().append("#Miss: "+symbol+" SPIKE "+delay2+"ms from "+companyId+" dif:"+bidDif+"/"+askDif+"\n");
								//System.out.println("#Miss: "+symbol+" SPIKE "+delay2+"ms from "+companyId+" dif:"+bidDif+"/"+askDif);
						}
						sr.time[companyId]=-1L;
					}
					
						//System.out.println("#Delay: "+symbol+" "+delay+"ms slower than "+companyId+" dif:"+bidDif+"/"+askDif);
				}
				else {
					sr.bid[companyId]=bid;
					form.table.setValueAt(bid, sr.index, companyId*2+1);
					sr.ask[companyId]=ask;
					form.table.setValueAt(ask, sr.index, companyId*2+2);
					sr.time[0]=time;
					int digits=100000;
					int dif=3;
					if (symbol.contains("JPY"))
						digits=1000;
					else if(symbol.contains("XAG")){
						digits=1000;dif=10;
					}else if(symbol.contains("XAU")){
						digits=1000;dif=30;
					}
					for (int i=1;i<=5;i++){
						delay=0;
						if (form.table.getValueAt(sr.index, i*2+1)==null)
							continue;
						float bidDif=Math.round((bid-sr.bid[0])*digits)/10.0f;
						float askDif=Math.round((sr.ask[0]-ask)*digits)/10.0f;
						form.table.setValueAt(bidDif, sr.index, i*2+1);
						form.table.setValueAt(askDif, sr.index, i*2+2);
						if (Math.abs(bidDif)>dif||Math.abs(askDif)>dif&&sr.bid[0]!=0){
							if (sr.time[i]==-1)					// first time detect delay
								sr.time[i]=System.currentTimeMillis();
						}
						else{
							if(sr.time[i]!=-1){					//price go back
								Long delay2=System.currentTimeMillis()-sr.time[i];
								if (delay2>3000)
									form.getTextArea().append("#Miss: "+symbol+" SPIKE "+delay2+"ms from "+companyId+" dif:"+bidDif+"/"+askDif+"\n");
									//System.out.println("#Miss: "+symbol+" SPIKE "+delay2+"ms from "+companyId+" dif:"+bidDif+"/"+askDif);
							}
							sr.time[i]=-1L;
						}
						if (delay>3000)
							form.getTextArea().append("#Delay: "+symbol+" "+delay+"ms slower than "+i+" dif:"+bidDif+"/"+askDif+"\n");
							//System.out.println("Delay: "+symbol+" "+delay+"ms slower than "+i+" dif:"+bidDif+"/"+askDif);
					}
				}



				form.table.invalidate();


				//System.out.println(str_receive);
				//由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，  
				//所以这里要将dp_receive的内部消息长度重新置为1024  
				dp_receive.setLength(128);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}  
	}
	
	public void Socketinit() {
		for(int i=0;i<Symbols.SymbolList.length;i++){
			SymbolRecord sr=new SymbolRecord(size);
			sr.index=i;
			form.tbl.put(Symbols.SymbolList[i], sr);
		}
		try {
			ds = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}  
		dp_receive = new DatagramPacket(buf, 128);
	}

}
