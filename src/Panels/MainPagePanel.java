/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Panels;

import java.io.File;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 完成各面板组装的Oxygen音乐播放器面板 完成了各面板间的逻辑组织
 *
 * @author 陈佳炜
 * @author 陈小龙
 * @author 陈康
 * @version 1.0
 */
public class MainPagePanel extends BorderPane {

    private Stage primaryStage;
    private HeaderPanel header;
    private MediaControlPanel controlPane = new MediaControlPanel();
    private ListInterfacePanel playlistPane = new ListInterfacePanel(this.controlPane);             //      先初始化播放界面
    private SideBarPanel sidebar = new SideBarPanel(this.playlistPane);                                      //     再初始化侧边栏？？

    public MainPagePanel() {
        this.initialize();
        /*在这里设置播放列表的第一首歌曲，因为要等所有的面板都初始化完成后设置才比较安全*/
    }

    public MainPagePanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.header = new HeaderPanel(this.primaryStage);       //只有primaryStage赋值后才能生成这个
        this.controlPane.setSideBarPanel(sidebar);                      //循环传递数据         //最后控制面板再绑定侧边栏                                                                 
        this.initialize();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void initialize() {

        /*设置BorderPane的属性*/
        this.setPrefSize(1400, 900);
        this.setStyle("-fx-border-color:#1a0068 ; -fx-border-width: 2px;");    //能否实现边框阴影                          
        this.setOnKeyPressed(e -> {                                                                                                         //响应键盘事件 控制播放开关

            if (e.getCode() == KeyCode.CONTROL) {                       // 播放/暂停
                if (this.controlPane.isPlaying()) {
                    this.controlPane.defaultPause();
                } else {
                    this.controlPane.defaultPlay();
                }
            } else if (e.getCode() == KeyCode.RIGHT) {                          //播放下一首
                this.controlPane.defaultNext();
            } else if (e.getCode() == KeyCode.LEFT) {                           //播放上一首
                this.controlPane.defaultPre();
            } else if (e.getCode() == KeyCode.UP) {                             //提高音量
                this.controlPane.regulateVolume(true);
            } else if (e.getCode() == KeyCode.DOWN) {                   //降低音量
                this.controlPane.regulateVolume(false);
            }

        });
        //设置组合键
        this.setOnKeyTyped(e -> {

        });
        
        this.controlPane.PrimarySong$intialize();
        
        this.setTop(this.header);
        this.setCenter(this.playlistPane);
        this.setBottom(this.controlPane);
        this.setLeft(this.sidebar);
    }

}
