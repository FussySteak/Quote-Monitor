package Server;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultCaret;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JTextArea;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import Common.Symbols;

public class Form {

	HashMap<String,SymbolRecord> tbl=new HashMap<String,SymbolRecord>();
	JFrame frame;
	JTable table;
	Object[] tblHeader=new Object[] {
			"FX Pairs", "VAU BID", "VAU ASK","NZGFT BID", "NZGFT ASK" , "MXT BID" ,"MXT ASK", "VUK BID","VUK ASK","INV BID","INV ASK","ISP BID","ISP ASK"
			//0			  1		 2		3			4					5			  6		     7				8			9
	};

	static Form window;
	private JSplitPane splitPane;
	private JTextArea logArea;
	int sec=180;
	
	public HashMap<String,Integer> pipsMap=new HashMap<String,Integer>();
	public HashMap<String,Integer> thMap=new HashMap<String,Integer>();
	public int defaultTh=3;
	//alert period
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Ini ini=new Ini();
		try {
			ini.load(new File("Server Config.ini"));
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		window = new Form();
		window.initialize();
		window.frame.setVisible(true);

		Listener tcv=new Listener(24325,window);
		tcv.Socketinit();
		tcv.startSocket();

	}

	/**
	 * Create the application.
	 */
	public Form() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1080, 880);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		splitPane = new JSplitPane();
		splitPane.setDividerSize(0);
		splitPane.setPreferredSize(new Dimension(1080, 800));
		splitPane.setSize(1000, 800);
		splitPane.setResizeWeight(0.25);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(scrollPane);

		table = new JTable(new DefaultTableModel(tblHeader, Symbols.SymbolList.length)){

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				component.setBackground(Color.white);
				
				
				if(column==0) return component;
				
				if (column==2||column==1){
					if (!tbl.containsKey(Symbols.SymbolList[row]))return component;
					if(System.currentTimeMillis()-tbl.get(Symbols.SymbolList[row]).time[0]>sec*1000)
						component.setBackground(Color.YELLOW);
					return component;
				}
				else{
					String symbol=(String)super.getModel().getValueAt(row, 0);
					if (super.getModel().getValueAt(row, column)==null) return component;
					String tmp=super.getModel().getValueAt(row, column).toString();
					if (tmp.length()==0) return component;

					float value=Float.parseFloat(tmp);
					int threahold=defaultTh;
					if(thMap.containsKey(symbol))
						thMap.get(symbol);
					if (Math.abs(value)>threahold)                        //GOLD
						component.setBackground(Color.red);
					return component;
				}
			}
			public Class getColumnClass(int i){
				if (i==0) return String.class;
				else return Float.class;
			}
			
		};
		scrollPane.setViewportView(table);

		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		logArea = new JTextArea();
		DefaultCaret caret = (DefaultCaret)logArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scrollPane2.setViewportView(logArea);
		splitPane.setRightComponent(scrollPane2);
		for (int i=0;i<Symbols.SymbolList.length;i++)
			table.setValueAt(Symbols.SymbolList[i], i, 0);
	}

	public JTextArea getTextArea() {
		return logArea;
	}
	public int getThreadhold(String symbol){
		if(thMap.containsKey(symbol))
			return thMap.get(symbol);
		else 
			return defaultTh;
	}

	public int getPips(String symbol) {
		if(pipsMap.containsKey(symbol))
			return (int) Math.pow(10,pipsMap.get(symbol));
		else
			return 10000;
	}
	
}
