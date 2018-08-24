/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Panels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The {@code SideBarPanel} class 提供了音乐播放器的侧边栏 包含 本地音乐的导入按钮、我喜爱的歌单、以及本地音乐、新建歌单按钮
 * 以及已经创建的歌单
 *
 * @author 陈佳炜
 * @author 陈康
 * @author 陈小龙
 */
public class SideBarPanel extends VBox {

    private VBox sidebar = new VBox();                                          //      存放各UI控件的面板

    private ScrollPane scrollpane = new ScrollPane();                   //      存放sidebar的面板

    private Button currentClickedButton;                                                     //     当前被选中的按钮 用来改变样式

    private Button favoriteBt = new Button("");                                           //     我喜欢的音乐

    private Button importBt = new Button("");                                             //     导入本地音乐

    private Button localMusicbt = new Button("  本地音乐         ");            //      本地音乐

    private Button Managmentbt = new Button("  歌单管理         ");         //      音乐管理

    private Button collectionbt = new Button(" 播放历史         ");              //     我的收藏

    private Separator spt1 = new Separator();                                           //      分隔符1

    private Separator spt2 = new Separator();                                           //       分隔符2

    private Button createdListBt = new Button("新建歌单           ");            //       新建歌单

    private ContextMenu songlistMenu = new ContextMenu();               //      鼠标右键点击歌单显示

    private MenuItem editItem = new MenuItem("编辑歌单信息");

    private MenuItem deleteItem = new MenuItem("删除歌单");

    private MenuItem cancelItem = new MenuItem("取消");

    private LinkedHashMap<String, String> mediaFilesInfo_current = new LinkedHashMap<String, String>();        //    当前播放界面列表的 歌曲名$歌手名->歌曲文件绝对路径的映射表

    private LinkedHashMap<String, String> songListsInfo = new LinkedHashMap<String, String>();             //      已创建的歌单名->歌单播放列表数据的绝对路径的映射表

    private LinkedHashMap<String, String> mediaFilesInfo_other = new LinkedHashMap<String, String>();   //         其他歌单中 歌曲名$歌手名->歌曲文件绝对路径 的临时映射表

    private ArrayList<Button> songListBts = new ArrayList<>();                                                      //      保存创建歌按钮的数组线性表，允许重名

    private TitledPane songListPane = new TitledPane();                                                                 //          显示已创建歌单的可下拉面板

    private VBox songListsContainer = new VBox();                                                                           //          存放歌单的面板

    private ListInterfacePanel listInterface;                                             //  绑定具体的播放列表对象

    private String selectedlistButton;                                                      //      当前被鼠标右键点击的歌单按钮

    /**
     * 这个类必须被实例化后才能使用
     */
    public SideBarPanel() {
        this.initialize();
    }

    /**
     * 实例化一个可以控制音乐播放器中歌单界面的侧边栏
     *
     * @param listInterface 侧边栏当前控制的歌单界面
     */
    public SideBarPanel(ListInterfacePanel listInterface) {
        this.setListInterface(listInterface);    //设置控制的播放界面
        this.initialize();                                    //侧边栏初始化
    }

    public ListInterfacePanel getListInterface() {
        return this.listInterface;
    }

    public void setListInterface(ListInterfacePanel listInterface) {
        this.listInterface = listInterface;
    }

    public LinkedHashMap<String, String> getMediaFilesInfo_current() {
        return mediaFilesInfo_current;
    }

    public LinkedHashMap<String, String> getMediaFilesInfo_other() {
        return mediaFilesInfo_other;
    }

    /**
     * 返回已创建的歌单名-歌单播放列表数据的绝对路径的映射表的方法
     *
     * @return 已创建的歌单名-歌单播放列表数据的绝对路径的映射表
     */
    public LinkedHashMap<String, String> getSongListsInfo() {
        return songListsInfo;
    }

    public void setSongListsInfo(LinkedHashMap<String, String> songListsInfo) {
        this.songListsInfo = songListsInfo;
    }

