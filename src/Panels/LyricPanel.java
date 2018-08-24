/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Panels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * 显示歌词的面板   1.透明化显示 2.
 *
 * @author chenjiawei
 */
public class LyricPanel extends VBox {

    //private TextField lyricField = new TextField();
    private double currentTime = 0.;                             //音乐文件当前的播放时间，以s为单位
    private String lyricFilePath = "test/费玉清 - 一剪梅.lrc";
    private File lyricFile;

    private Text lyricText1 = new Text("上一行歌词");
    private Text lyricText2 = new Text("下一行歌词");

    //歌词与时间的映射表
    private List<Map<Long, String>> lyricList = new ArrayList<Map<Long, String>>();     //注意初始化的问题
    private LinkedHashMap<Long, String> lyricMap = new LinkedHashMap<>();
    private ArrayList<Long> timeList = new ArrayList<>();

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    public Text getLyricText1() {
        return lyricText1;
    }

    public void setLyricText1(Text lyricText1) {
        this.lyricText1 = lyricText1;
    }

    public Text getLyricText2() {
        return lyricText2;
    }

    public void setLyricText2(Text lyricText2) {
        this.lyricText2 = lyricText2;
    }

    /*设置上方歌词行的内容*/
    public void setLyric1(String lyricLine) {
        this.lyricText1.setText(lyricLine);
    }
    /*设置下方歌词行的内容*/

    public void setLyric2(String lyricLine) {
        this.lyricText2.setText(lyricLine);
    }

    public File getLyricFile() {
        return lyricFile;
    }
    /*设置歌词文件*/

    public void setLyricFile(File lyricFile) {
        this.lyricFile = lyricFile;
    }

    /*构造一个歌词面板，1. 歌曲名->歌词文件->读取歌词信息 2.可根据当前播放时间刷新歌词 */
    public LyricPanel() {
        this.initialize();
        this.displayCurrentLyric();
        //this.setLyric1("111");
    }

    private void initialize() {
        /*设置基本属性*/
        this.setPrefSize(1200, 100);
        this.setMaxSize(1200, 100);
        this.setMinSize(1200, 100);

        /*设置Text的属性*/
        this.lyricText1.setFont(Font.font("Courier", FontWeight.BOLD, 40));
        this.lyricText2.setFont(Font.font("Courier", FontWeight.BOLD, 40));
        this.lyricText1.setFill(Color.BLUE);
        this.lyricText2.setFill(Color.RED);

        this.setAlignment(Pos.CENTER);    //居中

        this.getChildren().addAll(this.lyricText1, this.lyricText2);
        
        this.setOpacity(1);               //设置歌词透明度            可以考虑引入配置文件
    }

    /*根据时间从lyric文件中读取歌词，并规则化显示在此面板中*/
    public void displayCurrentLyric() {
        //long currentTime = this.getLongTime(min, sec, mill);
        this.lyricList = this.parse("src/test/告白气球.lrc");     //乱码
       // this.printLrc(lyricList);
        this.saveLrc(this.lyricList);                                               //存储歌词及其时间的映射表
        this.setLyric1(this.lyricMap.get(this.timeList.get(0)));
        this.setLyric2(this.lyricMap.get(this.timeList.get(1)));
    }

    /**
     * 解析LRC歌词文件
     *
     * @param path lrc文件路径
     * @return
     */
    public List<Map<Long, String>> parse(String path) {
        // 存储所有歌词信息的容器
        List<Map<Long, String>> list = new ArrayList<Map<Long, String>>();
        try {
            //String encoding = "UTF-8";                                                                                    // 字符编码，若与歌词文件编码不符将会出现乱码
            String encoding = "GBK";
            File file = new File(path);
            if (file.isFile() && file.exists()) {                                                                                // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String regex = "\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]";                                          // 正则表达式
                Pattern pattern = Pattern.compile(regex);                                                           // 创建 Pattern 对象
                String lineStr = null;                                                                                                  // 每次读取一行字符串
                while ((lineStr = bufferedReader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(lineStr);
                    while (matcher.find()) {
                        // 用于存储当前时间和文字信息的容器
                        Map<Long, String> map = new HashMap<Long, String>();
                        // System.out.println(m.group(0)); // 例：[02:34.94]
                        // [02:34.94] ----对应---> [分钟:秒.毫秒]
                        String min = matcher.group(1); // 分钟
                        String sec = matcher.group(2); // 秒
                        String mill = matcher.group(3); // 毫秒，注意这里其实还要乘以10
                        long time = getLongTime(min, sec, mill + "0");
                        // 获取当前时间的歌词信息
                        String text = lineStr.substring(matcher.end());
                        map.put(time, text); // 添加到容器中
                        list.add(map);
                    }
                }
                read.close();
                return list;
            } else {
                System.out.println("找不到指定的文件:" + path);
            }
        } catch (Exception e) {
            System.out.println("读取文件出错!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将以字符串形式给定的分钟、秒钟、毫秒转换成一个以毫秒为单位的long型数
     *
     * @param min 分钟
     * @param sec 秒钟
     * @param mill 毫秒
     * @return
     */
    public long getLongTime(String min, String sec, String mill) {
        // 转成整型
        int m = Integer.parseInt(min);
        int s = Integer.parseInt(sec);
        int ms = Integer.parseInt(mill);

        if (s >= 60) {
            System.out.println("警告: 出现了一个时间不正确的项 --> [" + min + ":" + sec + "."
                    + mill.substring(0, 2) + "]");
        }
        // 组合成一个长整型表示的以毫秒为单位的时间
        long time = m * 60 * 1000 + s * 1000 + ms;
        return time;
    }

    /**
     * 打印歌词信息
     */
    public void printLrc(List<Map<Long, String>> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("没有任何歌词信息！");
        } else {
            for (Map<Long, String> map : list) {
                for (Map.Entry<Long, String> entry : map.entrySet()) {
                    System.out.println("时间:" + entry.getKey() + "  \t歌词:"
                            + entry.getValue());
                    //System.out.println(entry.getValue());
                }
            }
        }
    }
    /*存储歌词和时间的映射表到LyrciMap中*/

    public void saveLrc(List<Map<Long, String>> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("没有任何歌词信息！");
        } else {
            for (Map<Long, String> map : list) {
                for (Map.Entry<Long, String> entry : map.entrySet()) {
                    this.lyricMap.put(entry.getKey(), entry.getValue());
                    this.timeList.add(entry.getKey());
                }
            }
        }

    }
}
