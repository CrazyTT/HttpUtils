package com.mhh.httputils.tab.utils;


/**
 * Created by √œª‘ª‘ on 2015/8/28.
 */
public class Config {


	public static boolean isDebug = true;
    private static String defaultPath = "local/temp";
    private static String suffix = ".temp";
    private static String sharedTable = "localData";
    private static String dbName = "tabUtil";

	public static String getDbName() {
		return dbName;
	}

	public static void setDbName(String dbName) {
		Config.dbName = dbName;
	}

    public static String getDefaultPath() {
        return defaultPath;
    }

    public static void setDefaultPath(String defaultPath) {
        Config.defaultPath = defaultPath;
    }

    public static String getSuffix() {
        return suffix;
    }

    public static void setSuffix(String suffix) {
        Config.suffix = suffix;
    }

    public static String getSharedTable() {
        return sharedTable;
    }

    public static void setSharedTable(String sharedTable) {
        Config.sharedTable = sharedTable;
    }
}
