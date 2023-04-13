package com.AntonSibgatulin.location.generation;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.AntonSibgatulin.Players.HouseLoader;
import com.AntonSibgatulin.Players.Player;
import com.AntonSibgatulin.location.LocationLoader;
import com.AntonSibgatulin.location.LocationModel;
import com.AntonSibgatulin.location.PlayerController;
import com.AntonSibgatulin.location.Vector2d;
import com.AntonSibgatulin.location.deamons.IDeamons;
import com.AntonSibgatulin.location.deamons.lowmoon.RuiModel.RuiModel;
import com.AntonSibgatulin.location.deamons.npc.NPCDeamon;

public class MapGeneration implements Cloneable {
    // 1 = ground
    // 2 = grass
    // 200 = money
    // 3 = glicinia
    // 4 = health
    // 5 = power
    // 6 = amor
    // 7 = nitro
    // 8 = just tree
    // 9 = glicinia2
    // 11/12/13 = house1/2/3
    // 12 = deamons
    public static int SIZE = 24;
    public static int SIZE_FIGHT = 18;
    public int map[][] = null;
    public ArrayList<NPCDeamon> deamons = new ArrayList<>();
    public ArrayList<IDeamons> iDeamons = new ArrayList<>();
    public JSONArray HardDeamons = null;
    public int px = 0, py = 0;
    public int money = 0;
    public int score = 0;
    public int rand_nitro = 30;
    public int rand_amor = 60;
    public int rand_power = 70;
    public int random_health = 90;
    public boolean killAllDeamons = false;

    public String id = null;
    public int sma = 0;
    public boolean up_b = false, down_b = false;
    public int up = 0, down = 0;
    public long seed = 0;
    public int min = 0, max = 0;
    public int rand = 0;
    public int width = 0;
    public int height = 0;
    public java.awt.Point spawn = null;
    public int idint = 0;
    public String category = null;
    public int minLevelNpc = 0;
    public int maxLevelNpc = 100;

    public int iteration_of_deamons = 1;
    public int MAX_FAST_DEAMON = 3;
    public int MAX_AMOR_DEAMON = 3;

    public double fastParametre = 2.1;

