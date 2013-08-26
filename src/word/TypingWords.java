package word;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class TypingWords extends JFrame implements KeyListener, ActionListener{
    protected ArrayList<String> words;
    protected JTextPane textpane;
    protected JComboBox combobox;
    protected long time;
    protected int miss;
    protected int length;
    protected String[] current;
    protected int mode;
    protected String[] questions;
    protected int number;
    protected int index;

    static final int[] WORDS_NUMBERS = { 
        10, 20, 30, 40, 50, 60, 70, 80, 90, 100,
        150, 200, 250, 300, 350, 400, 500, 600,
        700, 800, 900, 1000
    };

    public static void main(String[] args){
        if(args.length == 0){
            System.err.println("few arguments");
            System.exit(1);
        }
        TypingWords tw = new TypingWords(args[0]);
    }

    TypingWords(String file_path){
        super("Typing Words");
        this.textpane = new JTextPane();
        this.textpane.setEditable(false);
        this.textpane.setBackground(new Color(0x00, 0x00, 0x00));
        this.textpane.setFont(new Font("Times New Roman", 0, 36));

        this.words = new ArrayList<String>();

        this.combobox = new JComboBox();
        for(int i = 0; i < WORDS_NUMBERS.length; i++){
            this.combobox.addItem(String.format("%5d", WORDS_NUMBERS[i]));
        }

        try{
            FileReader fr = new FileReader(file_path);
            BufferedReader br = new BufferedReader(fr);

            String s;

            while((s = br.readLine()) != null){
                if(s.length() == 0){ break; }
                this.words.add(s);
            }

            if(this.words.size() == 0){
                br.close();
                fr.close();
                throw new IOException();
            }

            br.close();
            fr.close();
        }catch(IOException e){
            System.err.println("illegal file name");
            System.exit(1);
        }

        this.current = new String[2];
        this.load(WORDS_NUMBERS[0]);

        this.add(this.combobox, BorderLayout.WEST);
        this.add(this.textpane, BorderLayout.CENTER);
        this.combobox.addActionListener(this);
        this.textpane.addKeyListener(this);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 75);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    protected void cleanup(){
        this.setColoredText(Color.WHITE, "Press enter to start.");
    }

    protected void reset(){
        this.miss = 0;
        this.mode = 0;
        this.index = 0;
        this.cleanup();
    }

    protected void load(int num){
        this.questions = new String[num];
        this.number = num;
        int ind = 0;
        int len = this.words.size();
        this.length = 0;

        loop:
        while(true){
            Collections.shuffle(this.words);
            for(int i = 0; i < len; i++){
                String s = this.words.get(i);
                this.length += s.length();
                this.questions[ind] = s;
                ind++;
                if(ind >= num){
                    break loop;
                }
            }
        }
        this.reset();
    }

    protected void start(){
        this.current[0] = "";
        this.current[1] = this.questions[0];
        this.mode = 1;
        this.time = System.currentTimeMillis();
        this.setColoredText(Color.YELLOW, this.current[1]);
    }

    protected void setColoredText(Color color, String text){
        this.textpane.setText("");
        this.insertColoredText(color, text);
    }

    protected void insertColoredText(Color color, String text){
        SimpleAttributeSet sas = new SimpleAttributeSet();
        sas.addAttribute(StyleConstants.Foreground, color);
        Document doc = this.textpane.getDocument();
        if(doc != null){
            try{
                doc.insertString(doc.getLength(), text, sas);
            }catch(BadLocationException e){
                System.err.println("error");
                System.exit(1);
            }
        }
    }

    public void keyPressed(KeyEvent e){
        int keycode = e.getKeyCode();

        if(this.mode != 0){
            if(keycode == KeyEvent.VK_ESCAPE){
                this.load(WORDS_NUMBERS[this.combobox.getSelectedIndex()]);
            }
        }
    }

    public void keyReleased(KeyEvent e){
    }

    public void keyTyped(KeyEvent e){
        char keychar = e.getKeyChar();
        if(this.mode == 0){
            if(keychar == ' ' || keychar == '\n'){
                this.start();
            }
        }else if(this.mode == 1){
            if(keychar == this.current[1].charAt(0)){
                if(this.current[1].length() <= 1){
                    this.index ++;
                    if(this.index >= this.number){
                        this.time = System.currentTimeMillis() - this.time;
                        this.mode = 2;
                        String ts = String.format("%.2fsec", (double)this.time / 1000.0);
                        String ms = String.format("%dmiss", this.miss);
                        String kps = String.format("%.2fkey/s", (double)this.length / (double)this.time * 1000.0);
                        String exa = String.format("%.2f%%", (double)this.length / (double)(this.miss + this.length) * 100.0);
                        this.setColoredText(Color.ORANGE, ts + "," + ms + "," + kps + "," + exa);
                    }else{
                        this.current[0] = "";
                        this.current[1] = this.questions[this.index];
                        this.setColoredText(Color.YELLOW, this.current[1]);
                    }
                }else{
                    this.current[0] += keychar;
                    this.current[1] = this.current[1].substring(1);
                    this.setColoredText(Color.RED, this.current[0]);
                    this.insertColoredText(Color.YELLOW, this.current[1]);
                }
            }else{
                this.miss ++;
            }
        }
    }

    public void actionPerformed(ActionEvent e){
        this.load(WORDS_NUMBERS[this.combobox.getSelectedIndex()]);
    }
}

