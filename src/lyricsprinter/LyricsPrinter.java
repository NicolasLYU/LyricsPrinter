package lyricsprinter;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LyricsPrinter
{
    private String filePath;
    private volatile int row = 0;
    private volatile TimerTask timerTask;
    private volatile boolean changed=true;
    private volatile long timeStop=0;

    LyricsPrinter(String filePath){
        this.filePath = filePath;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
    public void rowNext(){
        this.row++;
    }
    public void rowLast(){
        this.row--;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public TimerTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(TimerTask timerTask) {
        this.timerTask = timerTask;
    }

    public static void main(String[] args)throws IOException{
        LyricsPrinter lyricsPrinter = new LyricsPrinter("C://Users/67309/Desktop/体面.lrc");
        ArrayList<String> lyrics = lyricsPrinter.getLyrics();
        KeyBoardListener key = new KeyBoardListener(lyricsPrinter);
        Thread t1 = new Thread(key);
        t1.start();
        while(true) {
            if(lyricsPrinter.isChanged()) {
                lyricsPrinter.setChanged(false);
                lyricsPrinter.printLyrics(lyrics);
            }
            Thread.currentThread().yield();
        }
    }
    public ArrayList<String> getLyrics() throws IOException{
        ArrayList<String> lyrics = new ArrayList<>();
        File file = new File(filePath);
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
    private long getTime(String row){
        long min = Long.parseLong(row.substring(1,3));
        long sec = Long.parseLong(row.substring(4,6));
        long mili = Long.parseLong(row.substring(7,9));
        long sum = min*60000 + sec*1000 + mili*10;
        return sum;
    }
    private void printLyrics(ArrayList<String> lyrics){
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
    private LyricsPrinter lyricsPrinter;
    private boolean timeTaskWait=false;
    KeyBoardListener(LyricsPrinter lyricsPrinter) {
        this.lyricsPrinter = lyricsPrinter;
    }

    @Override
    public void run() {
        while (true) try {
            char input = (char) System.in.read();
            if (input == 'q') {//前一句
                if (lyricsPrinter.getRow() > 0) {
                    lyricsPrinter.rowLast();
                    while (((char) System.in.read()) == 'q' && lyricsPrinter.getRow() > 0)
                        lyricsPrinter.rowLast();
                }
                lyricsPrinter.getTimerTask().cancel();
                lyricsPrinter.setChanged(true);
            } else if (input == 'w') {//后一句
                if (lyricsPrinter.getRow() < lyricsPrinter.getLyrics().size() - 1) {
                    lyricsPrinter.rowNext();
                    while (((char) System.in.read()) == 'w' && lyricsPrinter.getRow() < lyricsPrinter.getLyrics().size() - 1)
                        lyricsPrinter.rowNext();
                    lyricsPrinter.getTimerTask().cancel();
                    lyricsPrinter.setChanged(true);
                }

            } else if (input == 'e') {//暂停
                if(timeTaskWait == false){
                    lyricsPrinter.getTimerTask().cancel();
                    timeTaskWait = true;
                }else{
                    lyricsPrinter.setChanged(true);
                    timeTaskWait = false;
                }

            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}