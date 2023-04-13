package com.AntonSibgatulin.server;

import java.awt.Point;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.BreathLoader;
import com.AntonSibgatulin.Players.HouseLoader;
import com.AntonSibgatulin.Players.IBreath;
import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.Players.PlayersLoader;
import com.AntonSibgatulin.Players.stuff.StuffLoader;
import com.AntonSibgatulin.database.DatabaseModel;
import com.AntonSibgatulin.location.LocationLoader;
import com.AntonSibgatulin.location.LocationModel;
import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.TaskManager;
import com.AntonSibgatulin.location.TaskModel;
import com.AntonSibgatulin.location.generation.MapGeneration;
import com.AntonSibgatulin.services.CaptchaModel;
import com.AntonSibgatulin.services.IpRegService;
import com.AntonSibgatulin.services.RegisterModel;
import com.AntonSibgatulin.services.RegisterService;
import com.AntonSibgatulin.shop.ShopModel;
import com.AntonSibgatulin.user.User;

public class Server extends WebSocketServer {
    public int random_map = 0;

    public HashSet<WebSocket> conn = new HashSet<>();
    public HashMap<WebSocket, RegisterModel> registerModelHashMap = new HashMap<>();

    public HashMap<WebSocket, User> users = new HashMap<>();

    public HashMap<String, ArrayList<WebSocket>> webSocketArrayListIp = new HashMap<>();
    public ArrayList<LocationModel> battles = new ArrayList<>();
    public HashMap<String, Integer> blackIp = new HashMap<>();

    public RegisterService registerService = new RegisterService(this);

    public String space = ";";
    public DatabaseModel bDatabaseModel = new DatabaseModel();

    public PlayersLoader playersLoader = null;
    public LocationLoader locationLoader = null;
    public BreathLoader breathLoader = null;
    public TaskManager taskManager = null;
    public HouseLoader houseLoader = null;
    public StuffLoader stuffLoader = null;
    public ShopModel shopModel = null;

    public HashMap<String, LocationModel> battlesMaps = new HashMap<>();

    public Server(String host, int port, PlayersLoader playersLoader, BreathLoader breathLoader,
                  LocationLoader locationLoader, TaskManager taskManager, HouseLoader houseLoader, StuffLoader stuffLoader,
                  ShopModel shopModel) {
        super(new InetSocketAddress(host, port));

        this.playersLoader = playersLoader;
        this.locationLoader = locationLoader;
        this.breathLoader = breathLoader;
        this.taskManager = taskManager;
        this.houseLoader = houseLoader;
        this.stuffLoader = stuffLoader;
        this.shopModel = shopModel;
        super.setTcpNoDelay(true);
        System.out.println("Server was started on " + host + ":" + port);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        // String ip =
        // webSocket.getRemoteSocketAddress().getHostName().toString();

        String ip = webSocket.getRemoteSocketAddress().toString().split("/")[1].split(":")[0];

        if (blackIp.get(ip) != null) {
            webSocket.close();
            return;
        }
        ArrayList<WebSocket> object = webSocketArrayListIp.get(ip);
        if (object == null) {
            ArrayList<WebSocket> webSockets = new ArrayList<>();
            webSockets.add(webSocket);
            webSocketArrayListIp.put(ip, webSockets);
        } else {

            object.add(webSocket);

        }
        this.conn.add(webSocket);
        checkCaptchaCreate(webSocket);
        // String ip =
        // webSocket.getRemoteSocketAddress().toString().split("/")[1].split(":")[0];

        System.out.println("Socket opened " + ip);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        this.conn.remove(webSocket);
        String ip = webSocket.getRemoteSocketAddress().toString().split("/")[1].split(":")[0];
        ArrayList<WebSocket> object = webSocketArrayListIp.get(ip);
        if (object == null) {

        } else {

            object.remove(webSocket);
            if (object.size() == 0) {
                webSocketArrayListIp.remove(object);
            }
        }
        System.out.println("Socket closed " + ip);// ().getHostName().toString());
        User user = null;
        if ((user = users.get(webSocket)) != null && user.login != null) {
            if (user.playerController != null) {
                if (user.playerController.locationModel != null) {
                    user.playerController.locationModel.removeUser(user);
                    user.playerController.locationModel = null;
                }
                user.playerController.t.stop();
            }
            bDatabaseModel.updateUser(user);
            bDatabaseModel.cache.remove(user.login);

        }
        users.remove(webSocket);
        System.gc();
    }

