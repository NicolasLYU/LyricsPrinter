package lyricsprinter;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Lyrics
{
    public static volatile int row = 0;
    public static volatile TimerTask timerTask;
    public static boolean changed=true;
    public static void main(String[] args)throws IOException{
        ArrayList<String> lyrics = getLyrics();
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
        ArrayList<String> lyrics = new ArrayList<>();
        File file = new File("C://Users/67309/Desktop/体面.lrc");
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine()) != null){
                lyrics.add(line);
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return lyrics;
    }
    private static long getTime(String row){
        long min = Long.parseLong(row.substring(1,3));
        long sec = Long.parseLong(row.substring(4,6));
        long mili = Long.parseLong(row.substring(7,9));
        long sum = min*60000 + sec*1000 + mili*10;
        return sum;
    }
    private static void printLyrics(ArrayList<String> lyrics){
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
                            row++;
                            printLyrics(lyrics);
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, delay);
                }
            }else{
                row++;
                printLyrics(lyrics);
            }
        }
    }
}
class KeyBoardListener implements Runnable {
    private Thread main;
    private boolean timeTaskWait=false;
    KeyBoardListener(Thread main) {
        this.main = main;
    }

    @Override
    public void run() {
        while (true) try {
            char input = (char) System.in.read();
            if (input == 'q') {//前一句
                if (Lyrics.row > 0) {
                    Lyrics.row--;
                    while (((char) System.in.read()) == 'q' && Lyrics.row > 0)
                        Lyrics.row--;
                }
                Lyrics.timerTask.cancel();
                Lyrics.changed = true;
            } else if (input == 'w') {//后一句
                if (Lyrics.row < Lyrics.getLyrics().size() - 1) {
                    Lyrics.row++;
                    while (((char) System.in.read()) == 'w' && Lyrics.row < Lyrics.getLyrics().size() - 1)
                        Lyrics.row++;
                    Lyrics.timerTask.cancel();
                    Lyrics.changed = true;
                }

            } else if (input == 'e') {//暂停
                if(timeTaskWait == false){
                    Lyrics.timerTask.cancel();
                    timeTaskWait = true;
                }else{
                    Lyrics.changed = true;
                    timeTaskWait = false;
                }

            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}