    /**
     * 侧边栏面板的初始化方法
     */
    private void initialize() {

        /*初始化，装载数据（本地音乐）*/
        this.loadMediaFilesInfo_current("src/data/mediaFilesInfo.dat");

        /*设置面板属性*/
        this.sidebar.setPrefSize(170, 800);
        this.scrollpane.setPrefWidth(190);
        this.scrollpane.setOpacity(0.9);
        this.scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  //取消水平滚动条
        this.songListPane.setText("创建的歌单");
        VBox.setMargin(this.songListPane, new Insets(5, 2, 5, 2));
        this.songListPane.setContent(this.songListsContainer);
        this.songListsContainer.setStyle("-fx-background-color: #b3ccff;");
        this.songListsContainer.setAlignment(Pos.BOTTOM_LEFT);
        //this.setPadding(new Insets(10, 10, 10, 10));
        this.getStylesheets().add("css/SidebarPanel.css");

        //this.songlistMenu.getItems().addAll(this.editItem, this.deleteItem, this.cancelItem);                     //右键点击歌单显示

        /*绑定各个MenuItem的事件*/
        this.sidebar.setAlignment(Pos.TOP_CENTER);
        this.sidebar.setStyle("-fx-background-color: b3ccff");
        this.setStyle("-fx-background-color: b3ccff");

        /*布局并设置各按钮*/
        this.sidebar.getChildren().addAll(this.importBt, this.favoriteBt, this.spt2, this.localMusicbt, this.Managmentbt, this.collectionbt, this.spt1);
        this.sidebar.getChildren().addAll(this.createdListBt, this.songListPane);
        this.importBt.setId("importBt");
        this.favoriteBt.setId("favoriteBt");

        VBox.setMargin(this.importBt, new Insets(15, 2, 2, 2));
        VBox.setMargin(this.favoriteBt, new Insets(2, 2, 7, 2));
        VBox.setMargin(this.spt2, new Insets(5, 2, 5, 2));
        VBox.setMargin(this.localMusicbt, new Insets(2, 2, 2, 2));
        VBox.setMargin(this.Managmentbt, new Insets(2, 2, 2, 2));
        VBox.setMargin(this.collectionbt, new Insets(2, 2, 2, 2));
        VBox.setMargin(this.spt1, new Insets(5, 2, 5, 2));
        VBox.setMargin(this.createdListBt, new Insets(2, 2, 2, 2));

        /*各按钮的图像节点及其大小设置*/
        ImageView addIcon = new ImageView(new Image("image/addIcon.png"));
        ImageView loveIcon = new ImageView(new Image("image/loveIcon.png"));
        ImageView computerIcon = new ImageView(new Image("image/computerIcon.png"));
        ImageView managmentIcon = new ImageView(new Image("image/managmentIcon.png"));
        ImageView collectionIcon = new ImageView(new Image("image/collectionIcon.png"));
        ImageView creatIcon = new ImageView(new Image("image/creatIcon.png"));
        addIcon.setFitHeight(25);
        addIcon.setFitWidth(25);
        loveIcon.setFitHeight(25);
        loveIcon.setFitWidth(25);
        computerIcon.setFitHeight(25);
        computerIcon.setFitWidth(25);
        managmentIcon.setFitHeight(25);
        managmentIcon.setFitWidth(25);
        collectionIcon.setFitHeight(25);
        collectionIcon.setFitWidth(25);
        creatIcon.setFitHeight(20);
        creatIcon.setFitWidth(20);

        this.localMusicbt.setGraphic(computerIcon);
        this.Managmentbt.setGraphic(managmentIcon);
        this.collectionbt.setGraphic(collectionIcon);
        this.importBt.setGraphic(addIcon);
        this.favoriteBt.setGraphic(loveIcon);
        this.createdListBt.setGraphic(creatIcon);
        this.createdListBt.setContentDisplay(ContentDisplay.RIGHT);
        /*各按钮功能绑定*/

        this.importBt.setOnAction(new localFilesImportHandler());
        this.favoriteBt.setOnAction(new displayFavoriteListHandler());
        this.localMusicbt.setOnAction(new displayAllMusicHandler());
        this.createdListBt.setOnAction(new promptInputHandler());
        this.deleteItem.setOnAction(e -> {                       //删除
            this.deleteSongList(this.selectedlistButton);                                                           //      删除当前被选中的歌单，当前歌单被删除后应该显示其他歌单！！！
        });
        this.songlistMenu.getItems().addAll(editItem, deleteItem, cancelItem);

        this.currentClickedButton = this.localMusicbt;    //初始化时显示的是本地音乐歌单
        this.localMusicbt.setStyle("-fx-background-radius: 8; -fx-border-color: #334db3; -fx-border-radius: 8; -fx-background-color: linear-gradient(#8099ff, #4d66cc);");
        //初始化本地音乐按钮

        this.scrollpane.setContent(this.sidebar);
        this.getChildren().add(this.scrollpane);
        this.importCreatedList();               //导入已创建的歌单    //放在最后执行
        this.listInterface.loadSonglistMenus();     //装入右键收藏歌单菜单选项
    }

