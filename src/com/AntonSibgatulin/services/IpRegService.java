package com.AntonSibgatulin.services;

public class IpRegService {
    public static final int MAX_TIME = 1000*60*5;

    public static final int MAX_COUNT_LOGIN_BEFORE_CAPTCHA = 5;

    public boolean captcha = false;

    public static final int MAX_COUNT = 5;
    public String ip = null;
    public int tryLogin = 0;

    public int countReg = 0;
    public int check_reg= 0;

    public int countExecute = 0;
    public long lastTime = System.currentTimeMillis();
    public CaptchaModel captchaModel=null;

    public IpRegService(){

    }
    public IpRegService(String ip){
        this.ip = ip;

    }

}
