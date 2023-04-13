package com.AntonSibgatulin.services;

import com.AntonSibgatulin.server.Server;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterService extends Thread {
    public HashMap<String, IpRegService> ipRegServiceHashMap = new HashMap<>();
    public ArrayList<IpRegService> ipRegServices = new ArrayList<>();
    public Server server = null;

    public RegisterService(Server server) {
        this.server = server;
        start();

    }


    public IpRegService getOrCreate(RegisterModel registerModel) {

        if(registerModel.connection==null || registerModel.connection.isClosed()){

            return null;
        }
        // String ip = registerModel.connection.getRemoteSocketAddress().getHostName().toString();
        String ip = registerModel.connection.getRemoteSocketAddress().toString().split("/")[1].split(":")[0];

        if (ipRegServiceHashMap.get(ip) == null) {
            IpRegService ipRegService = new IpRegService(ip);
            ipRegServiceHashMap.put(ip, ipRegService);
            ipRegServices.add(ipRegService);
            return ipRegService;
        } else {
            return ipRegServiceHashMap.get(ip);
        }
    }





    public IpRegService getOrCreate(WebSocket connection) {

        if(connection==null || connection.isClosed()){

            return null;
        }
        // String ip = registerModel.connection.getRemoteSocketAddress().getHostName().toString();
        String ip = connection.getRemoteSocketAddress().toString().split("/")[1].split(":")[0];

        if (ipRegServiceHashMap.get(ip) == null) {
            IpRegService ipRegService = new IpRegService(ip);
            ipRegServiceHashMap.put(ip, ipRegService);
            ipRegServices.add(ipRegService);
            return ipRegService;
        } else {
            return ipRegServiceHashMap.get(ip);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                ArrayList<IpRegService> ipRegServices = (ArrayList<IpRegService>) this.ipRegServices.clone();

                for (int i = 0; i < ipRegServices.size(); i++) {
                    IpRegService ipRegService = ipRegServices.get(i);
                    if (System.currentTimeMillis() - ipRegService.lastTime >= IpRegService.MAX_TIME) {
                        this.ipRegServices.remove(ipRegService);
                        continue;
                    }

                   // System.out.println("Iteration "+ipRegService);

                    if (ipRegService.countExecute >= RegisterModel.MAX_REG_CHECK_CRITICAL) {
                        server.blackIp.put(ipRegService.ip, ipRegService.countReg);
                       System.out.println("Blockin' "+ipRegService.ip);
                        this.ipRegServices.remove(ipRegService);
                        this.ipRegServiceHashMap.remove(ipRegService);
                        server.closeConnectionIp(ipRegService.ip);
                        continue;
                    }


                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