    /**
     * 添加本地音乐更新本地音乐播放列表的处理器
     *
     * @author 陈佳炜
     * @version 0.5
     */
    public class localFilesImportHandler implements EventHandler<ActionEvent> {

        /*是否要将导入的音乐文件都复制到一个特定的地址，如果需要，此处设置一个目标路径*/
        /*导入新音乐文件前保留旧数据*/
        private List<File> files;

        @Override
        public void handle(ActionEvent e) {
            mediaFilesInfo_current.clear();                        //清空先前歌单的数据 
            listInterface.loadMediaFilesInfo("src/data/mediaFilesInfo.dat");
            listInterface.setTitleLabel("本地音乐");
            loadMediaFilesInfo_current("src/data/mediaFilesInfo.dat");      //保存当前歌单的信息到映射表
            currentClickedButton.setStyle(null);
            localMusicbt.setStyle("-fx-background-radius: 8; -fx-border-color: #334db3; -fx-border-radius: 8; -fx-background-color: linear-gradient(#8099ff, #4d66cc);");
            currentClickedButton = localMusicbt;                             //设置当前选中的按钮
            /*导入新音乐文件前导入旧本地音乐数据文件  */
            try {
                /* 文件选择器，选择多个音乐文件，获取文件名（信息）和绝对路径名，并保存在映射表中，再存储在一个文件里*/
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("mp3 files (*.mp3)", "*.mp3"); //筛选java文件
                fileChooser.getExtensionFilters().add(extFilter);

                this.files = fileChooser.showOpenMultipleDialog(new Stage()); //选择文件

                //String FileName;                      原先的key值为mp3的文件名，现在为mp3文件的信息（歌曲名+歌手名）
                String songNameAndSongArtist;
                String absolutePath;

                for (File file : files) {
                    absolutePath = file.getPath();    //获取被选中的文件的绝对路径
                    songNameAndSongArtist = listInterface.getSongName(file) + "$" + listInterface.getSongArtist(file);        //获取被选中的文件的文件名
                    mediaFilesInfo_current.put(songNameAndSongArtist, absolutePath);                                                                                             //（歌曲名$歌手名 -> 歌曲的绝对路径）
                    //System.out.println(file.getPath() + " " + file.getName() + " " + getMediaFileSize(new File(file.getPath())));
                }

                saveMediaFilesInfo_current("src/data/mediaFilesInfo.dat");                                                                                                                                      //保存映射表 （歌曲信息 -> 绝对路径）     
                listInterface.loadMediaFilesInfo("src/data/mediaFilesInfo.dat");    // 导入播放列表  应该是添加
            } catch (Exception ex) {
                System.out.println("未导入任何歌曲文件");
            } finally {
                /**/
                listInterface.getBindingControlPane().PrimarySong$intialize();     //           本地音乐数据文件不存在的情况下
            }
        }

    }

