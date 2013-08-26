package document;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.*;

public class TypingDocument extends JFrame implements ActionListener, KeyListener{
	JTextPane textpane;
	// JPanel panel;
	JComboBox combobox;
	JLabel label;
	ArrayList<String[]> paths;
	Game game;
	
	public static void main(String[] args){
		if(args.length < 1){
			System.err.println("few arguments");
			System.exit(1);
		}
		TypingDocument t = new TypingDocument(args[0]);
	}
	
	TypingDocument(String dir_name){
		super("Typing Document");
		this.textpane = new JTextPane();
		this.textpane.setEditable(false);
		this.textpane.setBackground(new Color(0x90, 0xEE, 0x90));
		this.textpane.setFont(new Font("Times New Roman", 0, 24));
		this.combobox = new JComboBox();
		this.paths = new ArrayList<String[]>();
		
		try{
			FileReader fr = new FileReader(dir_name + "/index.txt");
			BufferedReader br = new BufferedReader(fr);
			
			int index = 0;
			
			while(true){
				String line = br.readLine();
				if(line == null){ break; }
				String[] datas = line.split("\\s+");
				if(datas.length < 2){ break; }
				
				this.combobox.addItem(String.format("%3d: ", index + 1) + datas[0]);
				
				String[] ps = new String[datas.length - 1]; 
				
				for(int i = 1; i < datas.length; i++){
					ps[i - 1] = dir_name + "/" + datas[i];
				}
				this.paths.add(ps);
				index++;
			}
			br.close();
			fr.close();
		}catch(IOException e){
			System.err.println("illegal directory name");
			System.exit(1);
		}
		
		this.label = new JLabel();
		this.label.setForeground(Color.CYAN);
		this.label.setBackground(Color.BLACK);
		this.label.setOpaque(true);
		this.label.setFont(new Font("Times New Roman", 0, 36));
		this.cleanup();
		this.add(this.combobox, BorderLayout.NORTH);
		this.add(this.textpane, BorderLayout.CENTER);
		this.add(this.label, BorderLayout.SOUTH);
		this.combobox.addActionListener(this);
		this.textpane.addKeyListener(this);
		
		this.game = new Game();
		this.game.set(0, this.paths.get(0));
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	protected void cleanup(){
		this.textpane.setText("");
		this.label.setText("Press enter to start.");
	}
	
	public void keyPressed(KeyEvent e){
		int keycode = e.getKeyCode();
		int mode = this.game.getMode();
		
		if(mode != 0){
			if(keycode == KeyEvent.VK_ESCAPE){
				int select = this.combobox.getSelectedIndex();
				this.game.set(select, paths.get(select));
				this.cleanup();
			}
		}
	}
	
	public void keyReleased(KeyEvent e){
	}
	
	public void keyTyped(KeyEvent e){
		int mode = this.game.getMode();
		char key = e.getKeyChar();
		
		if(mode == 0){
			if(key == ' ' || key == '\n'){
				this.game.start(this.textpane, this.label);
			}
		}else if(mode == 1){
			this.game.action(key, this.textpane, this.label);
		}
	}
	
	public void actionPerformed(ActionEvent e){
		int index;
		
		if((index = this.combobox.getSelectedIndex()) != -1){
			this.game.set(index, this.paths.get(index));
			this.cleanup();
		}
	}
}

class Game{
	private static final int QUEUE_LENGTH = 10; 
	private ThemeDocument[] document_queue;
	private Integer[] queue_iterator;
	private ThemeDocument target_document;
	private RunTimeDocument runtime_document;
	private long time;
	private int string_length;
	private int miss;
	private int mode;
		
	Game(){
		this.document_queue = new ThemeDocument[QUEUE_LENGTH];
		this.queue_iterator = new Integer[QUEUE_LENGTH];
	}
	
	public int loadDocument(int index, String[] paths){
		for(int i = 0; i < QUEUE_LENGTH; i++){
			if(this.queue_iterator[i] == null){
				this.document_queue[i] = new ThemeDocument(paths);
				this.queue_iterator[i] = new Integer(index);
				return i;
			}else if(this.queue_iterator[i].intValue() == index){
				int n = 0;
				for(int j = 0; j < QUEUE_LENGTH; j++){
					if(this.queue_iterator[j] != null){
						n = j;
					}
				}
				
				ThemeDocument td = this.document_queue[i];
				Integer it = this.queue_iterator[i];
				
				for(int j = i; j < n; j++){
					this.document_queue[j] = this.document_queue[j + 1];
					this.queue_iterator[j] = this.queue_iterator[j + 1];
				}
				
				this.document_queue[n] = td;
				this.queue_iterator[n] = it;
				
				return n;
			}
		}
		
		for(int i = 0; i < QUEUE_LENGTH - 1; i++){
			this.document_queue[i] = this.document_queue[i + 1];
			this.queue_iterator[i] = this.queue_iterator[i + 1];
		}
		
		this.document_queue[QUEUE_LENGTH - 1] = new ThemeDocument(paths);
		this.queue_iterator[QUEUE_LENGTH - 1] = new Integer(index);
		
		return QUEUE_LENGTH - 1;
	}
	
