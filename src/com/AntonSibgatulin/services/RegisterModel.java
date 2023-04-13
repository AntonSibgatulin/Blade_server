package com.AntonSibgatulin.services;

import java.util.ArrayList;

import com.AntonSibgatulin.Players.Information;
import com.AntonSibgatulin.server.Server;
import org.java_websocket.WebSocket;

import com.AntonSibgatulin.user.User;

import com.AntonSibgatulin.database.DatabaseModel;

public class RegisterModel extends Thread {
    public boolean active = true;

    public Server server = null;

    public static final int MAX_TIME_OF_LIVIN_THREAD_OF_REGISTER = 1000 * 60 * 3;
    public static final int MAX_REG_CHECK = 5;
    public static final int MAX_REG_CHECK_CRITICAL = 25;
    public static final int MAX_REG = 5;
    public long time = System.currentTimeMillis();
    public WebSocket connection = null;
    public ArrayList<String> commands_check = new ArrayList<>();
    public ArrayList<String> register = new ArrayList<>();
    public DatabaseModel databaseModel = null;

    String ip = null;

    public RegisterModel(WebSocket socket, Server server) {
        connection = socket;
         ip = socket.getRemoteSocketAddress().toString().split("/")[1].split(":")[0];

        //ip = connection.getRemoteSocketAddress().getHostName().toString();

        this.server = server;
        this.databaseModel = server.bDatabaseModel;
        start();

    }

    public void send(String message) {

        if (connection != null && connection.isClosed() == false && connection.isClosing() == false)
            connection.send(message);
    }

    @Override
    public void run() {
        while ( active) {
            if (System.currentTimeMillis() - time >= MAX_TIME_OF_LIVIN_THREAD_OF_REGISTER) {
                break;

            }

            try {
                Thread.sleep(1000);
                if (commands_check.size() >= MAX_REG_CHECK) {
                    ban();
                    break;
                }
                if (commands_check.size() >= MAX_REG_CHECK_CRITICAL) {
                    ban();
                    break;
                }
                if (register.size() >= MAX_REG) {
                    break;
                }

                IpRegService ipRegService = server.registerService.getOrCreate(this);
                if (ipRegService == null||commands_check.size() >= MAX_REG_CHECK_CRITICAL) {
                    ban();
                }

                if (commands_check.size() > 0) {
                    String string = commands_check.get(0);
                    execute(string);
                    commands_check.remove(0);
                }
                if (register.size() > 0) {
                    String string = register.get(0);
                    execute(string);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void banForever() {
        server.closeConnectionIp(ip);
        server.blackIp.put(ip, 100);
        connection.close();

    }
    public void ban() {
      /*  IpRegService ipRegService = server.registerService.getOrCreate(this);
        if (ipRegService == null||ipRegService.check_reg>=MAX_REG_CHECK) {
           ipRegService.captcha=true;
           ipRegService.captchaModel = new CaptchaModel(connection);
        }

       */
        //server.blackIp.put(ip, 100);
        IpRegService ipRegService = server.registerService.getOrCreate(this);
        if (ipRegService != null) {
            ipRegService.captcha=true;
            ipRegService.captchaModel = new CaptchaModel(connection);
        }
        server.closeConnectionIp(ip);

        connection.close();


    }

    public boolean checkValidatePassword(String password) {
      //  String reg = "[a-zA-Z]\\w{1,32}";
        String reg = "[a-zA-Z0-9]\\w{1,32}";

        // "[a-z]+"
        if (password.length() <= 32 && password.matches(reg)) {
            return true;
        }

        return false;

    }

    public boolean checkValidateLogin(String login) {
        String reg = "[a-zA-Z0-9]\\w{1,32}";
        // "[a-z]+"
        if (login.length() <= 32 && login.matches(reg)) {
            return true;
        }

        return false;
    }

    public void execute(String str) {
        String[] split = str.split(";");
        if (split[0].equals("reg")) {
            String login = split[1];
            String password = split[2];
            if (checkValidateLogin(login)) {
                if (checkValidatePassword(password)) {
                    IpRegService ipRegService = server.registerService.getOrCreate(this);

                    if (!databaseModel.isExist(login)) {
                        if (ipRegService != null && ipRegService.countReg < IpRegService.MAX_COUNT) {
                            User user = new User(login, password);
                            user.setRang(0);
                            user.setmoney(300);
                            user.setScore(0);
                            user.setMagicCards("{}");
                            user.setLocale("ru");
                            user.setBox(0);

                            Information information = new Information(user.id);
                            Anticheat anticheat = new Anticheat();
                            user.info = information;
                            user.anticheat = anticheat;
                            databaseModel.regUser(user);
                            active = false;
                            ipRegService.lastTime = System.currentTimeMillis();
                            ipRegService.countReg++;
                            ipRegService.check_reg--;
                            ipRegService.countExecute=0;
                            if(ipRegService.check_reg<=0){

                                ipRegService.check_reg=0;

                            }
                            server.registerModelHashMap.remove(this);


                            send("reg;in");
                        } else {
                            ban();
                        }

                    } else {
                        send("reg;exist");
                       // active = false;
                    }
                } else {
                    send("reg;password_unvalidate");
                   // active = false;
                }
            } else {

                send("reg;login_unvalidate");
               // active = false;
            }
        }
    }

    public void add(String s) {
        IpRegService ipRegService = server.registerService.getOrCreate(this);
        if (ipRegService == null) {
            ban();
        }
        ipRegService.check_reg ++;

        this.commands_check.add(s);
    }
}
