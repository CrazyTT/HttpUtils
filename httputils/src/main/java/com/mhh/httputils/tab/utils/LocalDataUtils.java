package com.mhh.httputils.tab.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by 孟辉辉 on 2015/8/28.
 */
public class LocalDataUtils {

    private static LocalDataUtils utils;

    public static LocalDataUtils getInstance(){

        if (utils == null){
            utils = new LocalDataUtils();
        }
        return utils;
    }


    /**
     * 返回SD卡的路径
     * @return SdCard路径
     */
    public static String getSdCardPath(){

        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }


    /**
     *
     * 将数据保存到SD卡
     * @param data 保存的数据对象
     * @param key 保存数据的键名
     * @param <T> 对象类型泛型
     * @throws IOException
     */
    public <T extends Object> void saveDataToSdCar(T data, String key) throws IOException {


        File file = new File(getSdCardPath() + File.separator + Config.getDefaultPath());
        if (!file.exists()){
            file.mkdirs();
        }
        String fileName = key + Config.getSuffix();
        file = new File(getSdCardPath() + File.separator + Config.getDefaultPath(), fileName);
        ObjectOutputStream OS = null;
        try {
            OS = new ObjectOutputStream(new FileOutputStream(file));
            OS.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (OS != null){
                OS.close();
                OS = null;
            }
        }
    }

    /**
     * 通过文件名获取数据
     * @param key 获取数据的键名
     * @param <T>
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	public <T extends Object> T getDataFromSdCard(String key) throws IOException {
        String fileName = key + Config.getSuffix();
        T result = null;
        ObjectInputStream OIS = null;
        try {

            OIS = new ObjectInputStream(LocalFileUtil.getLocalIS(fileName, getSdCardPath() + File.separator + Config.getDefaultPath()));
            result = (T) OIS.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (OIS != null){
                OIS.close();
                OIS = null;
            }
        }

        return result;
    }

    public <T extends Object> void saveDataToCache(T data, Context context, String key) throws IOException {

        String fileName = key + Config.getSuffix();
        ObjectOutputStream OS = null;
        try {
            OS = new ObjectOutputStream(LocalFileUtil.getLocalOS(context, fileName));
            OS.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (OS != null){
                OS.close();
                OS = null;
            }
        }
    }

    @SuppressWarnings("unchecked")
	public <T extends Object> T getDataFromCache(Context context,String key) throws IOException {
        String fileName = key + Config.getSuffix();
        T result = null;
        ObjectInputStream OIS = null;
        try {

            OIS = new ObjectInputStream(LocalFileUtil.getLocalIS(context, fileName));
            result = (T) OIS.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (OIS != null){
                OIS.close();
                OIS = null;
            }
        }

        return result;
    }
}
