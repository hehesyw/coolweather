package com.syw.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.syw.coolweather.model.City;
import com.syw.coolweather.model.County;
import com.syw.coolweather.model.Province;

public class CoolWeatherDB {
	//数据库名
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
	 * 将province实例存储到数据库
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name",province.getProvinceName());
			db.insert("Province", null, values);
		}
	}
	/**
	 * 从数据库读取全国所有省份信息
	 */
	public List<Province> loadProvince(){
		List<Province> list=new ArrayList<Province>();
		
		Cursor cursor=db.query("Province",null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			do{
				Province province=new Province();
				//Log.d("loadProvince", cursor.getString(cursor.getColumnIndex("province_name")));
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				list.add(province);
				for(Province province2:list){
					Log.d("loadProvince", "no"+province2.getProvinceName());
				}
			}while(cursor.moveToNext());
		}
		for(Province province1:list){
			Log.d("loadProvince", "yes"+province1.getProvinceName());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	/*
	 * 将City实例保存到数据库中
	 */
	public void saveCity(City city){
		if(city!=null){
			Log.d("savecity", "savecity executed");
			Log.d("savecity",city.getCityName() );
			Log.d("savecity", city.getProvinceName());
			ContentValues values=new ContentValues();
			values.put("city_name",city.getCityName());
			values.put("province_name", city.getProvinceName());
			//values.put("id", city.getId());
			db.insert("City",null, values);
		}
	}
	/*
	 * 读取某个省下的所有市
	 */
	public List<City> loadCity(String provinceName){
		List<City> list=new ArrayList<City>();
		
		Cursor cursor=db.query("City", null, "province_name=?", new String[]{provinceName}, null,null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setProvinceName(provinceName);
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	/*
	 * 将county实例保存到数据库中
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values=new ContentValues();
			values.put("county_name",county.getCountyName());
			values.put("city_name",county.getCityName());
			db.insert("County", null, values);
		}
	}
	/*
	 * 读取某个城市下的所有县
	 */
	public List<County> loadCounty(String cityName){
		List<County> list=new ArrayList<County>();
		
		Cursor cursor=db.query("County", null,"city_name=?",new String[]{cityName},null,null, null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCityName(cityName);
				list.add(county);
			}while(cursor.moveToNext());
			
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	
	
	
	
}