    public MapGeneration(int width, int height) {

        map = new int[width][height];

    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public MapGeneration(MapGeneration mapGeneration) {
        this.seed = mapGeneration.seed;
        this.id = mapGeneration.id;
        this.down = mapGeneration.down;
        width = mapGeneration.width;
        height = mapGeneration.height;
        this.idint = mapGeneration.idint;
        this.down_b = mapGeneration.down_b;
        this.min = mapGeneration.min;
        this.max = mapGeneration.max;
        this.rand = mapGeneration.rand;
        this.sma = mapGeneration.sma;
        this.money = mapGeneration.money;
        this.score = mapGeneration.score;
        this.spawn = mapGeneration.spawn;
        this.category = mapGeneration.category;
        this.description = mapGeneration.description;
        this.description_en = mapGeneration.description_en;
        map = new int[width][height];

        this.random_health = mapGeneration.random_health;
        this.rand_amor = mapGeneration.rand_amor;
        this.rand_power = mapGeneration.rand_power;
        this.rand_nitro = mapGeneration.rand_nitro;
        this.iteration_of_deamons = mapGeneration.iteration_of_deamons;
        this.HardDeamons = mapGeneration.HardDeamons;
        this.MAX_FAST_DEAMON = mapGeneration.MAX_FAST_DEAMON;
        this.fastParametre = mapGeneration.fastParametre;
        this.MAX_AMOR_DEAMON = mapGeneration.MAX_AMOR_DEAMON;

        generation();
    }

    public String description = null;
    public String description_en = null;
    public String description_es = null;
    public String description_jp = null;
    public String description_zh = null;

    public MapGeneration(Integer idint, String id, int i, int j, long seed, boolean down_b, int down, int min, int max,
                         int rand, int sma, java.awt.Point spawn, int money, int score, String description, String description_en, String description_es, String description_jp, String description_zh,
                         String category) {
        map = new int[i][j];
        this.seed = seed;
        this.id = id;
        this.down = down;
        width = i;
        height = j;
        this.idint = idint;
        this.down_b = down_b;
        this.min = min;
        this.max = max;
        this.rand = rand;
        this.sma = sma;
        this.spawn = spawn;
        this.money = money;
        this.score = score;
        this.description = description;
        this.description_en = description_en;
        this.category = category;
        this.description_zh = description_zh;
        this.description_es = description_es;
        this.description_jp = description_jp;


    }

    public MapGeneration(int idint, String id, int i, int j, long seed, boolean down_b, int down, int min, int max,
                         int rand, int sma, java.awt.Point spawn, HouseLoader houseLoader, int money, int score, String description,
                         String description_en, String description_es, String description_jp, String description_zh, String category) {
        map = new int[i][j];
        this.idint = idint;
        this.seed = seed;
        this.id = id;
        this.down = down;
        width = i;
        height = j;

        this.down_b = down_b;
        this.min = min;
        this.max = max;
        this.rand = rand;
        this.sma = sma;
        this.spawn = spawn;
        this.description = description;
        this.money = money;
        this.score = score;
        this.description_en = description_en;
        this.description_jp = description_jp;
        this.description_es = description_es;
        this.description_zh = description_zh;

        this.category = category;

    }

    public MapGeneration(int idint, String id, int i, int j, long seed, boolean down_b, int down, int min, int max,
                         int rand, int sma, java.awt.Point spawn, HouseLoader houseLoader, int money, int score, String description,
                         String description_en, String description_es, String description_jp,String description_zh, String category, int random_amor,
                         int random_power, int random_nitro, int random_health, int iteration_of_deamons, JSONArray HardDeamons,
                         int MAX_FAST_DEAMON, double fastParametre, int MAX_AMOR_DEAMON) {
        map = new int[i][j];
        this.idint = idint;
        this.seed = seed;
        this.id = id;
        this.down = down;
        width = i;
        height = j;

        this.down_b = down_b;
        this.min = min;
        this.max = max;
        this.rand = rand;
        this.sma = sma;
        this.spawn = spawn;

        this.description = description;
        this.description_en = description_en;
        this.description_es = description_es;
        this.description_jp = description_jp;
        this.description_zh = description_zh;
        this.money = money;
        this.score = score;

        this.category = category;

        this.random_health = random_health;
        this.rand_amor = random_amor;
        this.rand_power = random_power;
        this.rand_nitro = random_nitro;
        this.iteration_of_deamons = iteration_of_deamons;
        this.HardDeamons = HardDeamons;
        this.MAX_FAST_DEAMON = MAX_FAST_DEAMON;
        this.fastParametre = fastParametre;
        this.MAX_AMOR_DEAMON = MAX_AMOR_DEAMON;
    }

    public void draw() {
        for (int i = 0; i < map[0].length; i++) {
            for (int j = 0; j < map.length; j++) {
                // if(map[j][i]==null)continue;
                System.out.print(" " + map[j][i]);
            }
            System.out.println();
        }
    }

    public void setTile(int x, int y, int type) {
        this.map[x][y] = type;
    }

    public int getIdTileType(TileType type) {
        int t = 0;
        if (type == TileType.NONE)
            t = 0;
        if (type == TileType.GRASS)
            t = 2;
        if (type == TileType.GROUND)
            t = 1;
        if (type == TileType.TREE)
            t = 3;
        if (type == TileType.STONE)
            t = 4;
        if (type == TileType.AURUM)
            t = 5;
        if (type == TileType.FERRUM)
            t = 6;
        if (type == TileType.ARGENTUM)
            t = 7;

        return t;

    }

    public int getRandom(Random random, int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public void generation(int seed) {
        int ideamonI = 0;

        Random random = seed >= 0 ? new Random(seed) : new Random(System.currentTimeMillis());
        int groundMax = 10;
        int groundMin = 20 + groundMax;
        // System.out.println("map "+map.length);
        int arr[] = new int[map.length];
        for (int i = 0; i < map.length; i++) {

            int dir = random.nextInt(2) == 1 ? 1 : -1;

            if (i > 0) {
                if (arr[i - 1] + dir < groundMax || arr[i - 1] + dir > groundMin) {
                    dir = -dir;
                }
                arr[i] = arr[i - 1] + dir;
            } else {
                arr[i] = groundMin;
            }

        }

        for (int i = 1; i < map.length - 1; i++) {
            float sum = arr[i];
            int count = 1;
            for (int k = 0; k <= 2; k++) {
                int i1 = i - k;
                int i2 = i + k;
                if (i1 > 0) {
                    sum += arr[i1];
                    count++;
                }
                if (i2 < map.length) {
                    sum += arr[i2];
                    count++;
                }
            }
            arr[i] = (int) (sum / count);
        }

        for (int i = 0; i < map.length; i++) {
            map[i][arr[i]] = 2;
            if (i > 0 && map[i - 1][arr[i - 1]] == 2 && map[i][arr[i]] == 2 && arr[i] != arr[i - 1]) {
                map[i - 1][arr[i]] = 2;
            }
            for (int j = arr[i] + 1; j < map[0].length; j++) {
                map[i][j] = 1;
            }
        }

    }

    public void initDeamons(Player player) {
        for (int i = 0; i < deamons.size(); i++) {
            // 1659457111101

            NPCDeamon deamon = deamons.get(i);
            deamon.player = player;
            deamon.init();

        }
    }

    public void initIDeamons(HashMap<String, Player> players, int level_of_deamons) {

        for (int i = 0; i < this.iDeamons.size(); i++) {
            // 1659457111101

            IDeamons deamon = iDeamons.get(i);
            System.out.println(deamon.getName() + "_" + level_of_deamons);
            Player player = (Player) players.get(deamon.getName() + "_" + level_of_deamons).clone();

            deamon.init(player);

        }
    }

    public void initIDeamons(LocationModel locationModel) {

        for (int i = 0; i < this.iDeamons.size(); i++) {
            // 1659457111101

            IDeamons deamon = iDeamons.get(i);
            deamon.setLocation(locationModel);

        }
    }

    public void initIDeamons(PlayerController playerController) {

        for (int i = 0; i < this.iDeamons.size(); i++) {
            // 1659457111101

            IDeamons deamon = iDeamons.get(i);
            deamon.setPlayerController(playerController);// (locationModel);

        }
    }

    public void generation() {
        int ideamonI = 0;

        Random random = seed >= 0 ? new Random(seed) : new Random(System.currentTimeMillis());

        int groundMax = max;
        int groundMin = min;

        int randi = this.getRandom(random, 10, 20);
        // randi = spawn character
        /*
         * ArrayList<Integer> none = new ArrayList<Integer>(); for(int i
         * =0;i<200+random.nextInt(150);i++){ Integer m = new
         * Integer(i+random.nextInt(100)); none.add(m);
         *
         * none.add(m+1); none.add(m+2); }
         */
        // System.out.println("map "+map.length);
        int arr[] = new int[map.length];
        int l = 0;
        for (int i = 0; i < map.length; i++) {

            int dir = 0;

            if (down_b) {
                // random.nextInt(2) == 1? 1:-1;
                if (random.nextInt(rand) == down) {
                    dir = -1;
                } else {
                    dir = 1;
                }
            }

            if (!down_b) {
                // random.nextInt(2) == 1? 1:-1;
                if (random.nextInt(rand) == down) {
                    dir = -1;
                } else {
                    dir = 1;
                }
            }

            if (i > 55 && i < map.length - 55) {
                if (arr[i - 1] + dir < groundMax || arr[i - 1] + dir > groundMin) {
                    dir = -dir;
                }
                arr[i] = arr[i - 1] + dir;

            } else {
                arr[i] = groundMin;
            }

            if (i == randi) {
                l = i;
            }

        }

        for (int i = 1; i < map.length - 1; i++) {

            float sum = arr[i];
            int count = 1;
            for (int k = 0; k < sma; k++) {
                int i1 = i - k;
                int i2 = i + k;
                if (i1 > 0) {
                    sum += arr[i1];
                    count++;
                }
                if (i2 < map.length) {
                    sum += arr[i2];
                    count++;
                }
            }

            arr[i] = (int) (sum / count);

        }

        int rather = 0;
        int genMonsters = 0;
        int getMonsterWaiter = 0;
        for (int i = 0; i < map.length; i++) {
            /*
             * boolean is = false; for(int k=0;k<none.size();k++){
             * if(none.get(k)==i) { is = true; } }
             */

            map[i][arr[i]] = 2;

            if (i == randi) {
                px = i;
                py = arr[i] - 6;
            } else {

                int is = random.nextInt(10);

                if (rather > 0)
                    rather--;

                if (is == 1 && rather == 0) {
                    if (i > 50 && i < map.length - 50) {

                        map[i][arr[i]] = 0;
                        if (i + 1 < map.length) {
                            map[i + 1][arr[i]] = 0;
                        }
                        if (i + 2 < map.length) {
                            map[i + 2][arr[i]] = 0;
                            i += 2;
                        }
                        rather = 10 + new Random().nextInt(5);
                        continue;
                    }
                }

            }

            if (rather == 14) {

                genMonsters = random.nextInt(rather);
            }
            if (rather == genMonsters && i > 55 && i < map.length - 55) {
                if (getMonsterWaiter <= 0 && new Random().nextInt(2) == 0) {
                    getMonsterWaiter = 6;
                    map[i][arr[i] - 5] = 10;
                    NPCDeamon deamon = new NPCDeamon(null, "npc_" + deamons.size(),
                            new Point(i * MapGeneration.SIZE, (arr[i] - 5) * MapGeneration.SIZE));
                    if (MAX_FAST_DEAMON != 0 && MAX_AMOR_DEAMON == 0) {
                        if (new Random().nextInt(MAX_FAST_DEAMON) == 0) {
                            deamon.initParametre(fastParametre);
                        }
                    }

                    if (MAX_AMOR_DEAMON != 0 && MAX_FAST_DEAMON != 0) {
                        if (new Random().nextInt(MAX_AMOR_DEAMON) == 0) {
                            deamon.initParametreAmor();// (fastParametre);
                        } else {
                            if (new Random().nextInt(MAX_FAST_DEAMON) == 0) {
                                deamon.initParametre(fastParametre);
                            }
                        }
                    }

                    deamons.add(deamon);
                }
            }

            if (this.HardDeamons != null && rather == genMonsters
                    && i > (map.length - 55 * 2) / this.HardDeamons.length() * ideamonI && i > 55 && i < map.length - 55
                    && ideamonI < this.HardDeamons.length()) {
                getMonsterWaiter = 6;

                /*
                 * map[i][arr[i] - 5] = 10; NPCDeamon deamon = new
                 * NPCDeamon(null, "npc_" + deamons.size(), new Point(i *
                 * MapGeneration.SIZE, (arr[i] - 5) * MapGeneration.SIZE));
                 * deamons.add(deamon);
                 */
                String name = this.HardDeamons.getString(ideamonI);
                IDeamons iDeamons = null;
                if (name.equals("rui")) {
                    iDeamons = (IDeamons) new RuiModel("deamons_" + this.iDeamons.size(),
                            new Vector2d(i * MapGeneration.SIZE, (arr[i] - 3) * MapGeneration.SIZE), name);
                }
                ideamonI += 1;
                this.iDeamons.add(iDeamons);

            }
            int money = new Random().nextInt(6);
            int moneyis = new Random().nextInt(15);
            if (moneyis == 0) {
                map[i][arr[i] - 4 - money] = 200;
            }

            getMonsterWaiter--;

            if (i > 0 && map[i - 1][arr[i - 1]] == 2 && map[i][arr[i]] == 2 && arr[i] != arr[i - 1]) {

                map[i - 1][arr[i]] = 2;
            }
            int endi = arr[i] + 30;
            if (endi >= map[0].length)
                endi = map[0].length;
            for (int j = arr[i] + 1; j < endi; j++) {
                /*
                 * if(is){ map[i][j]=0; }else
                 */
                map[i][j] = 1;
            }

        }
        int last = 0;
        for (int i = 0; i < map.length; i++) {
            /*
             * boolean is = false; for(int k=0;k<none.size();k++){
             * if(none.get(k)==i) { is = true; } }
             */

            if ((i > map.length - 55 && i < map.length) || (i > 0 && i < 55)) {

                if (i % 8 == 0) {
                    // System.out.println("Location is train");
                    if (this.category.equals(LocationLoader.types[0]) && checkDataGround(4, i, arr[i]))
                        map[i][arr[i] - 1] = 3;
                    if (this.category.equals(LocationLoader.types[1]) && checkDataGround(4, i, arr[i])) {
                        map[i][arr[i] - 1] = 9;
                    }

                }
                if (i % 12 == 0 && i < 55 - 12) {
                    if (this.category.equals(LocationLoader.types[1])) {
                        map[i][arr[i] - 1] = 11;// + random.nextInt(2);
                    }
                }
            } else {
                if (random.nextInt(30) == 1 && checkDataGround(8, i, arr[i])
                        || checkDataGround(8, i, arr[i]) && last == 0) {
                    map[i][arr[i] - 1] = 8;
                    last = 6 + random.nextInt(15);
                }
                if (i % 18 == 0 && checkDataGround(8, i, arr[i])) {
                    if (this.category.equals(LocationLoader.types[1])) {
                        map[i][arr[i] - 1] = 11 + random.nextInt(2);
                    }
                }

            }

            if (last > 0)
                last--;

            int rand_nitro = random.nextInt(this.rand_nitro);
            int rand_amor = random.nextInt(this.rand_amor);
            int rand_power = random.nextInt(this.rand_power);
            int rand_health = random.nextInt(this.random_health);
            // 200 = money
            // 3 = glicinia
            // 4 = health
            // 5 = power
            // 6 = amor
            // 7 = nitro
            if (rand_nitro == 1) {
                map[i][arr[i] - 2] = 7;
            }
            if (rand_amor == 1) {
                map[i][arr[i] - 2] = 6;
            }
            if (rand_power == 1) {
                map[i][arr[i] - 2] = 5;
            }
            if (rand_health == 1) {
                map[i][arr[i] - 2] = 4;
            }

        }

    }

    public boolean checkDataGround(int length, int start, int height) {
        for (int i = start; i < start + length; i++) {
            if (map[i][height] == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkCheckDataGround(int length, int i, int[] arr, int length_height) {
        for (int j = 0; j < length_height; j++) {
            checkDataGround(length, i, arr[i + 1]);
        }
        return true;
    }

    public static void main1(String... args) throws Exception {
        // for(int i =0;i<500;i++){
        long time = System.currentTimeMillis();
        long seed = time;
        boolean down_b = true;
        int down = 1;
        int min = 950, max = 50;
        int rand = 2;
        int sma = 7;
        String id = "";
        String category = LocationLoader.types[1];
        String description = "День 7.Финальный отбор — ежегодное испытание, которое проходят люди, желающие стать истребителями демонов. Испытание проходит на горе Фуджикасане, на которой круглый год от подножия до середины растёт глициния. Вершина горы полна демонов, которых поймали и привели истребители. Глициния ядовита для демонов, поэтому они оказываются в ловушке и не могут покинуть гору.Задача испытуемых заключается в том, чтобы пережить на горе семь ночей. Выжившие участники получают право на вступление в организацию, выбирают руду для собственного клинка ничирин, а также получают собственную униформу и ворону уз.Твоя задача дойти до камня и убить как можно больше демонов,чтобы заработать как моэно больше очков и денег.";
        String description_en = "Day 7. Final selection - an annual test that people who want to become demon slayers pass. The test takes place on Mount Fujikasane, where wisteria grows all year round from the foot to the middle. The top of the mountain is full of demons that have been captured and brought back by the Slayers. Wisteria is poisonous to demons, so they are trapped and cannot leave the mountain. The task of the test subjects is to survive seven nights on the mountain. Surviving members get the right to join the organization, choose the ore for their own nichirin blade, and also receive their own uniform and crow of bonds. Your task is to reach the stone and kill as many demons as possible in order to earn more points and money as moeno.";
        String description_es = null;
        String description_ja = null;
        String description_zh = null;

        MapGeneration generation = new MapGeneration(0, id, 5000, 1000, seed, down_b, down, min, max, rand, sma, null,
                50, 250, description, description_en, description_es, description_ja, description_zh, category);
        // generation.killAllDeamons = true;

        generation.generation();
        generation.save(id, "main/");
        System.out.println(System.currentTimeMillis() - time);
        // generation.draw();
        // }
    }

    public void save(String name, String main) throws Exception {
        java.io.File file = new java.io.File("location/" + main + name + ".json");

        // file.mkdir();
        // file = new java.io.File("location/"+name+".json");
        boolean created = file.createNewFile();
        if (created) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idint", new File("location").listFiles().length);
            jsonObject.put("id", name);
            jsonObject.put("down", down);
            jsonObject.put("down_b", down_b);
            jsonObject.put("seed", seed);
            jsonObject.put("sma", sma);
            jsonObject.put("rand", rand);
            jsonObject.put("width", width);
            jsonObject.put("height", height);

            jsonObject.put("min", min);
            jsonObject.put("max", max);
            jsonObject.put("spawnx", px);
            jsonObject.put("spawny", py);
            jsonObject.put("money", money);
            jsonObject.put("score", score);
            jsonObject.put("category", category);
            jsonObject.put("description", description);
            jsonObject.put("description_en", description_en);
            jsonObject.put("description_es", description_es);
            jsonObject.put("description_ja", description_jp);
            jsonObject.put("description_zh", description_zh);


            jsonObject.put("rand_amor", rand_amor);

            jsonObject.put("rand_health", random_health);

            jsonObject.put("rand_power", rand_power);

            jsonObject.put("rand_nitro", rand_nitro);
            jsonObject.put("iteration_of_deamons", this.iteration_of_deamons);

            if (killAllDeamons == true) {
                jsonObject.put("killAllDeamons", killAllDeamons);
            }
            if (minLevelNpc != 0) {
                jsonObject.put("minLevelNpc", minLevelNpc);
            }
            if (maxLevelNpc != 100) {
                jsonObject.put("maxLevelNpc", maxLevelNpc);
            }
            file = new File("location/" + name + ".json");

            FileWriter out = new FileWriter(file);
            out.write(jsonObject.toString());
            out.flush();
            out.close();
        } else {
            System.err.println("File already exist!(MapGeneration)");

        }

    }

}
