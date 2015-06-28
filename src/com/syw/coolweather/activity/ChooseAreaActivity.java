package com.syw.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.syw.coolweather.R;
import com.syw.coolweather.db.CoolWeatherDB;
import com.syw.coolweather.model.City;
import com.syw.coolweather.model.County;
import com.syw.coolweather.model.Province;
import com.syw.coolweather.util.HttpCallbackListener;
import com.syw.coolweather.util.HttpUtil;
import com.syw.coolweather.util.Utility;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	private TextView textView;
	private ListView listView;
	private List<String> dataList=new ArrayList<String>();
	private CoolWeatherDB coolWeatherDB;
	private ArrayAdapter<String> adapter;
	private List<Province> provinceList;
	private Province selectedProvince;
	private City selectedCity;
	private List<City> cityList;
	private ProgressDialog progressDialog;
	private int currentLevel;
	private List<County> countyList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		textView=(TextView)findViewById(R.id.title_text);
		listView=(ListView)findViewById(R.id.list_view);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentLevel==LEVEL_PROVINCE){
				selectedProvince=provinceList.get(position);
				queryCities();
				}else if(currentLevel==LEVEL_CITY){
					selectedCity=cityList.get(position);
					queryCounties();
				}
				
			}
		});
		queryProvinces();
	}
	
	

	private void queryProvinces() {
		Log.d("choose", "queryProvinces executed");
		provinceList=coolWeatherDB.loadProvince();
		if(provinceList.size()>0){
			Log.d("choose", "provincelist size is "+provinceList.size());
			dataList.clear();
			for(Province province:provinceList){
				Log.d("choose", "province name is "+province.getProvinceName());
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	protected void queryCities() {
		cityList=coolWeatherDB.loadCity(selectedProvince.getProvinceName());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			
			queryFromServerCity(null,selectedProvince.getProvinceName());
		}
	}

	protected void queryCounties() {
		countyList=coolWeatherDB.loadCounty(selectedCity.getCityName());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromSeverCounty(selectedCity.getCityName());
		}
	}

	

	private void queryFromServer(String name, String string) {
		Log.d("choose", "queryFromServer executed");
		String address="http://192.168.191.1:8080/ProvinceAndCity.xml";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				result=Utility.handleProvincesResponse(coolWeatherDB, response);
				Log.d("sendHttpRequest", result+"");
				if(result){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							closeProgressDialog();
							queryProvinces();
						}

						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void queryFromServerCity(String name, final String provinceName) {
		String address="http://192.168.191.1:8080/ProvinceAndCity.xml";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result=false;
				Log.d("querycity", response);
				result=Utility.handleCitiesResponse(coolWeatherDB, response,provinceName);
				if(result){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							closeProgressDialog();
							queryCities();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void queryFromSeverCounty(final String cityName) {
		String address="http://192.168.191.1:8080/ProvinceAndCity.xml";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result=false;
				result=Utility.handleCountiesResponse(coolWeatherDB, response, cityName);
				if(result){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							closeProgressDialog();
							queryCounties();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void showProgressDialog() {
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载.....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	private void closeProgressDialog() {
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else if(currentLevel==LEVEL_COUNTY){
			queryCities();
		} else{
			finish();
		}
		
	}
}
