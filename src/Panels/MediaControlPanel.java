/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Panels;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

/**
 * 控制器面板，控制当前播放的歌曲 包含一个显示当前播放音乐信息的面板 （上一首、下一首、播放/暂停、进度条、音量键、播放模式切换按钮、歌词按钮）
 *
 * @author 陈佳炜
 * @author
 * @author
 */
public class MediaControlPanel extends HBox {

    private HBox controller = new HBox();

    private littleIndicator indicator = new littleIndicator();                                       //      显示当前播放歌曲信息的小面板，包括歌曲名和歌手名
    private String currentSongName;                                                 //        当前播放的歌曲名
    private String currentSongArtist;                                             //        当前播放的歌曲的歌手名
    private boolean isPlaying = false;                                                                          //       控制面板状态，表示播放按钮的状态，播放为true，暂停为false

    private String mediaUrl;                                                                                    //      当前播放歌曲文件路径

    private File preMediaFile;                                                                              //      上一首歌曲的歌曲文件
    private File mediaFile;                                                                                    //       当前播放歌曲的歌曲文件
    private File nextMediaFile;                                                                             //      下一首歌曲的歌曲文件

    private ArrayList<String> songPath = new ArrayList<>();                        //       桉顺序存放播放列表中的音乐文件的绝对路径   使用这个实现列表的顺序播放
    private ArrayList<String> historicList = new ArrayList<>();                     //          历史播放列表
    private int sumOfSongs;                                                                               //       当前列表中的音乐数量

    private PlayingMode playingMode = PlayingMode.LIST_CYCLICAL;        //      歌曲播放模式默认为列表循环

    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private ImageView playBtIcon = new ImageView(new Image("image/playBtIcon.png"));
    private ImageView pauseBtIcon = new ImageView(new Image("image/pauseBtIcon.png"));
    private Button playBt = new Button("");                                                                              //     播放按钮
    private Button rightBt = new Button(">>");                                                                      //      下一首按钮
    private Button leftBt = new Button("<<");                                                                           //      上一首按钮
    private Label currentTime = new Label("00:00");                                                                 //       歌曲当前播放时间
    private Slider progressSld = new Slider();                                                                          //       播放进度条

    private Label totalTime = new Label("00:00");                                                                   //      显示音乐时长
    private Label volumeLbl = new Label("");                                                                            //      音量图标
    private Slider volumeSld = new Slider();                                                                             //     调整音量的进度条  
    private Button modeBt = new Button("三");                                                                        //     播放模式按钮
    private Button lyricBt = new Button("词");                                                                           //         歌词显示按钮

    private Timeline animation = new Timeline();                                                                     //         音乐播放进度条移动动画

    private DecimalFormat df = new DecimalFormat("######0.00");                                   //        保留两位小数

    private SideBarPanel sideBarPanel;

    public MediaControlPanel() {
        this.Initialize();
    }

    public MediaControlPanel(SideBarPanel sideBarPanel) {
        this.setSideBarPanel(sideBarPanel);
        this.Initialize();
    }

    public SideBarPanel getSideBarPanel() {
        return sideBarPanel;
    }

    public void setSideBarPanel(SideBarPanel sideBarPanel) {
        this.sideBarPanel = sideBarPanel;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * 设置当前播放的音乐文件
     */
    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
        //this.sideBarPanel.getListInterface().getPlayllist().getSelectionModel().getSelectedItem().setMusicStatus("O");    //设置状态位
        this.reInitialize();
    }

    public File getMediaFile() {
        return this.mediaFile;
    }

    public File getPreMediaFile() {
        return preMediaFile;
    }

    public void setPreMediaFile(File preMediaFile) {
        this.preMediaFile = preMediaFile;
    }

    public File getNextMediaFile() {
        return nextMediaFile;
    }

    public void setNextMediaFile(File nextMediaFile) {
        this.nextMediaFile = nextMediaFile;
    }

    public void setSongPath(ArrayList<String> songPath) {
        this.songPath = songPath;
    }

    public ArrayList<String> getSongPath() {
        return songPath;
    }

