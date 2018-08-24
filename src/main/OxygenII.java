/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Panels.MainPagePanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author chenjiawei
 */
public class OxygenII extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainPagePanel main = new MainPagePanel(primaryStage);
        Scene scene = new Scene(main);
        primaryStage.getIcons().add(new Image("image/OxygenOriginIcon.png"));  //设置图标
        primaryStage.initStyle(StageStyle.UNDECORATED);           //隐藏系统窗口
        primaryStage.setResizable(false);                                                           //  固定尺寸
        primaryStage.setTitle("OriginII");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
