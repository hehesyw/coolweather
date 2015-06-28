package com.syw.coolweather.util;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;
import android.util.Log;

import com.syw.coolweather.db.CoolWeatherDB;
import com.syw.coolweather.model.City;
import com.syw.coolweather.model.County;
import com.syw.coolweather.model.Province;

public class Utility {
	/*
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			try{
			XmlPullParserFactory xmlPullParserFactory=XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser=xmlPullParserFactory.newPullParser();
			xmlPullParser.setInput(new StringReader(response));
			int eventType=xmlPullParser.getEventType();
			String name="";
			while(eventType!=XmlPullParser.END_DOCUMENT){
				Province province=new Province();
				String nodeName=xmlPullParser.getName();
				switch(eventType){
				case XmlPullParser.START_TAG:
					if("ProvinceName".equals(nodeName)){
						name=xmlPullParser.nextText();
						province.setProvinceName(name);
						coolWeatherDB.saveProvince(province);
					}
					break;
				case XmlPullParser.END_TAG:
					if("China".equals(nodeName)){
						Log.d("MainActivity","name is :"+name);
					}
					break;
				default:
					break;
				}
				eventType=xmlPullParser.next();
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,String provinceName){
		
			if(!TextUtils.isEmpty(response)){
				try{
				XmlPullParserFactory xmlPullParserFactory=XmlPullParserFactory.newInstance();
				XmlPullParser xmlPullParser=xmlPullParserFactory.newPullParser();
				xmlPullParser.setInput(new StringReader(response));
				int eventType=xmlPullParser.getEventType();
				String name="";
				int flag=0;
				while(eventType!=XmlPullParser.END_DOCUMENT){
					String nodeName=xmlPullParser.getName();
					
					switch(eventType){
					case XmlPullParser.START_TAG:
						if("ProvinceName".equals(nodeName)){
							if(xmlPullParser.nextText().equals(provinceName)){
							flag=1;
							}
						}
						if("CityName".equals(nodeName)&&(flag==1)){
							name=xmlPullParser.nextText();
							Log.d("MainActivity","name is :"+name);
							City city=new City();
							city.setCityName(name);
							city.setProvinceName(provinceName);
							coolWeatherDB.saveCity(city);
						}
						break;
					case XmlPullParser.END_TAG:
						if("Province".equals(nodeName)){
							//Log.d("MainActivity","name is :"+name);
							flag=0;
						}
						break;
					default:
						break;
					}
					
					eventType=xmlPullParser.next();
					
					
				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		return true;
	}
	/*
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,String cityName){
		if(!TextUtils.isEmpty(response)){
			try{
			XmlPullParserFactory xmlPullParserFactory=XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser=xmlPullParserFactory.newPullParser();
			xmlPullParser.setInput(new StringReader(response));
			int eventType=xmlPullParser.getEventType();
			String name="";
			int flag=0;
			while(eventType!=XmlPullParser.END_DOCUMENT){
				String nodeName=xmlPullParser.getName();
				switch(eventType){
				case XmlPullParser.START_TAG:
					if("CityName".equals(nodeName)){
						if(xmlPullParser.nextText().equals(cityName)){
						flag=1;
						}
					}
					if("Area".equals(nodeName)&&(flag==1)){
						name=xmlPullParser.nextText();
						Log.d("MainActivity","name is :"+name);
						County county=new County();
						county.setCountyName(name);
						county.setCityName(cityName);
						coolWeatherDB.saveCounty(county);
					}
					break;
				case XmlPullParser.END_TAG:
					if("City".equals(nodeName)){
						//Log.d("MainActivity","name is :"+name);
						flag=0;
					}
					break;
				default:
					break;
				}
				
				eventType=xmlPullParser.next();
				
				
			}
		
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
