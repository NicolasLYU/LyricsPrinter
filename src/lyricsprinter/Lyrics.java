package lyricsprinter;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.lang.reflect.Array;
import java.security.Key;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Lyrics
{
    public static volatile int row = 0;
    public static volatile Timer timer;
    public static volatile TimerTask timerTask;
    public static boolean changed=true;
    public static void main(String[] args)throws IOException{
        ArrayList<String> lyrics = getLyrics();
        timer = new Timer();
        KeyBoardListener key = new KeyBoardListener(Thread.currentThread());
        Thread t1 = new Thread(key);
        t1.start();
        while(true) {
            if(changed == true) {
                changed = false;
                printLyrics(lyrics);
            }
            Thread.currentThread().yield();

        }


    }
    public static ArrayList<String> getLyrics() throws IOException{
        ArrayList<String> lyrics = new ArrayList<String>();
        File file = new File("C://Users/67309/Desktop/体面.lrc");
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line = null;
            while((line = br.readLine()) != null){
                //System.out.println(line);
                lyrics.add(line);
            }

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return lyrics;
    }
    public static long getTime(String row){
        long min = Long.parseLong(row.substring(1,3));
        long sec = Long.parseLong(row.substring(4,6));
        long mili = Long.parseLong(row.substring(7,9));
        long sum = min*60000 + sec*1000 + mili*10;
        return sum;
    }
    public static void printLyrics(ArrayList<String> lyrics){
        if(row < lyrics.size()){
            System.out.println(lyrics.get(row));
            if(lyrics.get(row).length() > 9 &&lyrics.get(row).charAt(1) == '0' ) {
                if(row < lyrics.size() - 1) {
                    long now = getTime(lyrics.get(row++));
                    long next = getTime(lyrics.get(row--));
                    long delay = next - now;
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            //System.out.println(lyrics.get(row));
                            row++;
                            printLyrics(lyrics);
                        }
                    };

                    timer.schedule(timerTask, delay);
                }

            }else{
                //System.out.println(lyrics.get(row));
                row++;
                printLyrics(lyrics);
            }
        }
    }
}
class KeyBoardListener implements Runnable{
    Thread main;
    KeyBoardListener(Thread main){
        this.main = main;
    }
    @Override
    public void run(){
        while(true){
            try {
                char input = (char)System.in.read();
                if(input == 'q'){
                    if(Lyrics.row > 0){
                        Lyrics.row--;
                        while(((char)System.in.read()) == 'q' && Lyrics.row > 0)
                            Lyrics.row--;
                    }
                    Lyrics.timerTask.cancel();
                    Lyrics.changed = true;
                }
                else if(input == 'w'){
                    if(Lyrics.row < Lyrics.getLyrics().size() - 1){
                        Lyrics.row++;
                        while(((char)System.in.read()) == 'w' && Lyrics.row < Lyrics.getLyrics().size() - 1)
                            Lyrics.row++;
                        Lyrics.timerTask.cancel();
                        Lyrics.changed = true;
                    }

                }
            }
            catch(IOException e){
                e.getStackTrace();
            }

        }
    }


}
class printLyrics implements Runnable{
    public long delay;
    public ArrayList<String> lyrics;
    public int row;

    printLyrics(long delay, ArrayList<String> lyrics, int row){
        this.delay = delay;
        this.lyrics = lyrics;
        this.row = row;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(delay);
            System.out.println(lyrics.get(row));
        }
        catch (InterruptedException e){
            e.getStackTrace();
        }
    }
}