    public void setSumOfSongs(int sumOfSongs) {
        this.sumOfSongs = sumOfSongs;
    }

    /**
     * 类初始化
     */
    private void Initialize() {

        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(55);
        this.setMinHeight(55);
        this.setPrefWidth(1000);
        this.setStyle("-fx-border-color: #1a0068; -fx-border-width: 2px; -fx-border-radius: 0px; -fx-background-border: 0px");   //如何整合到层叠样式表中

        //
        this.modeBt.setId("modeBt");
        this.lyricBt.setId("lyricBt");
        this.playBt.setId("playBt");

        this.volumeSld.setPrefWidth(100);
        this.volumeSld.setValue(50);    //50
        this.progressSld.setMinWidth(550);
        this.progressSld.setMaxWidth(550);

        /*设置各按钮的图标*/
        ImageView volumeIcon = new ImageView(new Image("image/volumeIcon.png"));
        volumeIcon.setFitHeight(21);
        volumeIcon.setFitWidth(21);
        this.playBtIcon.setFitHeight(30);
        this.playBtIcon.setFitWidth(30);
        this.pauseBtIcon.setFitHeight(30);
        this.pauseBtIcon.setFitWidth(30);
        this.volumeLbl.setGraphic(volumeIcon);
        this.playBt.setGraphic(this.playBtIcon);

        this.getStylesheets().add("css/MediaControlPanel.css");     //添加级联样式表

        /*设置各UI控件的事件处理器*/
        this.leftBt.setOnAction(new leftBtHandler());
        this.playBt.setOnAction(new playBtHandler());
        this.rightBt.setOnAction(new rightBtHandler());
        this.progressSld.setOnMousePressed(new progressSldHandler1());
        this.progressSld.setOnMouseReleased(new progressSldHandler2());
        //实现拉动音量进度条时改变音量
        this.volumeSld.setOnMouseReleased(e -> {
            this.mediaPlayer.setVolume(this.volumeSld.getValue() / 100);
        });
        //this.volumeSld.addEventHandler(EventType.ROOT, null);
        this.lyricBt.setOnAction(new displayLyricHandler());
        this.modeBt.setOnAction(new togglePlayingModeHandler());

        this.getChildren().addAll(this.indicator, this.leftBt, this.playBt, this.rightBt, this.currentTime, this.progressSld, this.totalTime);
        this.getChildren().addAll(this.volumeLbl, this.volumeSld, this.modeBt, this.lyricBt);

        /*设置控制模块的样式*/
        HBox.setMargin(this.indicator, new Insets(0, 45, 0, 0));
        HBox.setMargin(this.leftBt, new Insets(10, 10, 10, 20));
        HBox.setMargin(this.playBt, new Insets(10, 10, 10, 10));
        HBox.setMargin(this.rightBt, new Insets(10, 40, 10, 10));
        HBox.setMargin(this.progressSld, new Insets(10, 10, 10, 10));
        HBox.setMargin(this.volumeLbl, new Insets(0, -10, 0, 40));                        //上 右 下 左
        HBox.setMargin(this.volumeSld, new Insets(10, 10, 10, 10));
        HBox.setMargin(this.modeBt, new Insets(10, 5, 10, 30));
        HBox.setMargin(this.lyricBt, new Insets(10, -150, 10, 5));

        /*进度条的动画*/
        animation = new Timeline(new KeyFrame(Duration.seconds(0.5), new progressSldAnimationHandler()));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.pause();

        //this.defaultPlay();
    }

    /**
     * 设置当前歌单列表的第一首歌曲为默认播放歌曲 该方法只能在 songPath 初始化完成后使用，所以我在 MainPagePanel
     * 的初始化方法结束后才调用这个方法
     *
     * @see #songPath
     * @see MainPagePanel
     * @see MainPagePanel#initialize
     */
    public void PrimarySong$intialize() {

        try {
            this.mediaFile = new File(songPath.get(0));                                //       当前列表中第一首歌曲
            this.media = new Media(this.mediaFile.toURI().toString());     //         将音乐文件的绝对路径转化为合适的格式
            this.mediaPlayer = new MediaPlayer(this.media);
        } catch (Exception ex) {
            System.out.println("本地音乐数据文件不存在");
        } finally {
            this.currentSongName = this.getSongName(mediaFile);             //初始化
            this.currentSongArtist = this.getSongArtist(mediaFile);             //初始化
            this.setFormalTotalTime();
        }
    }