    /**
     * 装载特定路径的数据文件中的歌曲信息到当前播放列表的映射表中
     *
     * @param mediaInfoPath 歌单播放列表歌曲的信息数据文件
     * @see #mediaFilesInfo_current
     */
    public void loadMediaFilesInfo_current(String mediaInfoPath) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(mediaInfoPath))) {
            int i = 0;    //音乐文件序号计数
            while (input.available() != 0) {
                i++;

                String songNameAndSongArtist = input.readUTF();                  //获取音乐文件名
                String path = input.readUTF();                      //获取路径
                String size = input.readUTF();                      //获取音乐文件大小
                this.mediaFilesInfo_current.put(songNameAndSongArtist, path);  // 歌曲名$歌手名 -> 文件绝对路径
            }
        } catch (FileNotFoundException ex) {
            //System.out.println("尚未添加任何音乐文件form#SideBarPanel$loadMediaFilesInfo_current");
        } catch (IOException ex) {
            //System.out.println("歌单播放列表为空form#SideBarPanel$loadMediaFilesInfo_current");
        } catch (Exception ex) {
            //System.out.println("其他错误form#SideBarPanel$loadMediaFilesInfo_current");
        }
    }

    /**
     * 装载特定路径的数据文件中的歌曲信息到其他歌单播放列表的映射表中
     *
     * @param mediaInfoPath 歌单播放列表歌曲的信息数据文件
     * @see #mediaFilesInfo_other
     */
    public void loadMediaFilesInfo_other(String mediaInfoPath) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(mediaInfoPath))) {
            int i = 0;    //音乐文件序号计数
            while (input.available() != 0) {
                i++;

                String songNameAndSongArtist = input.readUTF();                  //获取音乐文件名
                String path = input.readUTF();                      //获取路径
                String size = input.readUTF();                      //获取音乐文件大小
                this.mediaFilesInfo_other.put(songNameAndSongArtist, path);  // 歌曲名$歌手名 -> 文件绝对路径
            }
        } catch (FileNotFoundException ex) {
            //System.out.println("尚未添加任何音乐文件form#SideBarPanel$loadMediaFilesInfo_other");
        } catch (IOException ex) {
           // System.out.println("歌单播放列表为空form#SideBarPanel$loadMediaFilesInfo_other");
        } catch (Exception ex) {
            //System.out.println("其他错误form#SideBarPanel$loadMediaFilesInfo_other");
        }
    }

    /**
     * 保存当前歌单播放列表的信息到特定路径下的dat文件
     *
     * @param targetPath 数据文件的路径
     * @see #mediaFilesInfo_current
     */
    public void saveMediaFilesInfo_current(String targetPath) {

        Set<Map.Entry<String, String>> mediaFilesSet = this.mediaFilesInfo_current.entrySet();  //转换为可遍历的集
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(targetPath))) {
            for (Map.Entry<String, String> entry : mediaFilesSet) {
                output.writeUTF(entry.getKey());                                                                                            //歌曲名$歌手名
                output.writeUTF(entry.getValue());                                                                                       //音乐文件绝对路径
                output.writeUTF(this.getMediaFileSize(new File(entry.getValue())));                              //写入文件大小 ,这样子读取的时候就不需要再计算
            }
        } catch (Exception ex) {
            /*处理*/
            //ex.printStackTrace();
        }
    }

    /**
     * 保存临时歌曲信息的映射表内容到特定路径下的dat文件
     *
     * @param targetPath 数据文件的路径
     * @see #mediaFilesInfo_other
     */
    public void saveMediaFilesInfo_other(String targetPath) {

        Set<Map.Entry<String, String>> mediaFilesSet = this.mediaFilesInfo_other.entrySet();  //转换为可遍历的集
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(targetPath))) {
            for (Map.Entry<String, String> entry : mediaFilesSet) {
                output.writeUTF(entry.getKey());                                                                                            //歌曲名$歌手名
                output.writeUTF(entry.getValue());                                                                                       //音乐文件绝对路径
                output.writeUTF(this.getMediaFileSize(new File(entry.getValue())));                              //写入文件大小 ,这样子读取的时候就不需要再计算
            }
        } catch (Exception ex) {
            /*处理*/
            //ex.printStackTrace();
        }
    }

    /**
     * 获取歌曲文件大小
     *
     * @param mediaFile 获取大小的歌曲文件
     * @return 文件大小（XXMB）
     */
    public String getMediaFileSize(File mediaFile) {
        DecimalFormat df = new DecimalFormat("######0.0");    //保留两位小数
        double size = (double) mediaFile.length() / (1024 * 1024);
        return df.format(size) + "MB";
    }

    /**
     * 事件处理器 点击新建歌单按钮时弹出窗口
     *
     * @author 陈佳炜
     */
    public class promptInputHandler implements EventHandler<ActionEvent> {

        private popInput1 input;                            //      填写新建歌单名的面板
        private Stage stage;
        private Scene scene;

        public promptInputHandler() {
            this.stage = new Stage();
            this.stage.setAlwaysOnTop(true);                        //始终在其他窗体之上
            this.input = new popInput1(this.stage);
            this.scene = new Scene(input);
            this.stage.setScene(scene);
        }

        @Override
        public void handle(ActionEvent e) {
            this.input.clearTextField();
            this.stage.show();
        }
    }

    /**
     * 新建歌单的方法 新增歌单并显示。并且保存歌单的音乐数据文件的地址，新建歌单的创建日期，新建歌单的描述
     *
     * @param title 新建歌单的歌单标题
     * @see #importCreatedList()
     * @see #selectedlistButton
     */
    public void addSongList(String title) {
        
        Date createdDate = new Date();
        Calendar calendar = new GregorianCalendar();                                        //      保存歌单的创建日期
        Button bt = new Button(title);                                                                      //      歌单按钮

        ImageView songListIcon = new ImageView(new Image("image/songIcon.png"));                  //      为每个歌单按钮新建一个歌单图标
        songListIcon.setFitHeight(20);
        songListIcon.setFitWidth(20);
        bt.setGraphic(songListIcon);
        bt.setAlignment(Pos.BOTTOM_LEFT);
        bt.setGraphicTextGap(0);

        /**
         * 将歌单数据导入listInterfacePanel
         *
         * @see #listInterface
         */
        ImageView ListMenuIcon = new ImageView(new Image("image/songIcon.png"));    //歌单图标
        ListMenuIcon.setFitHeight(20);
        ListMenuIcon.setFitWidth(20);
        MenuItem item = new MenuItem(title, ListMenuIcon);
        this.listInterface.getSonglistMenus().add(item);
        this.listInterface.loadSonglistMenus();           //将歌单菜单装入右键菜单

        bt.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.SECONDARY) {
                this.selectedlistButton = bt.getText();                                                                                 //      设置当前被选中的歌单按钮
                songlistMenu.show(this, me.getScreenX(), me.getScreenY());
            } else {
                songlistMenu.hide();
            }
        });

        /*歌单点击事件，被点击时更换样式*/
        bt.setOnAction(e -> {
            this.displaySonglist(bt);
            this.currentClickedButton.setStyle(null);
            bt.setStyle("-fx-background-radius: 10; -fx-border-color: #334db3; -fx-border-radius: 10;  -fx-padding: 4 7 4 7;-fx-background-color: #8099ff;");
            this.currentClickedButton = bt;                             //      设置当前选中的按钮, 以便在下次其他按钮被点击时清空样式
        });     //绑定事件处理器  显示该歌单

        songListBts.add(bt);
        songListsInfo.put(title, "src/data/" + title + "_ListMediaInfo.dat");     //歌单名到歌单数据文件绝对路径的映射表

        bt.getStyleClass().add("createdListBt");
        VBox.setMargin(bt, new Insets(2, 2, 2, 2));
        this.songListsContainer.getChildren().add(bt);
        saveCreatedList();
        /*保存新建的歌单数据（追加）*/
        this.saveCurrentList(title);
        this.songListPane.setExpanded(true);
    }

    /**
     * 导入已创建的歌单并添加到侧边栏上
     *
     * @see #addSongList(java.lang.String) 
     */
    public void importCreatedList() {
        String sourcePath = "src/data/songListInfo.dat";                                   //       保存歌单数据的数据文件的路径
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(sourcePath))) {
            while (input.available() != 0) {
                String title = input.readUTF();
                this.addSongList(title);
            }
        } catch (IOException ex) {
            //System.out.println("歌单列表为空");
        }
    }

    /**
     * 保存当前歌单到特定路径
     *
     * @param title 歌单标题
     */
    public void saveCurrentList(String title) {
        String targetPath = "src/data/";     //特定路径
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(targetPath + title + "_ListMediaInfo.dat"))) {

        } catch (Exception ex) {
            /*处理*/
            //ex.printStackTrace();
        }
    }

    /**
     * 显示我喜欢的音乐
     */
    public class displayFavoriteListHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {
                String path = "src/data/myFavoriteSonglist.dat";
                File dat = new File("src/data/myFavoriteSonglist.dat");
                if (!dat.exists()) {
                    dat.createNewFile();
                } else {
                    mediaFilesInfo_current.clear();                        //清空先前歌单的数据 
                    listInterface.loadMediaFilesInfo(path);
                    listInterface.setTitleLabel("我喜欢的音乐");                                          //设置当前面板的标题为歌单标题
                    //listInterface.getInfoPane().setImageView(new ImageView(new Image("image/Cover2.png")));         //      更换歌单面板的图片
                    listInterface.getInfoPane().getImageView().setImage(new Image("image/Cover2.png"));                       //      更换歌单面板的图片
                    listInterface.getInfoPane().getDescription().setText("描述：这个是我喜欢的音乐歌单。");                       //      更换歌单面板的歌单信息
                    loadMediaFilesInfo_current(path);      //保存当前歌单的信息到映射表
                }
            } catch (Exception ex) {   //       如果我喜欢的音乐的数据文件不存在，即创建一个空的数据文件

            } finally {
                /*更换按钮样式*/
                currentClickedButton.setStyle(null);
                favoriteBt.setStyle("-fx-border-color: #334db3; -fx-background-color: linear-gradient(#8099ff, #4d66cc);");
                currentClickedButton = favoriteBt;                             //设置当前选中的按钮
            }
        }

    }


    /*输入新建歌单的名称的弹出面板  内部类*/
    public class popInput1 extends GridPane {

        private Label lbl = new Label("请输入新歌单标题：");
        private TextField titleInput = new TextField();
        private Label tipLbl = new Label("");
        private Button submitBt = new Button("确认");
        private Button cancelBt = new Button("取消");
        private Stage stage;                                                                 //         新建歌单面板的Stage

        public popInput1(Stage stage) {
            this.initialize();
            this.stage = stage;
            stage.getIcons().add(new Image("image/promptIcon.png"));
            stage.setResizable(false);
        }

        public void clearTextField() {
            this.titleInput.setText("");                                                         //清空
        }

        public String getTitle() {
            return this.titleInput.getText();
        }

        /*初始化方法*/
        private void initialize() {
            this.setPrefSize(300, 150);
            this.setAlignment(Pos.CENTER);
            this.setPadding(new Insets(10, 10, 10, 10));

            HBox btPane = new HBox(this.submitBt, this.cancelBt);
            Tooltip tip = new Tooltip("请给歌单起个名字");                               //未输入名字时的提示符

            this.titleInput.setTooltip(tip);

            btPane.setAlignment(Pos.CENTER);
            btPane.setSpacing(60);

            GridPane.setMargin(this.lbl, new Insets(10, 10, 10, 10));
            GridPane.setMargin(this.tipLbl, new Insets(10, 10, 10, 10));
            GridPane.setMargin(btPane, new Insets(30, 10, 10, 10));

            this.add(this.lbl, 0, 0);
            this.add(this.titleInput, 0, 2);
            this.add(this.tipLbl, 0, 3);        //未输入名字时的提示
            this.add(btPane, 0, 4);

            /*提交*/
            this.submitBt.setOnAction(e -> {
                if (this.titleInput.getText().trim().length() == 0) {        //输入为空的情况
                    this.tipLbl.setText("歌单名不能为空");
                    this.tipLbl.setFont(Font.font("Verdana", FontWeight.LIGHT, FontPosture.ITALIC, 10));
                } else {
                    addSongList(this.titleInput.getText());
                    this.stage.close();
                }

            });

            /*取消*/
            this.cancelBt.setOnAction(e -> {
                this.stage.close();
            });

            /*回车提交*/
            this.titleInput.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {         //回车事件
                    if (this.titleInput.getText().trim().length() == 0) {        //输入为空的情况
                        this.tipLbl.setText("歌单名不能为空");
                        this.tipLbl.setFont(Font.font("Verdana", FontWeight.LIGHT, FontPosture.ITALIC, 16));
                    } else {
                        addSongList(this.titleInput.getText());
                        this.stage.close();
                    }
                }
            });

        }
    }

    /**
     * 保存已创建的所有歌单
     */
    public void saveCreatedList() {
        String targetPath = "src/data/songListInfo.dat";
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(targetPath))) {
            for (Button songlistBt : songListBts) {
                output.writeUTF(songlistBt.getText());
            }
        } catch (Exception ex) {
            /*处理*/
            //ex.printStackTrace();
        }
    }


    /*歌单按钮的事件处理器（）
     public class songlistDisplayHandler implements EventHandler<ActionEvent> {

     private Button songlistBt;

     public songlistDisplayHandler(Button songlistBt) {
     this.songlistBt = songlistBt;
     }

     @Override
     public void handle(ActionEvent e) {

     System.out.println("打开 " + this.songlistBt.getText());
     }
     }*/
    /**
     * 显示其他歌单的播放列表
     *
     * @param bt 其他歌单的歌单按钮
     */
    public void displaySonglist(Button bt) {
        String target = "src/data/";
        this.mediaFilesInfo_current.clear();                        //清空先前歌单的数据 
        this.listInterface.loadMediaFilesInfo(target + bt.getText() + "_ListMediaInfo.dat");
        this.listInterface.setTitleLabel(bt.getText());                                          //设置当前面板的标题为歌单标题
        listInterface.getInfoPane().getImageView().setImage(new Image("image/Cover1.png"));                       //      更换歌单面板的图片
        listInterface.getInfoPane().getDescription().setText("描述：这个是" + bt.getText() + "歌单");                       //      更换歌单面板的歌单信息
        this.loadMediaFilesInfo_current(target + bt.getText() + "_ListMediaInfo.dat");      //保存当前歌单的信息到映射表

    }

    /**
     * 显示本地音乐的事件处理器
     *
     * @author 陈佳炜
     */
    public class displayAllMusicHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            mediaFilesInfo_current.clear();                        //清空先前歌单的数据 
            listInterface.loadMediaFilesInfo("src/data/mediaFilesInfo.dat");
            listInterface.setTitleLabel("本地音乐");
            listInterface.getInfoPane().getImageView().setImage(new Image("image/Cover.png"));                       //      更换歌单面板的图片
            listInterface.getInfoPane().getDescription().setText("描述：这个是本地音乐歌单，保存了所有你导入的本地音乐。");                       //      更换歌单面板的歌单信息
            loadMediaFilesInfo_current("src/data/mediaFilesInfo.dat");      //保存当前歌单的信息到映射表
            currentClickedButton.setStyle(null);
            localMusicbt.setStyle("-fx-background-radius: 8; -fx-border-color: #334db3; -fx-border-radius: 8; -fx-background-color: linear-gradient(#8099ff, #4d66cc);");
            currentClickedButton = localMusicbt;                             //设置当前选中的按钮
        }
    }

    /**
     * 根据歌单名删除歌单的方法
     *
     * @param songlistName 歌单名
     * @see #songListBts
     */
    public void deleteSongList(String songlistName) {
        File songlistFile = new File("src/data/" + songlistName + "_ListMediaInfo.dat");
        songlistFile.delete();              //删除歌单文件
        for (Button songlistBt : this.songListBts) {
            if (songlistBt.getText().equals(songlistName)) {
                System.out.println(songlistBt.getText() + "已删除");
                // songlistBt.setVisible(false);                           //设置为不可见
                this.songListBts.remove(songlistBt);                 //删除songListInfo中的歌单事件
                this.songListsContainer.getChildren().remove(songlistBt);               //删除对应的按钮节点
                break;
            }
        }
        this.saveCreatedList();                     //保存新的歌单数据
        this.listInterface.deleteListMenu(songlistName);//删除播放列表界面中右键菜单中相应的歌单菜单

    }

}