	public void set(int index, String[] paths){
		this.mode = 0;
		this.miss = 0;
		
		int it = this.loadDocument(index, paths);
		this.runtime_document = new RunTimeDocument(this.document_queue[it]);
		
		this.string_length = this.runtime_document.getSeries().length();
	}
	
	public int getMode(){
		return this.mode;
	}
	
	public void start(JTextPane textpane, JLabel label){
		this.mode = 1;
		this.setDocument(textpane, label);
		this.time = System.currentTimeMillis();
	}
	
	public void setDocument(JTextPane textpane, JLabel label){
		textpane.setText("");
		SimpleAttributeSet attrred = new SimpleAttributeSet();
		SimpleAttributeSet attrblue = new SimpleAttributeSet();
		attrred.addAttribute(StyleConstants.Foreground, Color.RED);
		StyleConstants.setForeground(attrred, Color.RED);
		attrblue.addAttribute(StyleConstants.Foreground, Color.BLUE);
		StyleConstants.setForeground(attrblue, Color.BLUE);
		Document doc = textpane.getDocument();
		if(doc != null){
			try{
				doc.insertString(doc.getLength(), this.runtime_document.getPrevious(), attrred);
				doc.insertString(doc.getLength(), this.runtime_document.getFollowing(), attrblue);
			}catch(BadLocationException e){
				
			}
		}
		
		String ser = this.runtime_document.getSeries();
		
		if(ser.length() > 30){
			label.setText(ser.substring(0, 30));
		}else{
			label.setText(ser);
		}
	}
	
	public void action(char key, JTextPane textpane, JLabel label){
		int result = this.runtime_document.action(key);
		
		if(result == 0){
			this.miss++;
		}else{
			this.setDocument(textpane, label);
			
			if(result == 2){
				this.mode = 2;
				this.time = System.currentTimeMillis() - this.time;
				String ts = String.format("%.2f sec", (double)this.time / 1000.0);
				String ms = String.format("%d miss", this.miss);
				String kps = String.format("%.2f key/s", (double)this.string_length / (double)this.time * 1000.0);
				String exa = String.format("%.2f %%", (double)this.string_length / (double)(this.miss + this.string_length) * 100.0);
				label.setText(ts + ", " + ms + ", " + kps + ", " + exa);
			}
		}
		
	}
}

class ThemeDocument{
	protected ArrayList<String> pages;
	protected String series;
	
	ThemeDocument(){
		this.pages = new ArrayList<String>();
		this.series = "";
	}
	
	ThemeDocument(String[] paths){
		this.pages = new ArrayList<String>();
		this.series = "";
		
		try{
			for(String path : paths){
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
				String page = "\t";
				this.series += ' ';
				int in_size;
				char[] buf = new char[256];
				
				while((in_size = br.read(buf, 0, 256)) != -1){
					for(int i = 0; i < in_size; i++){
						if(buf[i] == '\n'){
							page += "\n\t";
							this.series += ' ';
						}else{
							page += buf[i];
							this.series += buf[i];
						}
					}
				}
				this.pages.add(page);
				br.close();
				fr.close();
			}
		}catch(IOException e){
			System.err.println("file read error");
		}
	}
	
	public ArrayList<String> getPages(){ return pages; }
	public String getSeries(){ return series; }
}

class RunTimeDocument{
	protected ArrayList<String> pages;
	protected String series;
	protected String previous;
	protected String following;
	protected int page_number;
	
	RunTimeDocument(ThemeDocument tdoc){
		this.pages = tdoc.getPages();
		this.series = tdoc.getSeries();
		this.page_number = 0;
		this.previous = "";
		this.following = this.pages.get(0);
	}
	
	public String getSeries(){ return this.series; }
	public String getPrevious(){ return this.previous; }
	public String getFollowing(){ return this.following; }
	
	public int action(char key){ // miss 0, ok 1, end 2 
		if(this.series.charAt(0) == key){
			this.series = this.series.substring(1);
			if(key == ' '){
				if(this.following.length() == 0){
					this.page_number++;
					this.previous = "\t";
					this.following = this.pages.get(this.page_number).substring(1);
				}else if(this.following.charAt(0) == '\n'){
					this.previous += "\n\t";
					this.following = this.following.substring(2);
				}else if(this.following.charAt(0) == '\t'){
					this.previous += '\t';
					this.following = this.following.substring(1);
				}else{
					this.previous += key;
					this.following = this.following.substring(1);
					if(this.series.length() == 0){
						return 2;
					}else{
						return 1;
					}
				}
				return 1;
			}else{
				this.previous += key;
				this.following = this.following.substring(1);
				if(this.series.length() == 0){
					return 2;
				}else{
					return 1;
				}
			}
		}else{
			return 0;
		}
	}
}