    /**
     * 重新初始化控制面板的各个UI控件，通常在更改当前播放歌曲的时候调用
     *
     * @see #setMediaFile(java.io.File)
     */
    public void reInitialize() {
        this.defaultPause();
        mediaPlayer.stop();
        mediaPlayer.dispose();
        progressSld.setValue(0);
        this.currentTime.setText("00:00");
        this.media = new Media(this.mediaFile.toURI().toString());     // 将音乐文件的绝对路径转化为合适的格式
        this.mediaPlayer = new MediaPlayer(this.media);
        this.setFormalTotalTime();                                                              //  设置当前音乐的总时长
        this.mediaPlayer.setVolume(this.volumeSld.getValue() / 100);
        //this.mediaPlayer.volumeProperty().bind(this.vloumeSld.valueProperty().divide(100));      //音量键出现了问题
        this.defaultPlay();
    }

    /**
     * 默认播放的方法
     *
     * @see #indicator
     * @see littleIndicator
     * @see #getSongName(java.io.File)
     * @see #getSongArtist(java.io.File)
     */
    public void defaultPlay() {    //为什么放在事件处理器里可以使用而放在初始化方法里使用不了？
        try {
            this.sideBarPanel.getListInterface().getPlayllist().getSelectionModel().select(songPath.indexOf(mediaFile.getPath()));    //选中当前播放的歌曲
            this.isPlaying = true;
            this.currentSongName = this.getSongName(this.mediaFile);                 //刷新当前歌曲名
            this.currentSongArtist = this.getSongArtist(this.mediaFile);                 //刷新当前歌手名
            this.indicator.setSongNameLbl(this.currentSongName);
            this.indicator.setSongAritistLbl(this.currentSongArtist);
            mediaPlayer.play();
            playBt.setGraphic(this.pauseBtIcon);                       //更换图片结点
            animation.play();
        } catch (Exception ex) {

        }
    }

    /**
     * 默认暂停的方法
     */
    public void defaultPause() {
        this.isPlaying = false;
        mediaPlayer.pause();
        playBt.setGraphic(this.playBtIcon);     //更换图片结点
        animation.pause();
    }

    /**
     * 顺序从头开始播放当前歌曲列表中的所有音乐
     *
     * @see #songPath
     * @see progressSldAnimationHandler
     */
    public void playAll() {
        try {
            this.setMediaFile(new File(this.songPath.get(0))); //初始化第一首音乐和 下一首
            this.setNextMediaFile(new File(this.songPath.get(1)));
        } catch (Exception ex) {

        }

    }

