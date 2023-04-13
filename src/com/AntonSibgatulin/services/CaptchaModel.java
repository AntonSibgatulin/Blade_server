package com.AntonSibgatulin.services;

import org.java_websocket.WebSocket;

public class CaptchaModel {
    public static final int MAX_TIME_LIVE = 1000*60*3;
    public static final int MAX_COUNT_ENTER_CAPTCHA = 5;

    public String imageInBase64 = null;
    public long time = 0;
    public WebSocket connection = null;
    public int countEnter = 0;
    public CaptchaGenerateModel generateModel = null;

    public void init(){
        generateModel = new CaptchaGenerateModel();
        this.imageInBase64 = generateModel.Generate(6);
        send("game;captcha;"+imageInBase64);
    }

    public void send(String message) {

        if (connection != null && connection.isClosed() == false && connection.isClosing() == false)
            connection.send(message);
    }
public boolean enter(String text){
        if(text.equals(generateModel.line)){

            return true;
        }
        else{
            this.imageInBase64 = generateModel.Generate(6);
            send("game;noncaptcha");
            send("game;captcha;"+imageInBase64);
            return false;
        }

}
    public CaptchaModel(WebSocket webSocket){

        this.connection = webSocket;
        init();
    }
}
