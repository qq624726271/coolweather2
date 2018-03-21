package com.chenxiayan.coolweather.db;

import org.litepal.crud.DataSupport;

/**provinceId给数据库查找
 * Created by chenxiaoyan on 2018/3/21.
 */

public class City extends DataSupport {
    private int id ;
    private String cityName;
    private int citycode;
    private int provincedId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCitycode() {
        return citycode;
    }

    public void setCitycode(int citycode) {
        this.citycode = citycode;
    }

    public int getProvincedId() {
        return provincedId;
    }

    public void setProvincedId(int provincedId) {
        this.provincedId = provincedId;
    }

}
