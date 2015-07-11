package Server;

public class SymbolRecord {
	int index;
	float[] bid;
	float[] ask;
	Long[] time;
	public SymbolRecord(int size){
		bid=new float[size];
		ask=new float[size];
		time=new Long[size];
		//long now=System.currentTimeMillis();
		for (int i=0;i<size;i++)
			time[i]=-1L;
	}
	
}
