package com.niuyang.project;

/**
 * Created by niuyang on 2017/10/12.
 */
public class CameraData {

    public int getCon() {
        return Con;
    }

    public void setCon(int con) {
        Con = con;
    }

    public int getGet() {
        return Get;
    }

    public void setGet(int get) {
        Get = get;
    }

    private int Con;
    private int Send;
    private int Get;
    private static CameraData instance=null;
    public static synchronized CameraData getInstance(){
        if(instance==null){
            instance=new CameraData();
        }
        return instance;
    }
    private CameraData(){
    }
}
