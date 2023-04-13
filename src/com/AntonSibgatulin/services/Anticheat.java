package com.AntonSibgatulin.services;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Anticheat", schema = "public")
public class Anticheat implements Serializable {
    public int id;

    public long timeLastCreateMap;

    public int countCreatedMaps;
    public int countWarningFluds;
    public int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCountCreatedMaps(int countCreatedMaps) {
        this.countCreatedMaps = countCreatedMaps;
    }

    public void setCountWarningFluds(int countWarningForFluds) {
        this.countWarningFluds = countWarningForFluds;
    }

    public void setTimeLastCreateMap(long lastTimeCreateMap) {
        this.timeLastCreateMap = lastTimeCreateMap;
    }


    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "id")
    public int getId() {
        return id;
    }

    @Column(name = "userId")
    public int getUserId() {
        return userId;
    }

    @Column(name = "timeLastCreateMap")
    public long getTimeLastCreateMap() {
        return timeLastCreateMap;
    }

    @Column(name = "countCreatedMaps")
    public int getCountCreatedMaps() {
        return countCreatedMaps;
    }

    @Column(name = "countWarningFluds")
    public int getCountWarningFluds() {
        return countWarningFluds;
    }
    public Anticheat(){

    }
    public Anticheat(int id){
        setUserId(id);
        setCountWarningFluds(0);
        setTimeLastCreateMap(0);
        setCountCreatedMaps(0);
    }
}
