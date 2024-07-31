package com.example.beans;

import com.example.foes.Ice;

import java.util.Collection;

/**
 * Created by Admin on 03/03/2017.
 */
public class BeanByCtor {
    String myName;
    Ice ice;
    Collection<JavaBean> myBeans;
    double myDouble;
    double myDate;

    String smashing(String somePumpkins,char with,Ice picker){
        return somePumpkins;
    }

    public void setMyDate(double myDate) {
        this.myDate = myDate;
    }

    public BeanByCtor(String myName, Ice iceArg, Collection<JavaBean> myBeans,double myDouble) {
        this.myName = myName;
        this.ice = iceArg;
        this.myBeans = myBeans;
        this.myDouble = myDouble;
    }

    public BeanByCtor() {
        myName="Slim Shady";
    }

    public String getMyName() {
        return myName;
    }

    void setMyName(String myName) {
        this.myName = myName;
    }

    public Collection<JavaBean> getMyBeans() {
        return myBeans;
    }

    public Ice getIce() {
        return ice;
    }

    void setMyBeans(Collection<JavaBean> myBeans) {
        this.myBeans = myBeans;
    }

    public void setIce(Ice ice) {
        this.ice = ice;
    }

    public double getMyDouble() {
        return myDouble;
    }

    public void setMyDouble(double myDouble) {
        this.myDouble = myDouble;
    }

    @Override
    public String toString() {
        return "ImGroovy{" +
                "myName='" + myName + '\'' +
                ", ice=" + ice +
                '}';
    }

}
