package com.chenxiayan.coolweather.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.chenxiayan.coolweather.db.City;
import com.chenxiayan.coolweather.db.County;
import com.chenxiayan.coolweather.db.Provinces;
import com.chenxiayan.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chenxiaoyan on 2018/3/21.
 */

public class Utility {
    /***
     * 解析省级数据
     * @param response
     * @return
     */
    private static final String TAG = "Utility";
    public static boolean hanleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)) {  //判断是否为空
            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0 ; i < allProvinces.length(); i ++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Provinces provinces = new Provinces();
                    provinces.setProvinceName(provinceObject.getString("name"));  //获取省名 "name":"xx"
                    provinces.setProvinceCode(provinceObject.getInt("id"));
                    provinces.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /***
     * 解析市级数据并保存数据库中
     */
    public static boolean hanleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)) {  //判断是否为空
            try {
                JSONArray allcities = new JSONArray(response);
                for(int i = 0 ; i < allcities.length(); i ++){
                    JSONObject cityObject = allcities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));  //获取省名 "name":"xx"
                    city.setCitycode(cityObject.getInt("id"));
                    city.setProvincedId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /***
     * 解析县级数据并保存数据库中
     */

    public static boolean hanleCountyResponse(String response,int cityId){
        Log.d(TAG, "hanleCountyResponse: "+ response);
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for(int i = 0 ; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /***
     * 将返回的JSON解析成Weather实体类
     */
    @Nullable
    public static Weather hanleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.d(TAG, "hanleWeatherResponse: "+weatherContent);
            Gson gson = new Gson();
            Weather weather = gson.fromJson(weatherContent,Weather.class);
            Log.d(TAG, "hanleWeatherResponse: "+weather.toString());
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