    /**
     * 播放按钮的事件处理器
     *
     * @author 陈佳炜
     */
    public class playBtHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            if (playBt.getGraphic().equals(playBtIcon)) {   //播放
                defaultPlay();
            } else {                                                //暂停
                defaultPause();
            }

        }
    }

    /**
     * 切换上一首歌曲的事件处理器
     *
     * @author 陈佳炜
     */
    public class leftBtHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            defaultPre();

        }
    }

    /**
     * 四种模式下播放上一首的方法
     *
     * @see #playingMode
     * @see PlayingMode
     */
    public void defaultPre() {
        try {
            this.isPlaying = true;
            int currentIndex = songPath.indexOf(mediaFile.getPath());

            switch (this.playingMode) {
                case LIST_CYCLICAL:                     //列表循环
                case SEQUENTIAL:
                case SINGLE_CYCLICAL:
                case RANDOM:
                    this.setPreMediaFile(new File(songPath.get((currentIndex - 2 + sumOfSongs) % sumOfSongs)));                 //新设置上一首音乐
                    this.setMediaFile(new File(songPath.get((currentIndex - 1 + sumOfSongs) % sumOfSongs)));
                    this.setNextMediaFile(new File(songPath.get(currentIndex)));    //新设置的下一首音乐
                    break;
                /*case RANDOM:
                 int randomCurrent = (int) (Math.random() * this.sumOfSongs);//      生成随机数
                 if (randomCurrent == currentIndex) {
                 randomCurrent -= 2;
                 }
                 System.out.println(randomCurrent);
                 this.setMediaFile(this.nextMediaFile);
                 this.setNextMediaFile(new File(songPath.get(currentIndex)));
                 this.setPreMediaFile(new File(songPath.get((randomCurrent + sumOfSongs) % sumOfSongs)));                 //新设置上一首音乐
                 break;*/
            }
        } catch (Exception ex) {

        }

    }

    /**
     * 四种模式下播放下一首的方法
     */
    public void defaultNext() {
        try {
            this.isPlaying = true;
            int currentIndex = songPath.indexOf(mediaFile.getPath());
            switch (this.playingMode) {
                case LIST_CYCLICAL:                     //列表循环
                case SEQUENTIAL:
                case SINGLE_CYCLICAL:
                    setPreMediaFile(mediaFile);                 //新设置上一首音乐
                    setMediaFile(new File(songPath.get((currentIndex + 1) % sumOfSongs)));
                    setNextMediaFile(new File(songPath.get((currentIndex + 2) % sumOfSongs)));    //新设置的下一首音乐
                    break;
                case RANDOM:
                    int randomCurrent = (int) (Math.random() * this.sumOfSongs);//      生成随机数
                    if (randomCurrent == currentIndex) {
                        randomCurrent += 2;
                    }
                    System.out.println(randomCurrent);
                    this.setPreMediaFile(this.mediaFile);                 //新设置上一首音乐
                    this.setMediaFile(new File(songPath.get(randomCurrent)));
                    break;
            }

        } catch (Exception ex) {

        }

    }

    /*调整音量的方法*/
    public void regulateVolume(boolean isUp) {
        if (isUp && this.volumeSld.getValue() <= 95) {
            this.volumeSld.setValue(this.volumeSld.getValue() + 5);
        } else if (!isUp && this.volumeSld.getValue() >= 5) {
            this.volumeSld.setValue(this.volumeSld.getValue() - 5);
        }
        this.mediaPlayer.setVolume(this.volumeSld.getValue() / 100);
    }

    /*下一首*/
    public class rightBtHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            defaultNext();
        }
    }

    /**
     * 播放时进度条刷新动画的事件处理器
     *
     * @author 陈佳炜
     * @see #animation
     */
    public class progressSldAnimationHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            progressSld.setValue(mediaPlayer.getCurrentTime().toSeconds());       // 刷新进度条
            setFormalCurrentTime();// 刷新当前播放的时间  

            if (mediaPlayer.getCurrentTime().toSeconds() == mediaPlayer.getTotalDuration().toSeconds()) {
                try {
                    Thread.sleep(3000);                                      //等待3000 毫秒，也就是3秒.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                /*播放结束后根据当前播放模式播放下一首*/
                switch (playingMode) {
                    case LIST_CYCLICAL:                     //列表循环
                        mode_list_cyclical();
                        break;
                    case RANDOM:
                        mode_list_random();
                        break;
                    case SEQUENTIAL:
                        break;
                    case SINGLE_CYCLICAL:               //单曲循环
                        mode_single_cyclical();
                        break;
                }
            }

        }
    }

    /**
     * 按照XX:XX的格式设置currentTime的方法
     *
     * @see #currentTime
     */
    public void setFormalCurrentTime() {
        /*时间的两种情况*/
        double currentTime = this.mediaPlayer.getCurrentTime().toSeconds();
        String seconds = (int) (currentTime % 60) + "";

        if (currentTime % 60 < 10) {
            seconds = "0" + (int) (currentTime % 60);
        }
        if ((int) (mediaPlayer.getCurrentTime().toSeconds() / 60) < 10) {
            this.currentTime.setText("0" + (int) (currentTime / 60) + ":" + seconds);
        } else {
            this.currentTime.setText((int) (currentTime / 60) + ":" + seconds);   // 设置当前音乐音乐的时长标签
        }
    }

    /**
     * 鼠标按下时播放进度条的事件处理器
     *
     * @see #progressSld
     */
    public class progressSldHandler1 implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent e) {
            animation.pause();
        }
    }

    /**
     * 鼠标松开时播放进度条的事件处理器
     *
     * @see #progressSld
     */
    public class progressSldHandler2 implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent e) {
            if (playBt.getGraphic().equals(playBtIcon)) {   //播放
                defaultPlay();
            }
            mediaPlayer.seek(Duration.seconds(progressSld.getValue()));
            animation.play();

        }
    }

    /**
     * 显示歌词面板的事件处理器
     *
     * @see LyricPanel
     */
    public class displayLyricHandler implements EventHandler<ActionEvent> {

        private LyricPanel lyricPane;
        private Stage lyricStage;
        private Scene lyricScene;

        public displayLyricHandler() {
            this.lyricPane = new LyricPanel();
            this.lyricStage = new Stage();
            //this.lyricStage.initStyle(StageStyle.TRANSPARENT);     
            this.lyricScene = new Scene(lyricPane, 1200, 100);
        }

        @Override
        public void handle(ActionEvent e) {
            lyricStage.setScene(lyricScene);
            lyricStage.show();
        }
    }

    /**
     * 切换播放模式（列表循环、单曲循环、随机播放）的事件处理器
     *
     * @see PlayingMode
     * @author 陈佳炜
     */
    public class togglePlayingModeHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            if (playingMode.equals(PlayingMode.LIST_CYCLICAL)) {
                playingMode = PlayingMode.SINGLE_CYCLICAL;
                modeBt.setText("O");
            } else if (playingMode.equals(PlayingMode.SINGLE_CYCLICAL)) {   //      需要维护一条历史播放列表，eg（播放序号）
                playingMode = PlayingMode.RANDOM;
                int nextNum = (int) (Math.random() * sumOfSongs);
                if (nextNum == songPath.indexOf(mediaFile.getPath())) {
                    nextNum += 2;
                }
                System.out.println(nextNum);
                setNextMediaFile(new File(songPath.get(nextNum % sumOfSongs)));         //      设置随机模式下的下一首和上一首歌曲
                modeBt.setText("R");
            } else if (playingMode.equals(PlayingMode.RANDOM)) {
                playingMode = PlayingMode.LIST_CYCLICAL;
                modeBt.setText("三");
            }
        }
    }

    /**
     * 关闭歌词面板的事件处理器
     *
     * @Todo
     */
    /**
     * 计算mediaFile当前歌曲时长信息的方法
     *
     * @see #mediaFile
     * @return 当前歌曲时长
     */
    public double getCurrentSDuration() {

        try {
            MP3File mp3File = new MP3File(this.mediaFile);                 //封装好的类
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            return header.getTrackLength();

        } catch (Exception ex) {
            System.out.println("无法获取时长");
            return 0.00;
        }

    }

    /**
     * 按照XX:XX的格式设置totalTime的方法
     *
     * @see #totalTime
     */
    public void setFormalTotalTime() {
        double currentSDuration = this.getCurrentSDuration();

        this.progressSld.setMax(currentSDuration);                               //  设置进度条的最大值
        String seconds = (int) (currentSDuration % 60) + "";
        if (currentSDuration % 60 < 10) {
            seconds = "0" + (int) (currentSDuration % 60 % 60);
        }
        if ((int) (currentSDuration / 60) < 10) {
            this.totalTime.setText("0" + (int) (currentSDuration / 60) + ":" + seconds);
        } else {
            this.totalTime.setText((int) (currentSDuration / 60) + ":" + seconds);    // 设置当前音乐音乐的总时长标签
        }
    }

    /**
     * 单曲循环的方法
     */
    public void mode_single_cyclical() {
        this.setMediaFile(this.mediaFile);
    }

    /**
     * 列表循环的方法
     */
    public void mode_list_cyclical() {
        int currentIndex = songPath.indexOf(mediaFile.getPath());
        this.setPreMediaFile(mediaFile);                 //新设置上一首音乐
        this.setMediaFile(new File(songPath.get((currentIndex + 1) % sumOfSongs)));               //    列表循环
        this.setNextMediaFile(new File(songPath.get((currentIndex + 2) % sumOfSongs)));    //新设置的下一首音乐
    }

    /**
     * 随机播放的方法
     */
    public void mode_list_random() {
        int currentIndex = songPath.indexOf(mediaFile.getPath());
        this.setPreMediaFile(mediaFile);                 //新设置上一首音乐
        int randomCurrent = (int) (Math.random() * this.sumOfSongs);//      生成随机数
        if (randomCurrent == currentIndex) {
            randomCurrent += 2;
        }
        this.setMediaFile(new File(songPath.get(randomCurrent % sumOfSongs)));
    }

    /**
     * 显示当前播放歌曲的信息的面板
     *
     * @author 陈佳炜
     * @version 0.5
     */
    public class littleIndicator extends VBox {      //宽度与sideBarPanel一样

        //private ImageView imageView = new ImageView(new Image("image/Cover.png"));
        private Label songNameLbl = new Label("当前播放的音乐");
        private Label songArtistLbl = new Label("歌手名");

        public littleIndicator() {
            this.initialize();
        }

        public Label getSongNameLbl() {
            return songNameLbl;
        }

        public void setSongNameLbl(String songName) {
            this.songNameLbl.setText(songName);
        }

        public Label getSongAritistLbl() {
            return songArtistLbl;
        }

        public void setSongAritistLbl(String songArtist) {
            this.songArtistLbl.setText(songArtist);
        }

        private void initialize() {
            this.setPrefSize(190, 55);
            this.setAlignment(Pos.CENTER);
            this.setStyle("-fx-background-color: #e6e6e6; -fx-border-color: linear-gradient(#b3ccff, #334db3); -fx-border-width: 2px; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
            this.songNameLbl.setFont(Font.font("Verdana", FontWeight.BLACK, FontPosture.REGULAR, 14));
            this.songArtistLbl.setFont(Font.font("Verdana", FontWeight.THIN, FontPosture.REGULAR, 12));

            this.songNameLbl.setText("当前歌曲名");
            this.songArtistLbl.setText("歌手名");
            this.getChildren().addAll(this.songNameLbl, this.songArtistLbl);
        }

        public void update() {
            this.songNameLbl.setText(currentSongName);
            this.songArtistLbl.setText(currentSongArtist);
        }
    }

    /**
     * 获取歌手名
     */
    public String getSongArtist(File mediaFile) {

        try {
            MP3File mp3File = new MP3File(mediaFile);                 //封装好的类
            int START = 6;
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            AbstractID3v2Tag id3v2tag = mp3File.getID3v2Tag();
            String singer = new String(id3v2tag.frameMap.get("TPE1").toString().getBytes("GBK"), "GB2312");
            return singer.substring(START, singer.length() - 3);            //返回seconds

        } catch (Exception ex) {
            System.out.println("无法获取");
            return "unknown";
        }
    }

    /**
     * 获取歌曲名
     */
    public String getSongName(File mediaFile) {

        try {
            MP3File mp3File = new MP3File(mediaFile);                 //封装好的类
            int START = 6;
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            AbstractID3v2Tag id3v2tag = mp3File.getID3v2Tag();
            String songName = new String(id3v2tag.frameMap.get("TIT2").toString().getBytes("GBK"), "GB2312");
            return songName.substring(START, songName.length() - 3);            //返回seconds

        } catch (Exception ex) {
            System.out.println("无法获取");
            return "unknown";
        }
    }

}

enum PlayingMode {

    SEQUENTIAL, SINGLE_CYCLICAL, RANDOM, LIST_CYCLICAL
};                                                                                                                              //播放模式的枚举类型
