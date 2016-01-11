package com.mhh.httputils.tab.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by �ϻԻ� on 2015/8/28.
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
     * ����SD����·��
     * @return SdCard·��
     */
    public static String getSdCardPath(){

        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //�ж�sd���Ƿ����
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼
        }
        return sdDir.toString();
    }


    /**
     *
     * �����ݱ��浽SD��
     * @param data ��������ݶ���
     * @param key �������ݵļ���
     * @param <T> �������ͷ���
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
     * ͨ���ļ�����ȡ����
     * @param key ��ȡ���ݵļ���
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
