package com.mhh.httputils.tab.utils;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import com.mhh.httputils.tab.utils.annotations.FieldNameAnnotation;
import com.mhh.httputils.tab.utils.annotations.TableNameAnnotation;
import com.mhh.httputils.tab.utils.bean.RulePair;
import com.mhh.httputils.tab.utils.bean.SqlBaseBean;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * @author �ϻԻ�
 * SQLite������
 */
public class SqliteUtils {

	private final String TYPE_TEXT = "text";
	private final String TYPE_INTEGER = "integer";
	private final String TYPE_DOUBLE = "double";
	private final String TYPE_REAL = "real";
	private final String TYPE_BLOB = "blob";
	private final String TYPE_DATE = "date";
	
	private final String idString = "_id";
	
	private static SqliteUtils tools;
	private SQLiteDatabase db;  
	
	/**
	 * @param context �����Ķ���
	 * @return  ��ȡSqliteUtils�ĵ�������
	 */
	public static SqliteUtils getInstance(Context context){
		if (tools == null) {
			tools = new SqliteUtils(context);
		}
		return tools;
	}
	
	public SqliteUtils(Context context){
		db = context.openOrCreateDatabase(Config.getDbName(), Context.MODE_PRIVATE, null);  
	}
	
	/**
	 * ӳ��ֵ��Ӧ��ֵ
	 * @param key �ֶ�����
	 * @param object ��Ӧ�ֶε�ֵ
	 * @param bean ʵ�����
	 * @throws Exception �쳣��Ϣ
	 */
	public <T> void reflectValue(String key, Object object, T bean) throws Exception {
		Class<?> typeClass = bean.getClass();
		Field field = typeClass.getDeclaredField(key);
		field.setAccessible(true);
		field.set(bean, object);
	}
	
	/**
	 * ������
	 * @param typeClass ��Ӧ��ʵ��������
	 * @throws Exception �쳣��Ϣ
	 */
	public <T extends SqlBaseBean> void createTable(Class<?> typeClass) throws Exception {
		if (typeClass == null) {
			return;
		}
		String tableName = getTableName(typeClass);
		
		if (isExistTable(tableName)) {
			return;
		}
		
		Field[] fields = typeClass.getDeclaredFields();
		db.execSQL("drop table if exists " + tableName);  
        String sql = "create table " + tableName +" ("+ idString +" integer primary key autoincrement,";
        for (int i = 0; i < fields.length; i++) {
			if (isAvailableName(fields[i].getName())) {
				sql += " " + getFieldName(fields[i]) + " " + getVarTypeName(fields[i].getType()) + ",";
			}
		}
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        if (Config.isDebug) {			
        	System.out.println("create table is : " + sql);
		}
        db.execSQL(sql);  
        if (Config.isDebug) {			
        	System.out.println("create table " + tableName + " success!");
		}
	}
	
	/**
	 * ɾ����
	 * @param typeClass ɾ�����Ӧ����
	 */
	public void deleteTable(Class<?> typeClass) {
		if (typeClass == null) {
			return;
		}
		
		String tableName = getTableName(typeClass);
		
		db.execSQL("drop table if exists " + tableName);  
		if (Config.isDebug) {			
			System.out.println("Table " + tableName + " delete success!");
		}
	}
	
	/**
	 * ��������ʵ�嵽���ݿ⡣������µ�ʵ�壬��ô�ͽ��в�����������������ʵ����ô�����Զ����и��²�����
	 * @param bean ʵ�����
	 * @throws Exception �쳣��Ϣ
	 */
	public <T extends SqlBaseBean> void saveData(T bean) throws Exception {
		int id = bean.get_id();
		if (id > 0) {
			updateData(bean);
		}else {
			insertData(bean);
		}
	}
	
