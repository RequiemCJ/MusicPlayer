/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Panels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

/**
 * 当前歌单的歌单信息以及歌曲列表界面面板
 *
 * @author 陈佳炜
 * @author 陈小龙
 * @author 陈康
 * @version 1.0
 */
public class ListInterfacePanel extends VBox {

    private String currentInfoFile = "src/data/mediaFilesInfo.dat";       //      歌单界面当前读取的数据文件的路径
    private String selectedSong;                                                                //      歌单界面当前被选中的歌曲的歌曲名$歌手名

    private listInfoPanel infoPane = new listInfoPanel();                          //      歌单信息面板

    private HBox fuctionGroupPane = new HBox();                                  //        放置设置歌单的一些按钮（导入、清空列表、播放全部）
    private Button importBt = new Button("");
    private Button clearBt = new Button("清空列表");
    private Button playAllBt = new Button("播放全部");

    private ScrollPane scorllPane = new ScrollPane();
    private TableView<Song> playllist = new TableView<>();

    private ObservableList<Song> songs = FXCollections.observableArrayList();
    private ArrayList<String> songPath = new ArrayList<>();                                                  //     桉顺序存放播放列表中的音乐文件的绝对路径   使用这个实现列表的顺序播放
    private TableColumn<Song, Integer> statusCol = new TableColumn(" ");
    private TableColumn<Song, String> sNameCol = new TableColumn("歌曲名");
    private TableColumn<Song, String> artistCol = new TableColumn("歌手名");
    private TableColumn<Song, String> albumCol = new TableColumn("专辑名");
    private TableColumn<Song, String> durationCol = new TableColumn("时长");
    private TableColumn<Song, String> sizeCol = new TableColumn("大小");

    private ContextMenu contextMeunu = new ContextMenu();
    private MenuItem infoItem = new MenuItem("查看歌曲信息");
    private Menu collectItem = new Menu("收藏到歌单");
    private MenuItem deleteItem = new MenuItem("删除");
    private ImageView favoriteMenuIcon = new ImageView(new Image("image/loveIcon.png"));
    private MenuItem myFavoriteList = new MenuItem("我喜欢的音乐");
    private ArrayList<MenuItem> songlistMenus = new ArrayList<>();                                                                                       //        保存已创建的歌单按钮 

    private LinkedHashMap<String, String> mediaFilesInfo = new LinkedHashMap<String, String>();     //      歌曲信息到其音乐文件绝对路径的映射表（歌曲名+歌手名 -> 文件绝对路径）

    private MediaControlPanel bindingControlPane = new MediaControlPanel();                                     //      控制面板

    public TableView<Song> getPlayllist() {
        return playllist;
    }

    public void setPlayllist(TableView<Song> playllist) {
        this.playllist = playllist;
    }

    public ArrayList<MenuItem> getSonglistMenus() {
        return songlistMenus;
    }

    public void setSonglistMenus(ArrayList<MenuItem> songlistMenus) {
        this.songlistMenus = songlistMenus;
    }

    public MediaControlPanel getBindingControlPane() {
        return bindingControlPane;
    }
    
    /**
     * 获取当前歌单界面面板中的信息面板
     * @return 歌单信息面板
     */
    public listInfoPanel getInfoPane() {
        return infoPane;
    }

    public void setInfoPane(listInfoPanel infoPane) {
        this.infoPane = infoPane;
    }
    
    

    /**
     * 默认构造器
     *
     * @param bindingControl 当前界面绑定的音乐控制器面板
     */
    public ListInterfacePanel(MediaControlPanel bindingControl) {
        this.bindingControlPane = bindingControl;
        this.initialize();

    }

