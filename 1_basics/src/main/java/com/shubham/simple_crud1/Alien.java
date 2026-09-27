package com.shubham.simple_crud1;

import org.springframework.stereotype.Component;

@Component // Hey Spring framework, please manage this object
public class Alien {
    private  int aid;
    private String aname,tech;

    public Alien() {
        System.out.println("Object created");
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }


    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }
    @Override
    public String toString() {
        return "Alien{" +
                "aid=" + aid +
                ", aname='" + aname + '\'' +
                ", tech='" + tech + '\'' +
                '}';
    }

}