	/**
	 * �������ݲ���
	 * @param bean ����ʵ�����
	 * @throws Exception �쳣��Ϣ
	 */
	public <T extends SqlBaseBean> void insertData(T bean) throws Exception {
		
		if (bean == null) {
			return;
		}
		String sql = "insert into " + getTableName(bean.getClass()) + "(";
		String names = "";
		String values = "";
		Field[] fields = bean.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = getFieldName(fields[i]);
			if (isAvailableName(fieldName)) {
				names += fieldName +",";
				fields[i].setAccessible(true);
				if (getVarTypeName(fields[i].getType()).equals(TYPE_BLOB)) {
					System.out.println("value is : " + fields[i].get(bean));
					int temp = fields[i].get(bean).toString().equals("true") ? 1 : 0;
					System.out.println("value is : " + temp);
					values += temp+",";
				}else {					
					values += "\""+fields[i].get(bean)+"\",";
				}
				
			}
		}
		names = names.substring(0,names.length()-1);
		values = values.substring(0,values.length()-1);
		sql += names + ") values(" + values + ")";
        if (Config.isDebug) {			
        	System.out.println("insert sql is : " + sql);
		}
		db.execSQL(sql);
        if (Config.isDebug) {			
        	System.out.println("insert sql is success!");
		}
	}
	
	/**
	 * �������ݲ���
	 * @param bean ʵ�����
	 * @throws Exception �쳣��Ϣ
	 */
	public <T extends SqlBaseBean> void updateData(T bean) throws Exception {
		
		if (bean == null) {
			return;
		}

		String sql = "update " + getTableName(bean.getClass()) + " set ";
		String contentsString = "";
		Field[] fields = bean.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = getFieldName(fields[i]);
			if (isAvailableName(fieldName)) {
				contentsString += fieldName + "=";
				fields[i].setAccessible(true);
				if (getVarTypeName(fields[i].getType()).equals(TYPE_BLOB)) {
					System.out.println("value is : " + fields[i].get(bean));
					int temp = fields[i].get(bean).toString().equals("true") ? 1 : 0;
					System.out.println("value is : " + temp);
					contentsString += temp+",";
				}else {					
					contentsString += "\""+fields[i].get(bean)+"\",";
				}
				
			}
		}
		contentsString = contentsString.substring(0,contentsString.length()-1);
		sql += contentsString + " where "+ idString + " = " + bean.get_id();;
        if (Config.isDebug) {			
        	System.out.println("update sql is : " + sql);
		}
		db.execSQL(sql);
        if (Config.isDebug) {			
        	System.out.println("update sql is success!");
		}
	}		
	
	/**
	 * ����ʵ��������������ݲ�ѯ
	 * @param beanType ʵ����
	 * @return ���ز�ѯ�������ݽ��
	 * @throws Exception �쳣��Ϣ
	 */
	@SuppressWarnings("unchecked")
	public <T extends SqlBaseBean> ArrayList<T> queryList(Class<?> beanType) throws Exception {
		ArrayList<T> dataList = new ArrayList<T>();
		if (beanType == null) {
			return dataList;
		}
		String sql = "select * from " + getTableName(beanType);
        if (Config.isDebug) {			
        	System.out.println("Select sql is : " + sql);
		}
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			T bean = (T) beanType.newInstance();
			String[] names = cursor.getColumnNames();
			for (int i = 0; i < names.length; i++) {	
				if (names[i].equals(idString)) {	
					int index = cursor.getColumnIndex(idString);
					int intValue = cursor.getInt(index);
					bean.set_id(intValue);
				}else {
					reflectValueFromName(names[i], bean, cursor);	
				}			
			}
			dataList.add(bean);
			cursor.moveToNext();
		}
		cursor.close();
		return dataList;
	}
	
	/**
	 * ����ĳ���ֶν��в�ѯ
	 * @param beanType ��ǰ�������ʵ��
	 * @param whereName �����ֶε�����
	 * @param whereValue �����ֶε�ֵ
	 * @return ���ز�ѯ�Ľ����
	 * @throws Exception �쳣��Ϣ
	 */
	@SuppressWarnings("unchecked")
	public <T extends SqlBaseBean> ArrayList<T> queryList(Class<?> beanType, String whereName, Object whereValue) throws Exception {
		ArrayList<T> dataList = new ArrayList<T>();
		if (beanType == null) {
			return dataList;
		}
		
		String sql = "select * from " + getTableName(beanType) + " where " + getSqlFieldName(whereName, beanType) + " = " + whereValue;
        if (Config.isDebug) {			
        	System.out.println("Select sql is : " + sql);
		}
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			T bean = (T) beanType.newInstance();
			String[] names = cursor.getColumnNames();
			for (int i = 0; i < names.length; i++) {	
				if (names[i].equals(idString)) {	
					int index = cursor.getColumnIndex(idString);
					int intValue = cursor.getInt(index);
					bean.set_id(intValue);
				}else {
					reflectValueFromName(names[i], bean, cursor);	
				}				
			}
			dataList.add(bean);
			cursor.moveToNext();
		}
		cursor.close();
		return dataList;
	}
	
	/**
	 * 
	 * ���ݶ��������в�ѯ
	 * @param beanType ���Ӧ������ʵ��
	 * @param rules ��ѯ��������
	 * @return ��ѯ������ݼ�
	 * @throws Exception �쳣��Ϣ
	 */
	@SuppressWarnings("unchecked")
	public <T extends SqlBaseBean> ArrayList<T> queryList(Class<?> beanType, RulePair[] rules) throws Exception {
		ArrayList<T> dataList = new ArrayList<T>();
		if (beanType == null) {
			return dataList;
		}
		if (rules == null || rules.length == 0) {
			return queryList(beanType);
		}
		String ruleString = "";
		for (int i = 0; i < rules.length; i++) {
			ruleString += " " + getSqlFieldName(rules[i].getWhereName(), beanType) + " = " + rules[i].getWhereValue()+" and";
		}
		ruleString = ruleString.substring(0, ruleString.length()-3);//ȥ�����һ��and
		String sql = "select * from " + getTableName(beanType) + " where" + ruleString;
        if (Config.isDebug) {			
        	System.out.println("Select sql is : " + sql);
		}
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			T bean = (T) beanType.newInstance();
			String[] names = cursor.getColumnNames();
			for (int i = 0; i < names.length; i++) {	
				if (names[i].equals(idString)) {	
					int index = cursor.getColumnIndex(idString);
					int intValue = cursor.getInt(index);
					bean.set_id(intValue);
				}else {
					reflectValueFromName(names[i], bean, cursor);	
				}				
			}
			dataList.add(bean);
			cursor.moveToNext();
		}
		cursor.close();
		return dataList;
	}
	
	/** 
	 * ����_id��ѯ����ʵ��
	 * @param beanType ���Ӧ��ʵ����
	 * @param whereId ��ѯ������_idֵ
	 * @return ���ز�ѯ��������ʵ�����
	 * @throws Exception �쳣��Ϣ
	 */
	@SuppressWarnings("unchecked")
	public <T extends SqlBaseBean> T queryBean(Class<?> beanType, int whereId) throws Exception {
		if (beanType == null) {
			throw new NullPointerException();
		}
		T bean = (T) beanType.newInstance();
		String sql = "select * from " + getTableName(beanType) + " where " + idString + " = " + whereId;
        if (Config.isDebug) {			
        	System.out.println("Select sql is : " + sql);
		}
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String[] names = cursor.getColumnNames();
			for (int i = 0; i < names.length; i++) {
				if (names[i].equals(idString)) {	
					int index = cursor.getColumnIndex(idString);
					int intValue = cursor.getInt(index);
					bean.set_id(intValue);
				}else {
					reflectValueFromName(names[i], bean, cursor);	
				}
			}
			cursor.moveToNext();
		}
		cursor.close();
		return bean;
	}
	
	/**
	 * ����_idɾ������
	 * @param whereId ����_id��ֵ
	 * @param typeBean ���Ӧ��ʵ����
	 */
	public void deleteDataRow(int whereId, Class<?> typeBean) {
		String sql = "delete from "+ getTableName(typeBean) +" where " + idString +" = " + whereId; 
		if (Config.isDebug) {
			System.out.println("delete sql is : " + sql);
		}
		db.execSQL(sql);
	}
	
	/**
	 * ����ĳһ�ֶ�����ɾ������
	 * @param whereName �����ֶε�����
	 * @param whereValue ��Ӧ�����ֶε�ֵ
	 * @param typeBean ���Ӧ������ʵ��
	 */
	public void deleteDataRow(String whereName, Object whereValue, Class<?> typeBean) {
		String sql = "delete from "+ getTableName(typeBean) +" where " + getSqlFieldName(whereName, typeBean) +" = " + whereValue; 
		if (Config.isDebug) {
			System.out.println("delete sql is : " + sql);
		}
		db.execSQL(sql);
	}
	
	/**
	 * ���ݹ������ɾ������
	 * @param rules �������
	 * @param typeBean ���Ӧ������ʵ��
	 */
	public void deleteDataRow(RulePair[] rules, Class<?> typeBean) {
		if (typeBean == null) {
			return;
		}
		if (rules == null || rules.length == 0) {
			return;
		}
		String ruleString = "";
		for (int i = 0; i < rules.length; i++) {
			ruleString += " " + getSqlFieldName(rules[i].getWhereName(), typeBean) + " = " + rules[i].getWhereValue()+" and";
		}
		ruleString = ruleString.substring(0, ruleString.length()-3);//ȥ�����һ��and
		String sql = "delete from "+ getTableName(typeBean) +" where" + ruleString; 
		if (Config.isDebug) {
			System.out.println("delete sql is : " + sql);
		}
		db.execSQL(sql);
	}
	
	/**
	 * �����ֶ����ƶ�����ʵ����и�ֵ
	 * @param name �ֶ�����
	 * @param bean ����ʵ��
	 * @param cursor ���ݿ��ѯ�������
	 * @throws Exception �쳣��Ϣ
	 */
	private <T extends SqlBaseBean> void reflectValueFromName(String name, T bean, Cursor cursor) throws Exception {
		if (bean == null) {
			return;
		}		
		Field field = getFieldFromSqlName(name, bean.getClass());
		Class<?> type = field.getType();
		int index = cursor.getColumnIndex(name);
		field.setAccessible(true);
		if (type == String.class) {
			String valueString = cursor.getString(index);
			field.set(bean, valueString);
		}else if (type == int.class || type == Integer.class) {
			int intString = cursor.getInt(index);
			field.set(bean, intString);
		}else if (type == double.class || type == Double.class) {
			double doubleString= cursor.getDouble(index);
			field.set(bean, doubleString);
		}else if (type == float.class || type == Float.class) {
			float floatString= cursor.getFloat(index);
			field.set(bean, floatString);
		}else if (type == boolean.class || type == Boolean.class) {
			boolean booleanString= cursor.getInt(index) == 1 ? true : false;
			field.set(bean, booleanString);
		}else if (type == Date.class) {
			Date dateString= Date.valueOf(cursor.getString(index));
			field.set(bean, dateString);
		}else {
			// do nothing!
		}
	}
	
	/**
	 * ����ʵ���еı������ͻ�ȡ��Ӧ���ݿ��������������
	 * @param type ʵ���б�����������
	 * @return ���ݿ��еĶ�Ӧ��������
	 * @throws Exception �쳣��Ϣ
	 */
	private String getVarTypeName(Class<?> type) throws Exception {
		String result = "";
		
		if (type == String.class) {
			result = TYPE_TEXT;
		}else if (type == int.class || type == Integer.class) {
			result = TYPE_INTEGER;
		}else if (type == double.class || type == Double.class) {
			result = TYPE_DOUBLE;
		}else if (type == float.class || type == Float.class) {
			result = TYPE_REAL;
		}else if (type == boolean.class || type == Boolean.class) {
			result = TYPE_BLOB;
		}else if (type == Date.class) {
			result = TYPE_DATE;
		}else {
			throw new Exception("This type is not supported ---> " + type.toString());
		}
		
		return result;
	}
	
	/**
	 * ���һ��ʵ��ı������ԣ��磺serialVersionUID��Serializable��Ĭ��ID��������Ҫ���ˡ�_id���趨��ʶ������ʵ������Ĭ�ϱ������ƣ����Բ����ظ���
	 * @param name ʵ��ı�������
	 * @return �����Ƿ���õĽ��
	 */
	private boolean isAvailableName(String name) {
		
		return !name.equals("serialVersionUID") && !name.equals(idString);
	}
	
	/**
	 * ���ݱ������жϣ���ǰ���Ƿ��Ѿ����ڡ�
	 * @param tabName �������
	 * @return ���ص�ǰ���Ƿ���ڣ�true��ʾ���ڣ� ��֮�򲻴��ڡ�
	 */
	public boolean isExistTable(String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		Cursor cursor = null;
		try {

			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tabName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			if (Config.isDebug) {
				System.out.println("isExistTable Exception is : "
						+ e.toString());
			}
		}
		return result;
	}
	
	/**
	 * �ر����ݿ��������
	 */
	public void colseDataDase() {
		db.close();
	}
	
	/**
	 * ��������Ԫ�ض���
	 * @param whereName �����ֶ�����
	 * @param whereValue ����������ֶε�ֵ
	 * @return ��������Ԫ�ض���
	 */
	public RulePair createRulePair(String whereName, Object whereValue) {
		return new RulePair(whereName, whereValue);
	}
	
	public String getTableName(Class<?> typeClass) {
		String tableName = "";
		
		if (typeClass.isAnnotationPresent(TableNameAnnotation.class)) {
			TableNameAnnotation annotation = typeClass.getAnnotation(TableNameAnnotation.class);
			tableName = annotation.tableName();
		}else {				
			tableName = typeClass.getSimpleName();
		}
		return tableName;
	}
	
	public String getFieldName(Field field) {
		String fieldName = "";
		if (field == null) {
			return fieldName;
		}
		if (field.isAnnotationPresent(FieldNameAnnotation.class)) {
			FieldNameAnnotation annotation = field.getAnnotation(FieldNameAnnotation.class);
			fieldName = annotation.rename();
		}else {
			fieldName = field.getName();
		}
		
		return fieldName;
	}
	
	public Field getFieldFromSqlName(String name, Class<?> typeClass) {
		Field[] fields = typeClass.getDeclaredFields();
		Field field = null;
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(name) || getFieldName(fields[i]).equals(name)) {
				field = fields[i];
			}
		}
		return field;
	}
	
	public String getSqlFieldName(String name, Class<?> typeClass) {
		Field[] fields = typeClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(name)) {
				name = fields[i].getAnnotation(FieldNameAnnotation.class).rename();
			}
		}
		return name;
	}
}
