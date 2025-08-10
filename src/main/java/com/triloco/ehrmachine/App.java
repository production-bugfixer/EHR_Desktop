package com.triloco.ehrmachine;

import com.triloco.ehrmachine.applicationManager.CacheManager;
import com.triloco.ehrmachine.applicationManager.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    
    private final ScreenManager manager=ScreenManager.getInstance();

    @Override
    public void start(Stage stage) {
        manager.initialize(stage);
    }

    public static void main(String[] args) {
        CacheManager.loadCache();
        launch();
    }

    @Override
    public void stop() throws Exception {
        CacheManager.saveCache();
        super.stop();
    }
    
}
