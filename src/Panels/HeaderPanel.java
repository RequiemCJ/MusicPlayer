/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Panels;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * 播放器头部面板 包含 音乐播放器图标、音乐播放器名、设置按钮、皮肤按钮 最小化按钮、最大化按钮、关闭按钮
 *
 * @author 陈佳炜
 * @author 陈康
 * @author 陈小龙
 */
public class HeaderPanel extends HBox {

    private ImageView Logo = new ImageView(new Image("image/OxygenOriginIcon.png"));               //       Logo图标
    private Label logoLbl = new Label("OxygenII");
    private Button appearanceBt = new Button("");                                                                                       //      皮肤按钮
    private Button settingBt = new Button("");                                                                          //      设置按钮
    private Button minimizeBt = new Button("");                                                                     //      最小化按钮
    private Button maximizeBt = new Button("");                                                                     //      最大化按钮
    private Button closeBt = new Button("");                                                                            //      关闭按钮
    private Stage primaryStage;                                                                                                 //      primaryStage
    private ColorPicker colorPicker = new ColorPicker();                                                        //      颜色选择器

    public HeaderPanel() {
        this.initialize();
    }

    public HeaderPanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.initialize();
    }
    
    /**
     * 头部面板的初始化方法
     */
    private void initialize() {

        /*设置面板属性*/
        this.setPrefSize(830, 60);
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setStyle("-fx-background-color: #1a0068");
        this.getChildren().addAll(this.logoLbl, this.appearanceBt, this.colorPicker, this.settingBt, this.minimizeBt, this.maximizeBt, this.closeBt);

        EventHandler handler = new DragWindowHandler(this.primaryStage);
        this.setOnMousePressed(handler);
        this.setOnMouseDragged(handler);

        this.minimizeBt.setOnAction(e -> {                  //最小化窗口
            this.primaryStage.setIconified(true);
        });
        this.closeBt.setOnAction(e -> {                         //关闭窗口
            this.primaryStage.close();
        });
        this.settingBt.setOnAction(new promptStPaneHandler());

        ImageView appearanceIcon = new ImageView(new Image("image/tshirtIcon.png"));
        ImageView settingIcon = new ImageView(new Image("image/settingIcon.png"));
        ImageView minusIcon = new ImageView(new Image("image/minusIcon.png"));
        ImageView squareIcon = new ImageView(new Image("image/squareIcon.png"));
        ImageView crossIcon = new ImageView(new Image("image/crossIcon.png"));
        this.Logo.setFitHeight(35);
        this.Logo.setFitWidth(35);
        appearanceIcon.setFitHeight(20);
        appearanceIcon.setFitWidth(20);
        settingIcon.setFitHeight(20);
        settingIcon.setFitWidth(20);
        minusIcon.setFitHeight(20);
        minusIcon.setFitWidth(20);
        squareIcon.setFitHeight(20);
        squareIcon.setFitWidth(20);
        crossIcon.setFitHeight(20);
        crossIcon.setFitWidth(20);
        this.appearanceBt.setGraphic(appearanceIcon);
        this.minimizeBt.setGraphic(minusIcon);
        this.maximizeBt.setGraphic(squareIcon);
        this.closeBt.setGraphic(crossIcon);
        this.logoLbl.setGraphic(this.Logo);
        this.logoLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        this.logoLbl.setTextFill(Color.ALICEBLUE);
        //this.colorPicker.set
        //this.appearanceBt.setText("#334db3");
        this.settingBt.setGraphic(settingIcon);

        this.getStylesheets().add("css/HeaderPanel.css");   //添加级联样式表

        this.colorPicker.setOnAction(new modifyInterfaceHandler(this));
        //this.colorPicker.setEffect(null);
        //this.colorPicker.setStyle("-fx-skin: CustomSkin");
        HBox.setMargin(this.logoLbl, new Insets(0, 900, 0, 0));
        HBox.setMargin(this.appearanceBt, new Insets(0, 0, 0, 5));
        HBox.setMargin(this.colorPicker, new Insets(0, 0, 0, 5));
        HBox.setMargin(this.settingBt, new Insets(0, 20, 0, 5));
        HBox.setMargin(this.minimizeBt, new Insets(0, 0, 0, 5));
        HBox.setMargin(this.maximizeBt, new Insets(0, 0, 0, 5));
        HBox.setMargin(this.closeBt, new Insets(0, 0, 0, 5));

    }

    /**
     * 修改皮肤的事件处理器
     *
     * @author 陈佳炜
     */
    public class modifyInterfaceHandler implements EventHandler<ActionEvent> {

        private HeaderPanel header;    //被修改的面板

        public void setHeader(HeaderPanel header) {
            this.header = header;
        }

        public modifyInterfaceHandler(HeaderPanel header) {
            this.setHeader(header);
        }

        @Override
        public void handle(ActionEvent e) {
            String color = "#" + colorPicker.getValue().toString().substring(2, 8);    //转化   出现一些颜色无法应用的情况
            System.out.println(color);
            header.setStyle("-fx-background-color:" + color);
        }
    }

    /** 
     * 窗口拖动的事件处理器，隐藏了操作系统的窗口，实现了自定义窗口响应鼠标拖动事件
     * @author 陈佳炜
     * @author 陈康
     * @author 陈小龙
     */
    public class DragWindowHandler implements EventHandler<MouseEvent> {

        private Stage primaryStage;
        private double oldStageX = 0;
        private double oldStageY = 0;
        private double oldScreenX;
        private double oldScreenY;

        public DragWindowHandler(Stage primaryStage) {
            this.primaryStage = primaryStage;
        }

        @Override
        public void handle(MouseEvent e) {
            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {    //鼠标按下的事件    获取SceneX 和SceneY
                this.oldStageX = this.primaryStage.getX();
                this.oldStageY = this.primaryStage.getY();
                this.oldScreenX = e.getScreenX();
                this.oldScreenY = e.getScreenY();

            } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {  //鼠标拖动的事件
                this.primaryStage.setX(e.getScreenX() - this.oldScreenX + this.oldStageX);
                this.primaryStage.setY(e.getScreenY() - this.oldScreenY + this.oldStageY);
            }
        }

        public void enableDrag(Node node) {
            node.setOnMousePressed(this);
            node.setOnMouseDragged(this);
        }
    }
    
    /**
     * 设置面板
     * @author 陈佳炜
     * @version 0.5
     */
    public class SettingPanel extends GridPane {
        private Label lbl1 = new Label("本地目录：");
        private TextField pathOfLocalFile = new TextField("");                      //      显示本地音乐文件夹路径
        private Button bt = new Button("更改目录");                                                     //       设置本地音乐文件夹
        private Stage stage;                                                                                //      设置面板的Stage
        
        public SettingPanel(Stage stage) {
            this.initialize();
            this.stage = stage;
            stage.getIcons().add(new Image("image/settingIcon.png"));
            stage.setResizable(false);
        }
        
        private void initialize() {
            this.setPrefSize(400, 250);
            this.setAlignment(Pos.TOP_CENTER);
            this.setPadding(new Insets(10, 10, 10, 10));
            this.add(this.lbl1, 0, 2);
            this.add(this.pathOfLocalFile, 1, 2);
            this.add(this.bt, 2, 2);
            
            GridPane.setMargin(this.pathOfLocalFile, new Insets(5, 5, 5, 5));
            GridPane.setMargin(this.bt, new Insets(5, 5, 5, 5));
        }
    }
    
    /**
     * 弹出设置面板的事件处理器
     * @author 陈佳炜
     */
    public class promptStPaneHandler implements EventHandler<ActionEvent> {
        private SettingPanel stPane;                            //      填写新建歌单名的面板
        private Stage stage;
        private Scene scene;
        
        public promptStPaneHandler() {
            this.stage = new Stage();
            this.stage.setAlwaysOnTop(true);                        //始终在其他窗体之上
            this.stPane = new SettingPanel(this.stage);
            this.scene = new Scene(this.stPane);
            this.stage.setScene(scene);
        }
        
        @Override
        public void handle(ActionEvent e) {
            this.stage.show();
        }
    }
    
}