    public void closeConnectionIp(final String ip) {

        if (ip == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<WebSocket> object = webSocketArrayListIp.get(ip);
                if (object != null) {
                    webSocketArrayListIp.remove(object);
                    object = (ArrayList<WebSocket>) object.clone();
                    ArrayList<WebSocket> webSockets = new ArrayList<>();
                    for (int i = 0; i < webSockets.size(); i++) {
                        webSockets.get(i).close();
                    }

                }
            }
        }).start();

    }

    public void sendEverybody(String msg) {
        for (WebSocket sock : conn) {
            sock.send(msg);
        }
    }

    public boolean checkCaptcha(WebSocket webSocket) {
        IpRegService ipRegService = registerService.getOrCreate(webSocket);

        if (ipRegService != null) {
            if (ipRegService.tryLogin >= IpRegService.MAX_COUNT_LOGIN_BEFORE_CAPTCHA) {
                ipRegService.captcha = true;
                ipRegService.captchaModel = new CaptchaModel(webSocket);
                return true;
            }
        }
        return false;

    }

    public boolean checkCaptchaCreate(WebSocket webSocket) {
        IpRegService ipRegService = registerService.getOrCreate(webSocket);

        if (ipRegService != null) {
            if (ipRegService.captcha) {
                ipRegService.captcha = true;
                ipRegService.captchaModel = new CaptchaModel(webSocket);
                return true;
            }
        }
        return false;

    }

    public boolean checkCaptchaDoesntNew(WebSocket webSocket) {
        IpRegService ipRegService = registerService.getOrCreate(webSocket);

        if (ipRegService != null) {
            if (ipRegService.captcha == true) {
                // ipRegService.captcha=true;

                // ipRegService.captchaModel = new CaptchaModel(webSocket);
                return true;
            }
        }
        return false;

    }

    @Override
    public void onMessage(final WebSocket socket, final String s) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // if (s.equals("lobby;ping") == false)
                // System.out.println("message from client " + s);

                // socket.send("Message recived");
                // System.out.println(s);

                String[] data = s.split(space);
                if(data.length>=2){
                    if(data[0].equals("lobby") && data[1].equals("ping")){
                        socket.send("lobby;ping");
                        return;
                    }
                }
                if (data.length > 0) {
                    if (checkCaptchaDoesntNew(socket)) {
                        if (data[0].equals("captcha")) {
                            IpRegService ipRegService = registerService.getOrCreate(socket);

                            if (ipRegService != null && ipRegService.captchaModel != null) {

                                if (ipRegService.captchaModel.enter(data[1])) {
                                    ipRegService.tryLogin = IpRegService.MAX_COUNT_LOGIN_BEFORE_CAPTCHA - 2;
                                    ipRegService.countReg = RegisterModel.MAX_REG - 2;
                                    ipRegService.check_reg = RegisterModel.MAX_REG_CHECK - 2;
                                    ipRegService.captcha = false;
                                    ipRegService.captchaModel = null;
                                    socket.send("game;captcha_fine");
                                    System.gc();
                                }

                            }
                        }

                        return;
                    }

                    User user = null;
                    if ((user = users.get(socket)) != null) {
                        if (user.userType > 0) {
                            if (data[0].equals("kick")) {
                                String login = data[1];
                                bDatabaseModel.cache.get(login).connection.close();

                            }
                            if (data[0].equals("ban")) {
                                String login = data[1];
                                User users = bDatabaseModel.cache.get(login);
                                String ip = users.connection.getRemoteSocketAddress().toString().split("/")[1].split(":")[0];

                                closeConnectionIp(ip);
                                blackIp.put(ip, 100);
                                users.connection.close();

                            }
                        }
                        if (data[0].equals("lobby")) {

                            if (data[1].equals("get_map_data")) {
                                JSONObject main = new JSONObject();
                                JSONArray jsonArray = new JSONArray();

                                for (Map.Entry<String, LocationModel> entry : battlesMaps.entrySet()) {
                                    LocationModel locationModel = entry.getValue();
                                    if (locationModel == null)
                                        continue;

                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("type", locationModel.type);
                                    jsonObject.put("maxPlayer", locationModel.maxPlayer);
                                    jsonObject.put("name", locationModel.name);
                                    jsonObject.put("id", locationModel.id);
                                    jsonObject.put("index", locationModel.maps.idint);
                                    jsonObject.put("des", locationModel.maps.description);
                                    jsonObject.put("count", locationModel.playerControllers.size());
                                    jsonObject.put("des_en", locationModel.maps.description_en);
                                    jsonObject.put("des_es", locationModel.maps.description_es);
                                    jsonObject.put("des_ja", locationModel.maps.description_jp);

                                    jsonArray.put(jsonObject);
                                }
                                main.put("maps", jsonArray);
                                user.send("lobby;maps;" + main.toString());

                            }

                            if (data[1].equals("create_map_multiplay")) {
                                if (System.currentTimeMillis() - user.anticheat.getTimeLastCreateMap() >= User.MAX_TIME_BETWEEN_CREATE_MAP) {
                                    user.anticheat.setCountCreatedMaps(0);

                                }
                                if (Server.this.locationLoader.arrayList.size() > 0 && user.anticheat.getCountCreatedMaps() < User.MAX_CREATE_MAP) {
                                    int index = new Random().nextInt(Server.this.locationLoader.arrayList.size());

                                    MapGeneration mapGeneration = new MapGeneration(
                                            Server.this.locationLoader.arrayList.get(index));

                                    /*
                                     * MapGeneration mapGeneration = new
                                     * MapGeneration(
                                     * Server.this.locationLoader.hashMap.get(
                                     * "way_of_slayer_3"));
                                     */
                                    random_map += 1;

                                    JSONObject setting = new JSONObject(s.replace("lobby;create_map_multiplay;", ""));

                                    String id = random_map + "@" + mapGeneration.hashCode() + "#"
                                            + new Random().nextInt();

                                    id = "" + random_map;

                                    setting.put("id", id);

                                    LocationModel locationModel = new LocationModel(id, mapGeneration, setting,
                                            Server.this, playersLoader.players);

                                    if (locationModel.type.equals("Cheater")) {
                                        System.out.println("Should get ban cuz it cheater");
                                    }
                                    setting.put("type", locationModel.type);
                                    setting.put("idBattle",
                                            locationModel.maps.category + "_" + locationModel.maps.idint);
                                    setting.put("index", locationModel.maps.idint);
                                    setting.put("maxPlayer", locationModel.maxPlayer);

                                    setting.put("des", locationModel.maps.description);
                                    setting.put("count", locationModel.playerControllers.size());
                                    setting.put("des_en", locationModel.maps.description_en);
                                    setting.put("des_es", locationModel.maps.description_es);
                                    setting.put("des_ja", locationModel.maps.description_jp);

                                    System.out.println(locationModel.maps.description + " description");
                                    // locationModel.mainThread.start();
                                    locationModel.timer.start();
                                    Server.this.battles.add(locationModel);
                                    Server.this.battlesMaps.put(locationModel.id, locationModel);
                                    user.anticheat.setTimeLastCreateMap(System.currentTimeMillis());
                                    user.anticheat.countCreatedMaps++;
                                    sendEverybody("lobby;addBattle;" + setting.toString());
                                }
                            }
                            if (data[1].equals("play_map_multiplay")) {
                                String team = null;
                                String id = data[2];
                                if (data.length >= 4) {
                                    team = data[3];
                                    if (team.equals("null") || team.equals("undefined")) {
                                        team = null;
                                    }
                                }

                                LocationModel model = Server.this.battlesMaps.get(id);
                                if (model == null)
                                    return;
                                int posX = 0;// Integer.valueOf(location.split(";")[1]);
                                int posY = 0;// Integer.valueOf(location.split(";")[2]);
                                if (posX == 0 && posY == 0) {
                                    posX = model.maps.px;
                                    posY = model.maps.py;
                                }
                                // PlayerController playerController =
                                // user.playerController;
                                PlayerController playerController = new PlayerController(user, new Point(posX, posY));
                                user.playerController = playerController;
                                if (model.addPlayer(user, team)) {
                                    user.playerController.locationModel = model;

                                    getPlayer(user);
                                    initPlayerController(user, 1920, 1080, model.maps);
                                    model.send_players(user);
                                }

                            }

                            if (data[1].equals("shop_init")) {
                                user.send("lobby;shop_init;" + shopModel.json);
                            }
                            if (data[1].equals("shop_buy")) {
                                String id = data[2];
                                if (id.split("_")[0].equals("players")) {
                                    String type = data[3];
                                    String player_id = shopModel.getId(id);
                                    Player player = playersLoader.players.get(player_id + "_" + 0);
                                    shopModel.buy(user, id, type, player);

                                    boolean checkPlayerCharacter = checkPlayerandSet(player_id, user);
                                    if (checkPlayerCharacter) {
                                        JSONObject jsonObject1 = new JSONObject(user.info.getPlayers());
                                        jsonObject1.put("money", user.getMoney());
                                        jsonObject1.put("rang", user.getRang());
                                        jsonObject1.put("score", user.getScore());
                                        jsonObject1.put("magicKey", user.magicKey);

                                        user.send("lobby;set_player;" + jsonObject1.toString());
                                    }
                                }
                                if (id.split("_")[0].equals("money")) {

                                }
                            }

                            if (data[1].equals("ping")) {
                                socket.send("lobby;ping");
                            }
                            if (data[1].equals("upgrade")) {
                                String id = data[2];
                                JSONObject player = getPlayerJSON(user);
                                if (player.getString("id").equals(id)) {
                                    getPlayer(user);
                                    int index = user.player.id;
                                    Player player2 = playersLoader.players
                                            .get(player.getString("id") + "_" + (index + 1));
                                    if (player2 != null) {
                                        int money = player2.jsonObject.getJSONArray("levels").getJSONObject(index + 1)
                                                .getInt("price");

                                        System.out.println("Money " + money);

                                        int rang = player2.jsonObject.getJSONArray("levels").getJSONObject(index + 1)
                                                .getInt("min_rang");
                                        if (rang <= user.getRang()) {
                                            if (user.money - money >= 0) {
                                                user.money -= money;

                                                user.score += Math.floor(money / 2);

                                                upgradePlayer(user);
                                                user.send_score_money();
                                                JSONObject jsonObject = new JSONObject(user.info.getPlayers());
                                                jsonObject.put("money", user.getMoney());
                                                jsonObject.put("rang", user.getRang());
                                                jsonObject.put("score", user.getScore());
                                                jsonObject.put("magicKey", user.magicKey);

                                                user.send("lobby;upgrade;" + jsonObject.toString());

                                            }
                                        }

                                    }

                                }
                            }
                            if (data[1].equals("inited")) {

                                /*
                                 * JSONObject jsonObject = new
                                 * JSONObject(user.info.getPlayers()); String
                                 * location = jsonObject.getString("location");
                                 * String mainLocation = location.split(";")[0];
                                 *
                                 * loadLocation(user, w, h, mainLocation);
                                 */
                                int train_get = getMap(user);
                                MapGeneration mapGeneration = locationLoader.hashMap
                                        .get(LocationLoader.types[1] + "_" + train_get);
                                if (mapGeneration == null) {
                                    mapGeneration = locationLoader.hashMap.get(LocationLoader.types[1] + "_"
                                            + new Random().nextInt(locationLoader.hashMap.size()));
                                }
                                String description = checkLocaleDescription(user, mapGeneration);

                                user.send("lobby;main_get;" + train_get + ";" + description);

                            }
                            if (data[1].equals("play")) {
                                getPlayer(user);
                                int w = Integer.valueOf(data[2]);
                                int h = Integer.valueOf(data[3]);
                                if (w < 1024)
                                    w = 1024;
                                if (h < 600)
                                    h = 600;
                                if (w > 1920)
                                    w = 1920;
                                if (h > 1080)
                                    h = 1080;
                                int train_get = getMap(user);
                                MapGeneration mapGeneration = locationLoader.hashMap
                                        .get(LocationLoader.types[1] + "_" + (train_get));
                                if (mapGeneration == null) {
                                    mapGeneration = locationLoader.hashMap.get(LocationLoader.types[1] + "_"
                                            + new Random().nextInt(locationLoader.hashMap.size()));
                                }
                                System.out.println(mapGeneration.id + " " + mapGeneration.category);
                                loadLocation(user, w, h, mapGeneration);
                                user.playerController.train = true;

                                user.playerController.taskManager = getTaskForPlayer(user);

                            }
                            if (data[1].equals("train")) {
                                getPlayer(user);
                                int w = Integer.valueOf(data[2]);
                                int h = Integer.valueOf(data[3]);
                                if (w > 1920)
                                    w = 1920;
                                if (h > 1080)
                                    h = 1080;

                                if (w < 1024)
                                    w = 1024;
                                if (h < 600)
                                    h = 600;
                                if (user.playerController != null) {
                                    user.playerController.t.stop();

                                    user.playerController = null;
                                }

                                int train_get = getMapTrain(user);
                                MapGeneration mapGeneration = locationLoader.hashMap
                                        .get(LocationLoader.types[0] + "_" + (train_get));
                                if (mapGeneration == null) {
                                    mapGeneration = locationLoader.hashMap.get(LocationLoader.types[1] + "_"
                                            + new Random().nextInt(locationLoader.hashMap.size()));
                                }
                                System.out.println(mapGeneration.id + " " + mapGeneration.idint);
                                loadLocation(user, w, h, mapGeneration);
                                user.playerController.train = true;

                                user.playerController.taskManager = getTaskForPlayer(user);

                            }
                            if (data[1].equals("train_get")) {
                                int train_get = getMapTrain(user);
                                MapGeneration mapGeneration = locationLoader.hashMap
                                        .get(LocationLoader.types[0] + "_" + train_get);
                                if (mapGeneration == null) {
                                    mapGeneration = locationLoader.hashMap.get(LocationLoader.types[1] + "_"
                                            + new Random().nextInt(locationLoader.hashMap.size()));
                                }
                                String description = checkLocaleDescription(user, mapGeneration);

                                user.send("lobby;train_get;" + train_get + ";" + description);
                            }

                            if (data[1].equals("change")) {
                                String id = data[2];
                                // if(){
                                boolean checkPlayerCharacter = checkPlayerandSet(id, user);
                                if (checkPlayerCharacter) {
                                    JSONObject jsonObject = new JSONObject(user.info.getPlayers());
                                    jsonObject.put("money", user.getMoney());
                                    jsonObject.put("rang", user.getRang());
                                    jsonObject.put("score", user.getScore());
                                    jsonObject.put("magicKey", user.magicKey);

                                    user.send("lobby;change_player;" + jsonObject.toString());
                                    System.out.println(checkPlayerCharacter + " " + id);
                                }
                                // }

                            }

                        }

                        if (data[0].equals("base")) {
                            if (data[1].equals("inited")) {

                            }
                        }

                        if (data[0].equals("battle")) {
                            if (data[1].equals("exit")) {

                                if (user.playerController != null) {
                                    if (user.playerController.locationModel != null) {
                                        user.playerController.locationModel.removeUser(user);

                                    }
                                    user.playerController.t.stop();
                                    user.playerController = null;
                                }
                                user.send("battle;exit");
                            }
                            if (user.playerController != null) {
                                if (data[1].equals("change")) {
                                    int style = Integer.valueOf(data[2]);
                                    if (user.playerController.ibreath != null)
                                        user.playerController.ibreath.setStyle(style);
                                }

                                if (data[1].equals("exit")) {

                                    user.playerController.t.stop();
                                    user.playerController = null;

                                    user.send("battle;exit");
                                }
                                if (data[1].equals("pause")) {
                                    user.playerController.pause = true;
                                }
                                if (data[1].equals("resume")) {
                                    user.playerController.pause = false;

                                }
                                if (data[1].equals("down")) {
                                    if (data[2].equals("fire")) {
                                        // user.playerController.ibreath.setSetting(user.info.players);
                                        // user.playerController.ibreath.fire();
                                        user.playerController.fire();

                                    }
                                    if (data[2].equals("left")) {
                                        user.playerController.side = -1;
                                        if (user.playerController.up) {
                                            user.playerController.speed = user.playerController.player.speed;

                                        }
                                        user.playerController.left = true;
                                        user.playerController.right = false;
                                    }
                                    if (data[2].equals("right")) {
                                        user.playerController.side = 1;

                                        if (user.playerController.up) {
                                            user.playerController.speed = user.playerController.player.speed;

                                        }
                                        user.playerController.right = true;
                                        user.playerController.left = false;
                                    }
                                    if (data[2].equals("up")) {
                                        user.playerController.jump();
                                    }
                                    if (data[2].equals("down")) {

                                    }
                                    if (data[2].equals("shift")) {
                                        user.playerController.shift = true;
                                        // user.playerController.speed =
                                        // user.playerController.player.speed;
                                    }
                                }
                                if (data[1].equals("up")) {

                                    if (data[2].equals("left")) {
                                        user.playerController.left = false;
                                        // user.playerController.right = false;
                                    }
                                    if (data[2].equals("right")) {
                                        user.playerController.right = false;
                                        // user.playerController.left = false;
                                    }
                                    if (data[2].equals("shift")) {
                                        user.playerController.shift = false;
                                        user.playerController.speed = user.playerController.player.go;
                                    }
                                }

                            }
                        }
                    } else {
                        RegisterModel registerModel = registerModelHashMap.get(socket);
                        if (registerModel == null) {
                            if (data[0].equals("reg")) {
                                if (data[1].equals("init")) {
                                    RegisterModel register = new RegisterModel(socket, Server.this);
                                    registerModelHashMap.put(socket, register);
                                }
                            }
                            if (data[0].equals("auth")) {
                                if (data.length >= 3) {
                                    String login = data[1];
                                    String password = data[2];

                                    user = bDatabaseModel.getUser(login, password);
                                    if (user != null) {

                                        user.connection = socket;
                                        user.send("auth;true");

                                    } else {
                                        socket.send("auth;false");
                                    }

                                } else {

                                    socket.close();

                                }

                            }
                            if (data[0].equals("login")) {

                                if (data.length >= 3) {
                                    String login = data[1];
                                    String password = data[2];

                                    user = bDatabaseModel.getUser(login, password);
                                    if (user != null) {
                                        if (user.ban == 0) {
                                            users.put(socket, user);
                                            user.connection = socket;
                                            initUser(user);
                                            user.send("login;true;");
                                            if (data.length >= 4) {
                                                String locale = data[3];
                                                user.locale = locale;
                                            } else {
                                                user.locale = "en";
                                            }
                                        } else {
                                            user.send("login;banned");
                                            user.connection.close();
                                        }

                                    } else {
                                        IpRegService ipRegService = registerService.getOrCreate(socket);

                                        if (ipRegService != null) {
                                            ipRegService.tryLogin++;
                                        }
                                        boolean check = checkCaptcha(socket);
                                        if (check == false) {
                                            socket.send("login;false");
                                        }
                                    }
                                }

                            }

                        } else if (registerModel != null && registerModel.active == true) {
                            if (data[0].equals("reg")) {
                                IpRegService ipRegService = registerService.getOrCreate(socket);

                                if (ipRegService != null) {
                                    ipRegService.countExecute++;
                                }
                                registerModel.add(s);
                            }
                            if (data[0].equals("login")) {

                                if (data.length >= 3) {
                                    String login = data[1];
                                    String password = data[2];

                                    user = bDatabaseModel.getUser(login, password);
                                    if (user != null) {
                                        if (user.ban == 0) {
                                            users.put(socket, user);
                                            user.connection = socket;
                                            initUser(user);
                                            user.send("login;true;");
                                            if (data.length >= 4) {
                                                String locale = data[3];
                                                user.locale = locale;
                                            } else {
                                                user.locale = "en";
                                            }
                                        } else {
                                            user.send("login;banned");
                                            user.connection.close();
                                        }

                                    } else {
                                        IpRegService ipRegService = registerService.getOrCreate(socket);

                                        if (ipRegService != null) {
                                            ipRegService.tryLogin++;
                                        }
                                        boolean check = checkCaptcha(socket);
                                        if (check == false) {
                                            socket.send("login;false");
                                        }
                                    }
                                }

                            }
                        } else {
                            if (data[0].equals("login")) {

                                if (data.length >= 3) {
                                    String login = data[1];
                                    String password = data[2];

                                    user = bDatabaseModel.getUser(login, password);
                                    if (user != null) {
                                        if (user.ban == 0) {
                                            users.put(socket, user);
                                            user.connection = socket;
                                            initUser(user);
                                            user.send("login;true;");
                                            if (data.length >= 4) {
                                                String locale = data[3];
                                                user.locale = locale;
                                            } else {
                                                user.locale = "en";
                                            }
                                        } else {
                                            user.send("login;banned");
                                            user.connection.close();
                                        }

                                    } else {
                                        IpRegService ipRegService = registerService.getOrCreate(socket);

                                        if (ipRegService != null) {
                                            ipRegService.tryLogin++;
                                        }
                                        boolean check = checkCaptcha(socket);
                                        if (check == false) {
                                            socket.send("login;false");
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }

        }).start();

    }

    private boolean checkPlayerandSet(String id, User user) {
        JSONObject jsonObject = new JSONObject(user.info.players);
        // System.out.println(jsonObject);
        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            String id_pl = jsonObject2.getString("id");
            if (id_pl.equals(id)) {
                UnIsPlayer(jsonObject, user);
                jsonObject2.put("is", true);
                Player player = (Player) playersLoader.players
                        .get(jsonObject2.getString("id") + "_" + jsonObject2.getInt("type")).clone();
                // System.out.println(jsonObject.toString());
                if (player != null)
                    user.player = player;

                user.info.setPlayers(jsonObject.toString());
                return true;
            }

        }

        return false;
    }

    private void UnIsPlayer(JSONObject jsonObject, User user) {
        // JSONObject jsonObject = new JSONObject(user.info.players);

        JSONArray jsonArray = jsonObject.getJSONArray("players");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            boolean is = jsonObject2.getBoolean("is");
            if (is) {

                jsonObject2.put("is", false);
                break;

            }

        }

    }

    public TaskManager getTaskForPlayer(User user) {
        TaskManager taskManager = new TaskManager();

        JSONObject jsonObject = Server.getPlayerJSON(user);
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray("tasks");

            for (int i = 0; i < this.taskManager.task_list.size(); i++) {
                boolean isDeTrue = true;
                TaskModel taskModel = this.taskManager.task_list.get(i);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                    String id = jsonObject2.getString("id");
                    JSONArray jsonArray2 = jsonObject2.getJSONArray("things");

                    for (int k = 0; k < jsonArray2.length(); k++) {
                        // System.out.println(jsonArray2.getInt(k)+"
                        // "+taskModel.id+" "+id);
                        if (taskModel.id.split("_")[0].equals(id)
                                && taskModel.id.split("_")[1].equals("" + jsonArray2.getInt(k))) {
                            parseTask(taskModel, user);
                            isDeTrue = false;
                        }

                    }
                }

                if (isDeTrue) {

                    taskManager.task_list.add((TaskModel) taskModel.clone());
                }

            }
        }
        /*
         * System.out.println(jsonObject);
         * jsonObject.getJSONArray("tasks").getJSONObject(0).getJSONArray(
         * "things").put(1); System.out.println(jsonObject);
         */
        return taskManager;
    }

    public void parseTask(TaskModel taskModel, User user) {
        Player player = user.player;
        player.speed += taskModel.addspeed;
        player.run += taskModel.addrun;
        JSONObject jsonObject = player.jsonObject.getJSONArray("levels").getJSONObject(player.id);

        jsonObject.put("speed", player.speed);
        jsonObject.put("run", player.run);
        user.send("lobby;change_data;" + player.jsonObject.getString("id") + ";" + player.jsonObject.toString());

    }

    public static JSONObject getPlayerJSON(User user) {
        JSONArray jsonObject = new JSONObject(user.info.players).getJSONArray("players");
        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject jsonObject2 = jsonObject.getJSONObject(i);

            if (jsonObject2.getBoolean("is")) {

                return jsonObject2;
            }
        }
        return null;

    }

    public static int getPlayerIndex(User user) {
        JSONArray jsonObject = new JSONObject(user.info.players).getJSONArray("players");
        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject jsonObject2 = jsonObject.getJSONObject(i);

            if (jsonObject2.getBoolean("is")) {

                return i;
            }
        }
        return -1;

    }

    private void loadLocation(User user, int w, int h, String string) {

        String location = string;
        String mainLocation = location.split(";")[0];
        MapGeneration loc = new MapGeneration(
                locationLoader.maps.get(mainLocation)/* .clone() */);

        // MapGeneration loc = (MapGeneration)
        // locationLoader.maps.get(mainLocation).clone();
        int level_of_deamons = user.player.id;

        if (level_of_deamons < loc.minLevelNpc)
            level_of_deamons = loc.minLevelNpc;

        if (level_of_deamons > loc.maxLevelNpc)
            level_of_deamons = loc.maxLevelNpc;

        loc.initDeamons((Player) playersLoader.players.get("deamon_" + level_of_deamons).clone());
        loc.initIDeamons(playersLoader.players, level_of_deamons);

        // loc.generation();
        user.width = w;
        user.height = h;

        int posX = 0;// Integer.valueOf(location.split(";")[1]);
        int posY = 0;// Integer.valueOf(location.split(";")[2]);
        if (posX == 0 && posY == 0) {
            posX = loc.px;
            posY = loc.py;
        }
        PlayerController playerController = new PlayerController(user, new Point(posX, posY));
        user.send("lobby;player_size;" + playerController.position.w + ";" + playerController.position.h);
        playerController.loc = loc;
        loc.initIDeamons(playerController);

        user.playerController = playerController;
        playerController.width = w;
        playerController.height = h;

        JSONArray jsonArray = new JSONArray();

        int i0 = posX - (int) (w / 16);
        int j0 = posY - 3;
        if (i0 < 0)
            i0 = 0;
        int i1 = (i0 + (int) (w / 16)) * 2;
        if (i1 >= loc.map.length)
            i1 = loc.map.length;

        for (int i = 0; i < i1; i++) {
            // ||\\
            JSONArray jsonArray2 = new JSONArray();
            for (int j = 0; j < loc.map[0].length; j++) {
                jsonArray2.put(loc.map[i][j]);

            }
            jsonArray.put(jsonArray2);
            // ||\\
        }
        playerController.lastSent = i1;
        playerController.init();

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("map_start", jsonArray);

        user.send("battle;map_init;" + jsonObject2);
        user.send("battle;pos_me;" + posX + ";" + posY);
        playerController.sendData();
        playerController.sendCountOfHeart();
        user.sendEnergy(playerController.energy);

    }

    public void initPlayerController(User user, int w, int h, MapGeneration mapGeneration) {
        MapGeneration loc = mapGeneration;

        // MapGeneration loc = (MapGeneration)
        // locationLoader.maps.get(mainLocation).clone();
        int level_of_deamons = user.player.id;

        if (level_of_deamons < loc.minLevelNpc)
            level_of_deamons = loc.minLevelNpc;

        if (level_of_deamons > loc.maxLevelNpc)
            level_of_deamons = loc.maxLevelNpc;

        loc.initDeamons((Player) playersLoader.players.get("deamon_" + level_of_deamons).clone());
        user.width = w;
        user.height = h;

        int posX = 0;// Integer.valueOf(location.split(";")[1]);
        int posY = 0;// Integer.valueOf(location.split(";")[2]);
        if (posX == 0 && posY == 0) {
            posX = loc.px;
            posY = loc.py;
        }
        PlayerController playerController = user.playerController;
        // PlayerController playerController = new PlayerController(user, new
        // Point(posX, posY));
        user.send("lobby;player_size;" + playerController.position.w + ";" + playerController.position.h);
        playerController.loc = loc;

        // user.playerController = playerController;
        playerController.width = w;
        playerController.height = h;
        JSONArray jsonArray = new JSONArray();

        int i0 = posX - (int) (w / 16);
        int j0 = posY - 3;
        if (i0 < 0)
            i0 = 0;
        int i1 = (i0 + (int) (w / 16)) * 2;
        if (i1 >= loc.map.length)
            i1 = loc.map.length;

        for (int i = 0; i < i1; i++) {
            // ||\\
            JSONArray jsonArray2 = new JSONArray();
            for (int j = 0; j < loc.map[0].length; j++) {
                jsonArray2.put(loc.map[i][j]);

            }
            jsonArray.put(jsonArray2);
            // ||\\
        }
        playerController.lastSent = i1;

        // it isnt need for multiplay cuz it running timer for calculate simple
        // game

        // playerController.init();

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("map_start", jsonArray);
        jsonObject2.put("multiplayer", true);
        user.send("battle;map_init;" + jsonObject2);
        user.send("battle;pos_me;" + posX + ";" + posY);
        playerController.sendData();
        playerController.sendCountOfHeart();
        user.sendEnergy(playerController.energy);

    }

    private void loadLocation(User user, int w, int h, MapGeneration mapGeneration) {

        MapGeneration loc = new MapGeneration(mapGeneration/* .clone() */);

        // MapGeneration loc = (MapGeneration)
        // locationLoader.maps.get(mainLocation).clone();
        int level_of_deamons = user.player.id;

        if (level_of_deamons < loc.minLevelNpc)
            level_of_deamons = loc.minLevelNpc;

        if (level_of_deamons > loc.maxLevelNpc)
            level_of_deamons = loc.maxLevelNpc;

        loc.initDeamons((Player) playersLoader.players.get("deamon_" + level_of_deamons).clone());
        loc.initIDeamons(playersLoader.players, level_of_deamons);

        // loc.generation();
        user.width = w;
        user.height = h;

        int posX = 0;// Integer.valueOf(location.split(";")[1]);
        int posY = 0;// Integer.valueOf(location.split(";")[2]);
        if (posX == 0 && posY == 0) {
            posX = loc.px;
            posY = loc.py;
        }
        PlayerController playerController = new PlayerController(user, new Point(posX, posY));
        user.send("lobby;player_size;" + playerController.position.w + ";" + playerController.position.h);
        playerController.loc = loc;
        loc.initIDeamons(playerController);

        user.playerController = playerController;
        playerController.width = w;
        playerController.height = h;

        JSONArray jsonArray = new JSONArray();

        int i0 = posX - (int) (w / 16);
        int j0 = posY - 3;
        if (i0 < 0)
            i0 = 0;
        int i1 = (i0 + (int) (w / 16)) * 2;
        if (i1 >= loc.map.length)
            i1 = loc.map.length;

        for (int i = 0; i < i1; i++) {
            // ||\\
            JSONArray jsonArray2 = new JSONArray();
            for (int j = 0; j < loc.map[0].length; j++) {
                jsonArray2.put(loc.map[i][j]);

            }
            jsonArray.put(jsonArray2);
            // ||\\
        }
        playerController.lastSent = i1;
        playerController.init();

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("map_start", jsonArray);

        user.send("battle;map_init;" + jsonObject2);
        user.send("battle;pos_me;" + posX + ";" + posY);
        playerController.sendData();
        playerController.sendCountOfHeart();
        user.sendEnergy(playerController.energy);

    }

    private void initUser(User user) {
        user.info = this.bDatabaseModel.getInfo(user.id);
        user.info.user = user;
        getPlayer(user);

        for (int i = 0; i < playersLoader.datas.size(); i++) {
            user.send("lobby;players_setting;" + playersLoader.datas.get(i).toString());
        }

        for (int i = 0; i < breathLoader.datas.size(); i++) {
            user.send("lobby;breath_setting;" + breathLoader.datas.get(i).toString());
        }
        JSONObject jsonObject = new JSONObject(user.info.getPlayers());
        jsonObject.put("money", user.getMoney());
        jsonObject.put("rang", user.getRang());
        jsonObject.put("score", user.getScore());
        jsonObject.put("magicKey", user.magicKey);
        jsonObject.put("login", user.login);

        user.send("lobby;login;" + jsonObject.toString());

    }

    public void upgradePlayer(User user) {
        JSONObject jsonObject = new JSONObject(user.info.getPlayers());
        JSONArray array = jsonObject.getJSONArray("players");
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject2 = array.getJSONObject(i);
            String id = jsonObject2.getString("id");
            int type = jsonObject2.getInt("type");

            if (playersLoader.players.get(id + "_" + type) == null)
                return;

            // player = (Player) player.clone();

            if (jsonObject2.getBoolean("is")) {
                jsonObject2.put("type", type + 1);
                user.info.setPlayers(jsonObject.toString());
                getPlayer(user);
            }

            // user.players.add(player);

        }
    }

    private void getPlayer(User user) {
        JSONObject jsonObject = new JSONObject(user.info.getPlayers());
        JSONArray array = jsonObject.getJSONArray("players");
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject2 = array.getJSONObject(i);
            String id = jsonObject2.getString("id");
            int type = jsonObject2.getInt("type");
            Player player = (Player) playersLoader.players.get(id + "_" + type);
            if (player == null)
                return;

            player = (Player) player.clone();

            if (jsonObject2.getBoolean("is")) {
                user.player = player;
                getTaskForPlayer(user);
            }

            JSONArray array2 = jsonObject2.getJSONArray("breath");
            for (int j = 0; j < array2.length(); j++) {
                JSONObject jsonObject3 = array2.getJSONObject(j);
                String idb = jsonObject3.getString("id");

                IBreath breath = breathLoader.breaths.get(idb);
                if (breath != null) {
                    breath = (IBreath) breath.cl();
                    if (jsonObject3.getBoolean("is")) {
                        player.ibreath = breath;

                        breath.setSetting(jsonObject3.toString());
                    }
                    breath.setSetting(jsonObject3.toString());
                    player.arrayList.add(breath);
                    player.hashMap.put(idb, breath);
                    player.maxPlayers = playersLoader.players_info.get(player.name) + 1;

                }

            }

            // user.players.add(player);

        }

    }

    public int getMapTrain(User user) {
        int id = 1;
        JSONObject jsonObject = getPlayerJSON(user);
        if (jsonObject == null)
            return 1;
        JSONArray jsonArray = jsonObject.getJSONArray("location_end");
        if (jsonArray.length() == 0)
            return 1;
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                String string = jsonArray.getString(i);
                String name = string.split("_")[0];
                // System.out.println(string);
                int index = Integer.valueOf(string.split("_")[string.split("_").length - 1]);
                if (name.equals(LocationLoader.types[0])) {
                    if (index > id)
                        id = index;
                }

            }
        }
        System.out.println("train " + id);
        return (id + 1);

    }

    public int getMap(User user) {
        int id = 1;
        JSONObject jsonObject = new JSONObject(user.info.players);

        JSONArray jsonArray = jsonObject.getJSONArray("location_end");
        if (jsonArray.length() == 0)
            return 1;
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                String string = jsonArray.getString(i);
                String name = string.split("_")[0];
                // System.out.println(string);
                int index = Integer.valueOf(string.split("_")[string.split("_").length - 1]);
                if (string.contains(LocationLoader.types[1])) {
                    if (index > id)
                        id = index;
                }

            }
        }
        System.out.println("train " + id);
        return (id + 1);

    }

    @Override
    public void onError(WebSocket webSocket, final Exception e) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                e.printStackTrace();

            }
        }).start();
    }

    @Override
    public void onStart() {
        System.out.println("Started");
    }

    public String checkLocaleDescription(User user, MapGeneration mapGeneration) {
        if (user.locale.equals("ru")) {
            return mapGeneration.description;
        } else if (user.locale.equals("es")) {
            return mapGeneration.description_es;
        } else if (user.locale.equals("en")) {
            return mapGeneration.description_en;
        } else if (user.locale.equals("ja") || user.locale.equals("jp")) {
            return mapGeneration.description_jp;
        } else {
            return mapGeneration.description_en;
        }

    }
}
