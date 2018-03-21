package com.chenxiayan.coolweather.db;

import org.litepal.crud.DataSupport;

/**cityId 给数据库去查找
 * Created by chenxiaoyan on 2018/3/21.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherId;   //对应天气id
    private int cityId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

}
