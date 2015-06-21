package com.syw.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.syw.coolweather.model.City;
import com.syw.coolweather.model.County;
import com.syw.coolweather.model.Province;

public class CoolWeatherDB {
	//���ݿ���
	public static final String DB_NAME="cool_weather";
	public static final int VERSION=1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper helper=new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=helper.getWritableDatabase();
	}
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB==null){
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/**
	 * ��provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name",province.getProvinceName());
			values.put("province_code",province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	/**
	 * �����ݿ��ȡȫ������ʡ����Ϣ
	 */
	public List<Province> loadProvince(){
		List<Province> list=new ArrayList<Province>();
		Province province=new Province();
		Cursor cursor=db.query("Province",null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			do{
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	/*
	 * ��Cityʵ�����浽���ݿ���
	 */
	public void saveCity(City city){
		if(city!=null){
			ContentValues values=new ContentValues();
			values.put("city_name",city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			values.put("id", city.getId());
			db.insert("City",null, values);
		}
	}
	/*
	 * ��ȡĳ��ʡ�µ�������
	 */
	public List<City> loadCity(int provinceId){
		List<City> list=new ArrayList<City>();
		City city=new City();
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null,null, null);
		if(cursor.moveToFirst()){
			do{
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	/*
	 * ��countyʵ�����浽���ݿ���
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values=new ContentValues();
			values.put("county_name",county.getCountyName());
			values.put("id",county.getId());
			values.put("county_code",county.getCountyCode());
			values.put("city_id",county.getCityId());
			db.insert("County", null, values);
		}
	}
	/*
	 * ��ȡĳ�������µ�������
	 */
	public List<County> loadCounty(int city_id){
		List<County> list=new ArrayList<County>();
		County county=new County();
		Cursor cursor=db.query("County", null,"city_id=?",new String[]{String.valueOf(city_id)},null,null, null);
		if(cursor.moveToFirst()){
			do{
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(city_id);
				list.add(county);
			}while(cursor.moveToNext());
			
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	
	
	
	
}