    /*面板初始化*/
    private void initialize() {

        /*设置VBox的相关属性*/
        this.setPrefSize(700, 500);
        this.setStyle("-fx-background-color: #cce6ff;");
        this.getStylesheets().add("css/ListInterfacePanel.css");

        /*设置导入和清空按钮*/
        this.fuctionGroupPane.getChildren().addAll( this.playAllBt, this.clearBt);
        this.fuctionGroupPane.setAlignment(Pos.BASELINE_LEFT);
        HBox.setMargin(this.importBt, new Insets(2, 2, 2, 2));
        HBox.setMargin(this.playAllBt, new Insets(2, 2, 2, 2));
        HBox.setMargin(this.clearBt, new Insets(2, 2, 2, 2));

        ImageView plusIcon = new ImageView(new Image("image/plusIcon.png"));
        ImageView trashIcon = new ImageView(new Image("image/trashIcon.png"));
        ImageView playAllIcon = new ImageView(new Image("image/playAllIcon.png"));

        plusIcon.setFitHeight(21);
        plusIcon.setFitWidth(21);
        trashIcon.setFitHeight(21);
        trashIcon.setFitWidth(21);
        playAllIcon.setFitHeight(21);
        playAllIcon.setFitWidth(21);

        this.importBt.setGraphic(plusIcon);
        this.clearBt.setGraphic(trashIcon);
        this.playAllBt.setGraphic(playAllIcon);
        /*绑定按钮事件*/
        this.clearBt.setOnAction(e -> {
            this.clearPlaylist();
        });
        this.playAllBt.setOnAction(e -> {
            this.bindingControlPane.playAll();
        });

        /*设置TableView的相关属性*/
        this.playllist.setEditable(true);   //设置为false时无法相应TableColumn的编辑事件
        this.playllist.setPrefSize(1200, 2200);
        this.playllist.setPlaceholder(new Text("清先导入本地音乐"));
        //this.playllist设置为不可排序

        /*为播放列表各列设置宽度*/
        this.statusCol.setPrefWidth(40);
        this.sNameCol.setPrefWidth(290);
        this.artistCol.setPrefWidth(320);
        this.albumCol.setPrefWidth(200);
        this.durationCol.setPrefWidth(150);
        this.sizeCol.setPrefWidth(100);

        /*为表格中的每列指定相应的单元格工厂*/
        this.statusCol.setCellValueFactory(new PropertyValueFactory<>("musicStatus"));
        this.sNameCol.setCellValueFactory(new PropertyValueFactory<>("songName"));
        this.artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        this.albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        this.durationCol.setCellValueFactory(new PropertyValueFactory("duration"));
        this.sizeCol.setCellValueFactory(new PropertyValueFactory("size"));

        this.playllist.getColumns().addAll(this.statusCol, this.sNameCol, this.artistCol, this.albumCol, this.durationCol, this.sizeCol);

        this.playllist.setItems(this.songs);   //添加测试数据
        /*绑定播放事件*/

        this.statusCol.setOnEditStart(new playCurrentSongHandler_());
        this.sNameCol.setOnEditStart(new playCurrentSongHandler());
        this.artistCol.setOnEditStart(new playCurrentSongHandler());
        this.albumCol.setOnEditStart(new playCurrentSongHandler());
        this.albumCol.setOnEditStart(new playCurrentSongHandler());
        this.durationCol.setOnEditStart(new playCurrentSongHandler());
        this.sizeCol.setOnEditStart(new playCurrentSongHandler());

        this.contextMeunu.getItems().addAll(this.infoItem, this.collectItem, this.deleteItem);
        this.favoriteMenuIcon.setFitHeight(20);
        this.favoriteMenuIcon.setFitWidth(20);
        this.myFavoriteList.setGraphic(this.favoriteMenuIcon);
        this.collectItem.getItems().add(this.myFavoriteList);

        this.infoItem.setOnAction(
                //this.checkSongInfo(this.playllist.getSelectionModel().getSelectedItem().getSongName(), this.playllist.getSelectionModel().getSelectedItem().getArtist());
                new prompSongInfoPaneHandler()
        );

        this.deleteItem.setOnAction(e -> {
            this.deleteASong();
        });

        this.myFavoriteList.setOnAction(new collectSongToListHandler("我喜欢的音乐", "src/data/myFavoriteSonglist.dat"));               //      收藏到我喜欢的音乐

        /*实现收藏歌单的功能原理：获得当前选中的歌曲的歌曲名$歌手，通过歌曲名$歌手获得当前歌曲的绝对路径，从sidebarPane中获得所要收藏歌单的数据文件路径*/
        /*将歌曲名$歌手名 + 绝对路径 添加到收藏歌单的数据文件路径中*/
        /*追加数据的方法为将旧数据存在一个映射表中，往映射表中添加数据，再将此映射表的信息写入数据文件*/
        /*收藏歌单功能在其他面板也有效，但是不能重复收藏到当前歌单*/
        this.playllist.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.SECONDARY) {
                this.contextMeunu.show(this, me.getScreenX(), me.getScreenY());
                this.selectedSong = this.playllist.getSelectionModel().getSelectedItem().getSongName() + "$" + this.playllist.getSelectionModel().getSelectedItem().getArtist();
            } else {
                this.contextMeunu.hide();
            }
        });

        this.setSpacing(5);  //
        this.setPadding(new Insets(10, 0, 0, 10));
        this.scorllPane.setContent(this.playllist);
        this.scorllPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scorllPane.setMaxHeight(10000);
        this.getChildren().addAll(this.infoPane, this.fuctionGroupPane, this.scorllPane);

        this.loadMediaFilesInfo(this.currentInfoFile);       //初始化，加载所有音乐

    }

    /**
     * 数据模型 提供播放列表中各列Cell的属性和方法
     *
     * @serial
     * @see #playllist
     * @see #songs
     */
    public static class Song implements Serializable {

        private final SimpleIntegerProperty musicStatus;    //该行音乐的状态 当前未播放为空
        private final SimpleStringProperty songName;
        private final SimpleStringProperty artist;
        private final SimpleStringProperty album;
        //private final SimpleDoubleProperty duration;   //不知道为什么不能使用double类型传递参数？？
        // private final SimpleDoubleProperty size;
        private final SimpleStringProperty duration;
        private final SimpleStringProperty size;

        public Song(int Sstatus, String sName, String Artist, String Album, String Duration, String size) {
            this.musicStatus = new SimpleIntegerProperty(Sstatus);
            this.songName = new SimpleStringProperty(sName);
            this.artist = new SimpleStringProperty(Artist);
            this.album = new SimpleStringProperty(Album);
            this.duration = new SimpleStringProperty(Duration);
            this.size = new SimpleStringProperty(size);
        }

        public int getMusicStatus() {    //注意命名，否则将无法使用
            return musicStatus.get();
        }

        public void setMusicStatus(int status) {
            this.musicStatus.set(status);
        }

        public String getSongName() {
            return songName.get();
        }

        public void setSongName(String sName) {
            this.songName.set(sName);
        }

        public String getArtist() {
            return artist.get();
        }

        public void setArtist(String Artist) {
            this.artist.set(Artist);
        }

        public String getAlbum() {
            return album.get();
        }

        public void setAlbum(String Albun) {
            this.album.set(Albun);
        }

        public String getDuration() {
            return duration.get();
        }

        public void setDuration(String Duration) {
            this.duration.set(Duration);
        }

        public String getSize() {
            return size.get();
        }

        public void setSize(String Size) {
            this.size.set(Size);
        }
    }

    public class SongInfoPanel extends VBox {

        private TextArea infos = new TextArea("获取不到任何信息");

        public SongInfoPanel() {
            this.initialize();
        }

        public TextArea getInfos() {
            return infos;
        }

        public void setInfos(TextArea infos) {
            this.infos = infos;
        }

        private void initialize() {
            this.setPrefSize(350, 300);
            this.setAlignment(Pos.TOP_CENTER);
            this.setPadding(new Insets(10, 10, 10, 10));
            this.getChildren().add(this.infos);

            this.infos.setEditable(false);
            this.infos.setPrefRowCount(15);
            String selectedSongPath = mediaFilesInfo.get(selectedSong);          //     所要获取信息的歌曲文件的绝对路径
            this.infos.setText(getSongInfos(selectedSongPath));

        }

    }

    /**
     * 点击播放列表中的歌曲时弹出信息面板的事件处理器
     *
     * @author 陈佳炜
     */
    public class prompSongInfoPaneHandler implements EventHandler<ActionEvent> {

        private SongInfoPanel SongInfoPane;                            //      填写新建歌单名的面板
        private Stage stage;
        private Scene scene;

        public prompSongInfoPaneHandler() {
            this.stage = new Stage();
            this.stage.setAlwaysOnTop(true);                        //始终在其他窗体之上
            this.SongInfoPane = new SongInfoPanel();
            this.scene = new Scene(this.SongInfoPane);
            this.stage.setScene(scene);
            this.stage.setResizable(false);
        }

        @Override
        public void handle(ActionEvent e) {
            String selectedSongPath = mediaFilesInfo.get(selectedSong);
            this.SongInfoPane.getInfos().setText(getSongInfos(selectedSongPath));
            this.stage.show();
        }
    }

    /**
     * 从本表格中点击某一行可播放该行歌曲
     *
     * @author 陈佳炜
     * @see MediaControlPanel#setMediaFile(java.io.File)
     * @version 0.5
     */
    public class playCurrentSongHandler implements EventHandler<CellEditEvent<Song, String>> {

        @Override
        public void handle(CellEditEvent<Song, String> e) {
            bindingControlPane.setMediaFile(new File(mediaFilesInfo.get(e.getRowValue().getSongName() + "$" + e.getRowValue().getArtist())));   //播放新歌曲
        }
    }
    /**
     * 从本表格中点击某一行可播放该行歌曲(Integer)
     *
     * @author 陈佳炜
     * @see MediaControlPanel#setMediaFile(java.io.File)
     * @version 0.5
     */
    public class playCurrentSongHandler_ implements EventHandler<CellEditEvent<Song, Integer>> {

        @Override
        public void handle(CellEditEvent<Song, Integer> e) {
            //e.getRowValue().setMusicStatus(0);
            bindingControlPane.setMediaFile(new File(mediaFilesInfo.get(e.getRowValue().getSongName() + "$" + e.getRowValue().getArtist())));   //播放新歌曲
        }
    }

    /**
     * 从保存列表的数据文件中获取列表信息，并将音乐文件的歌曲名和文件绝对路径保存到映射表中
     *
     * @see #mediaFilesInfo
     * @see #currentInfoFile
     * @param mediaInfoPath 数据文件路径
     */
    public void loadMediaFilesInfo(String mediaInfoPath) {

        this.songs.clear();                                                                                                                                 //清空保存Song对象的ObservableList
        this.mediaFilesInfo.clear();                                                                                                                    //清空映射表
        this.songPath.clear();                                                                                                                              //  清空存放绝对路径的线性表
        this.currentInfoFile = mediaInfoPath;                                                                                                  //更新当前绑定的歌单信息文件

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(mediaInfoPath))) {

            int i = 0;    //音乐文件序号计数
            while (input.available() != 0) {
                i++;

                String FileName = input.readUTF();                  //获取音乐文件名
                String path = input.readUTF();                      //获取路径

                this.songPath.add(path);                              //
                String size = input.readUTF();                      //获取音乐文件大小         数据文件中已有的三个属性

                File mediaFile = new File(path);
                String songName = this.getSongName(mediaFile); //获取歌曲名                          //获取歌手名

                double time = this.getSongDuration(mediaFile);

                String artist = this.getSongArtist(mediaFile);
                String album = this.getSongAlbum(mediaFile);
                String duration = this.getFormalTime(time);

                this.mediaFilesInfo.put(songName + "$" + artist, path);  // 歌曲名$歌手名 -> 文件绝对路径
                this.songs.add(new Song(i , songName, artist, album, duration, size));     //添加一行到播放列表中
            }
        } catch (FileNotFoundException ex) {
           // System.out.println("歌单歌曲列表数据文件不存在from#PlaylistPanel$loadMediaFilesInfo");
        } catch (IOException ex) {
           // System.out.println("歌单歌曲列表为空from#PlaylistPanel$loadMediaFilesInfo");
        } catch (Exception ex) {
            //System.out.println("其他错误rom#PlaylistPanel$loadMediaFilesInfo");
        } finally {
            this.bindingControlPane.setSongPath(this.songPath);                   //      将当前播放列表所有歌曲的绝对路径传输到音乐控制面板
            this.bindingControlPane.setSumOfSongs(this.songPath.size());    //      歌曲的数量
        }
    }

    /**
     * 获取格式为XX:XX的时长信息
     *
     * @param time 单位为秒的时间
     * @see #loadMediaFilesInfo(java.lang.String)
     * @return 格式为XX:XX的字符串
     */
    public String getFormalTime(double time) {
        String duration;
        if (time % 60 < 10 && time / 60 < 10) {
            duration = "0" + (int) (time / 60) + ":0" + (int) (time % 60);         //获取时长
        } else if (time % 60 < 10) {
            duration = (int) (time / 60) + ":0" + (int) (time % 60);         //获取时长
        } else if (time / 60 < 10) {
            duration = "0" + (int) (time / 60) + ":" + (int) (time % 60);         //获取时长
        } else {
            duration = (int) (time / 60) + ":" + (int) (time % 60);         //获取时长
        }
        return duration;
    }

    /**
     * 根据绑定的当前的歌单信息文件,清空歌单
     *
     * @see #mediaFilesInfo
     */
    public void clearPlaylist() {
        this.songs.clear();    //清空播放列表中的可视数据
        this.mediaFilesInfo.clear();   //清空映射表
        this.bindingControlPane.defaultPause();
        this.bindingControlPane.getMediaFile().delete();
        this.bindingControlPane.getMediaPlayer().dispose();
        try {
            File file = new File(this.currentInfoFile);    //删除data数据
            if (file.exists()) {
                file.delete();    //删除数据文件  为了避免抛出太多异常，这里使用先删除文件再建立一个空文件的方法
                try {
                    file.createNewFile();
                } catch (Exception ex) {
                    /*处理*/
                    //ex.printStackTrace();
                    System.out.println("创建文件失败");
                }
            } else {
                System.out.println("列表已为空");
            }
        } catch (Exception ex) {
            //System.out.println();
        }

    }

    /**
     * 显示当前歌单的详细信息，包括歌单标题，歌单创建日期，歌单描述
     *
     * @author 陈佳炜
     * @version 0.5
     */
    public class listInfoPanel extends HBox {

        private GridPane textInfo = new GridPane();
        private ImageView imageView = new ImageView(new Image("image/Cover.png"));
        private Label imageLbl = new Label("");
        private Label Title = new Label("本地音乐");
        private Button reviseBt = new Button("修改信息");
        private Label createdDate = new Label("2018-4-18创建");
        private TextArea description = new TextArea("描述：这个是本地音乐歌单，保存了所有你导入的本地音乐。");
        
        /**
         * 默认构造器
         */
        public listInfoPanel() {
            this.initialize();
        }
        /**
         * 带有图片参数的构造方法
         * @param imageView 信息面板的图片
         */
        public listInfoPanel(ImageView imageView) {
            this.imageView = imageView;
        }
        
        /**
         * 设置歌单信息面板图片的方法
         * @param imageView 信息面板的图片 
         */
        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public ImageView getImageView() {
            return imageView;
        }
        
        
        
        public Label getCreatedDate() {
            return createdDate;
        }
        
        /**
         * 返回信息面板中的歌单信息描述面板
         * @return 歌单信息描述面板
         */
        public TextArea getDescription() {
            return description;
        }

        public void setCreatedDate(Label createdDate) {
            this.createdDate = createdDate;
        }

        public void setDescription(TextArea description) {
            this.description = description;
        }
        
        

        public void setTitleLbl(String titile) {
            this.Title.setText(titile);
        }

        private void initialize() {
            this.setPrefSize(700, 300);
            this.setAlignment(Pos.TOP_LEFT);
            this.textInfo.setAlignment(Pos.TOP_LEFT);
            this.imageView.setFitHeight(200);
            this.imageView.setFitWidth(200);
            this.imageLbl.setGraphic(this.imageView);
            this.Title.setId("listTitle");

            this.description.setPrefColumnCount(27);
            this.description.setPrefRowCount(5);
            this.description.setWrapText(true);
            this.description.setEditable(false);
            this.description.setId("description");

            this.textInfo.add(this.Title, 0, 0);
            this.textInfo.add(this.createdDate, 0, 1);
            this.textInfo.add(this.description, 0, 2);

            HBox.setMargin(this.imageLbl, new Insets(20, 20, 20, 20));
            HBox.setMargin(this.textInfo, new Insets(20, 20, 20, 20));

            GridPane.setMargin(this.Title, new Insets(0, 0, 0, 0));
            GridPane.setMargin(this.createdDate, new Insets(10, 0, 10, 10));

            this.getChildren().addAll(this.imageLbl, this.textInfo);

        }

    }

    /**
     * 设置界面标题为当前歌单的标题
     * @parm title 当前歌单标题
     */
    public void setTitleLabel(String title) {
        this.infoPane.setTitleLbl(title);
    }

    /**
     * 使用jaudiotagger-2.4.jar采集mp3文件的信息
     *
     * @param mp3Path 被获取信息的歌曲文件的路径
     * @return 所获取的歌曲信息
     */
    public String getSongInfos(String mp3Path) {
        try {
            StringBuilder infos = new StringBuilder("");
            //System.out.println("----------------Loading...Head-----------------");
            MP3File mp3File = new MP3File(mp3Path);//封装好的类
            int START = 6;
            DecimalFormat df = new DecimalFormat("######0.00");    //保留两位小数

            MP3AudioHeader header = mp3File.getMP3AudioHeader();

            infos.append("时长: " + this.getFormalTime(header.getTrackLength()) + "\n");
            infos.append("比特率: " + header.getBitRate() + "\n"); //获得比特率
            infos.append("音轨长度: " + header.getTrackLength() + "\n"); //音轨长度
            infos.append("格式: " + header.getFormat() + "\n"); //格式，例 MPEG-1
            infos.append("声道: " + header.getChannels() + "\n"); //声道
            infos.append("采样率: " + header.getSampleRate() + "\n"); //采样率
            infos.append("MPEG: " + header.getMpegLayer() + "\n"); //MPEG
            infos.append("MP3起始字节: " + header.getMp3StartByte() + "\n"); //MP3起始字节
            infos.append("精确的音轨长度: " + header.getPreciseTrackLength() + "\n"); //精确的音轨长度
            //System.out.println("----------------Loading...Content-----------------");
            AbstractID3v2Tag id3v2tag = mp3File.getID3v2Tag();
            //String songName = new String(id3v2tag.frameMap.get("TIT2").toString().getBytes("ISO-8859-1"), "GB2312");
            String songName = new String(id3v2tag.frameMap.get("TIT2").toString().getBytes("GBK"), "GB2312");
            //String singer = new String(id3v2tag.frameMap.get("TPE1").toString().getBytes("ISO-8859-1"), "GB2312");
            //String author = new String(id3v2tag.frameMap.get("TALB").toString().getBytes("ISO-8859-1"), "GB2312");
            String singer = new String(id3v2tag.frameMap.get("TPE1").toString().getBytes("GBK"), "GB2312");
            String author = new String(id3v2tag.frameMap.get("TALB").toString().getBytes("GBK"), "GB2312");
            infos.append("歌名：" + songName.substring(START, songName.length() - 3) + "\n");
            infos.append("歌手:" + singer.substring(START, singer.length() - 3) + "\n");
            infos.append("专辑名：" + author.substring(START, author.length() - 3) + "\n");
            return infos.toString();
        } catch (Exception ex) {
            return "没有获取到任何信息";
        }
    }

    /**
     * 获取歌曲文件中的作者信息
     *
     * @param mediaFile 歌曲文件
     * @see #getSongInfos
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
     * 获取歌曲文件中的专辑信息
     *
     * @param 歌曲文件
     * @see #getSongInfos
     */
    public String getSongAlbum(File mediaFile) {

        try {
            MP3File mp3File = new MP3File(mediaFile);                 //封装好的类
            int START = 6;
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            AbstractID3v2Tag id3v2tag = mp3File.getID3v2Tag();
            String author = new String(id3v2tag.frameMap.get("TALB").toString().getBytes("GBK"), "GB2312");
            return author.substring(START, author.length() - 3);            //返回seconds

        } catch (Exception ex) {
            System.out.println("无法获取");
            return "unknown";
        }
    }

    /**
     * 获取歌曲文件中的歌曲名信息
     *
     * @param 歌曲文件
     * @see #getSongInfos
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

    /**
     * 获取歌曲文件中的时长信息
     *
     * @param 歌曲文件
     * @see #getSongInfos
     */
    public double getSongDuration(File mediaFile) {

        try {
            MP3File mp3File = new MP3File(mediaFile);                 //封装好的类
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            return header.getTrackLength();             //返回seconds

        } catch (Exception ex) {
            System.out.println("无法获取");
            return 0.00;
        }

    }

    /**
     * 右键删除当前被选中的单首歌曲
     */
    public void deleteASong() {
        String selectedSongName = this.playllist.getSelectionModel().getSelectedItem().getSongName();
        String selectedSongArtist = this.playllist.getSelectionModel().getSelectedItem().getArtist();
        System.out.println("删除" + this.bindingControlPane.getSideBarPanel().getMediaFilesInfo_current().get(selectedSongName + "$" + selectedSongArtist));                                                                                                                           //从列表中删除
        this.bindingControlPane.getSideBarPanel().getMediaFilesInfo_current().remove(selectedSongName + "$" + selectedSongArtist);
        this.bindingControlPane.getSideBarPanel().saveMediaFilesInfo_current(this.currentInfoFile);
        this.bindingControlPane.getSideBarPanel().loadMediaFilesInfo_current(currentInfoFile);
        this.bindingControlPane.setSongPath(this.songPath);             //      将当前播放列表所有歌曲的绝对路径传输到音乐控制面板
        this.bindingControlPane.setSumOfSongs(this.songPath.size());    //      歌曲的数量
        this.loadMediaFilesInfo(currentInfoFile);
    }

    /**
     * 右键查看歌曲信息
     * @param SongName 歌曲名
     * @param SongArtist  歌手名
     * @see #getSongInfos(java.lang.String)
     */
    public void checkSongInfo(String SongName, String SongArtist) {
        this.getSongInfos(this.mediaFilesInfo.get(SongName + "$" + SongArtist));
    }

    /**
     * 歌单标题装入播放列表右键菜单中的收藏歌单列表，并对每个标题按钮添加事件处理器
     *
     * @see #songlistMenus
     */
    public void loadSonglistMenus() {
        this.collectItem.getItems().clear();
        this.collectItem.getItems().add(this.myFavoriteList);
        for (MenuItem listItem : this.songlistMenus) {
            this.collectItem.getItems().add(listItem);
            listItem.setOnAction(new collectSongToListHandler(listItem.getText()));
        }
    }

    /**
     * 收藏歌单的事件处理器
     */
    public class collectSongToListHandler implements EventHandler<ActionEvent> {

        private String listName;                               //      歌单名
        private String listPath;                                 //      歌单的播放列表数据文件路径
        private String selectedSongPath;                //      被选中的歌曲的绝对路径
        private boolean isMyFavorite = false;                   //      是否是收藏到我喜欢的音乐歌单

        /**
         * 指定歌单名的构造方法，歌单播放列表数据从侧边栏面板类中获取
         *
         * @param listName 歌单名
         */
        public collectSongToListHandler(String listName) {
            this.listName = listName;
        }

        /**
         * 指定歌单名和歌单数据文件路径的构造方法，路径为指定路径
         *
         * @param listName 歌单名
         * @param listPath 指定的路径
         */
        public collectSongToListHandler(String listName, String listPath) {
            this.listName = listName;
            this.listPath = listPath;
            this.isMyFavorite = true;
        }

        public String getListName() {
            return listName;
        }

        public void setListName(String listName) {
            this.listName = listName;
        }

        public String getListPath() {
            return listPath;
        }

        public void setListPath(String listPath) {
            this.listPath = listPath;
        }

        public String getSelectedSongPath() {
            return selectedSongPath;
        }

        public void setSelectedSongPath(String selectedSongPath) {
            this.selectedSongPath = selectedSongPath;
        }

        @Override
        public void handle(ActionEvent e) {
            if (!this.isMyFavorite) {
                this.listPath = bindingControlPane.getSideBarPanel().getSongListsInfo().get(listName);
            }

            this.selectedSongPath = mediaFilesInfo.get(selectedSong);                                                    //      获取被选中歌曲的绝对路径
            bindingControlPane.getSideBarPanel().loadMediaFilesInfo_other(listPath);                                   //      往侧边栏面板类的临时映射表中装载初始歌单数据
            bindingControlPane.getSideBarPanel().getMediaFilesInfo_other().put(selectedSong, selectedSongPath);           //      往临时映射表中添加歌曲新项
            bindingControlPane.getSideBarPanel().saveMediaFilesInfo_other(listPath);                                                           //      将临时映射表中的数据保存到数据文件中
            bindingControlPane.getSideBarPanel().getMediaFilesInfo_other().clear();                                                            //      清空临时映射表中的数据
        }
    }

    /**
     * 删除ArrayList<MenuItem>中的特定元素 并刷新菜单
     *
     * @param listName 被删除的歌单名
     * @see #songlistMenus
     */
    public void deleteListMenu(String listName) {
        for (MenuItem listItem : this.songlistMenus) {
            if (listItem.getText().equals(listName)) {
                this.songlistMenus.remove(listItem);
                break;
            }

        }
        this.loadSonglistMenus();
    }

}
