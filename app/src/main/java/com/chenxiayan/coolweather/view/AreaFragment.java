package com.chenxiayan.coolweather.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxiayan.coolweather.R;
import com.chenxiayan.coolweather.db.City;
import com.chenxiayan.coolweather.db.County;
import com.chenxiayan.coolweather.db.Provinces;
import com.chenxiayan.coolweather.util.HttpUtil;
import com.chenxiayan.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**将查询出来的省市县列表分别放在不同List中。
 * 点击某个选项，根据当前不同的页面，来判断点击的是哪个省市县。
 * 再去查询相应的列表
 * Created by chenxiaoyan on 2018/3/21.
 */

public class AreaFragment extends Fragment {
    private static final String TAG = "AreaFragment";

    //省份  lEVLE = 0
    public static final int LEVEL_PROVINCE = 0 ;
    //市    LEVEL = 1;
    public static final int LEVEL_CITY = 1;
    //县   LEVEL = 2；
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    /***
     * 绑定ListView的适配器,保存当前显示省市县列表的数据
     */
    private List<String> dataList = new ArrayList<>();

    /***
     * 省列表
     */
    private List<Provinces> provincesList;

    /***
     * 市列表
     */
    private List<City> cityList;

    /***
     * 县列表
     */
    private List<County> countyList;

    /***
     *选中的省份
     */
    private Provinces selectedProvinces;

    /***
     * 选中的城市
     */
    private City selectedCity;

    /***
     * 选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /***
                 * 记录选中的省份
                 */
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvinces = provincesList.get(position);
                    queryCities();

                    /***
                     * 记录选中的市  selectedCitty
                     */
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();

                    /***
                     * 如果当前LEVEL为COUNTY,则启动weatherActivity
                     */
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    /***
                     * 判断碎片属于哪个类的实例
                     */
                    if (getActivity() instanceof  MainActivity){

                        Log.d(TAG, "onItemClick: "+weatherId);
                        Intent intent = new Intent(getActivity(),weatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof weatherActivity){
                        weatherActivity activity = (weatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        /***
         * 初始化时直接查询省
         */
        queryProvinces();
    }

    /***
     * 查询对应省份的市 优先从数据库中查询，没有再从网络上查找
     */
    private void queryCities() {
        titleText.setText(selectedProvinces.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        /***
         * 从数据库查找  selectedProvinces.getId()
         */
        cityList = DataSupport.where("ProvincedId = ?",String.valueOf(selectedProvinces.getId())).find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvinces.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            Log.d(TAG, "queryCities: "+ address);
            queryFromServer(address,"city");
        }
    }

    /***
     * 查询对应市的县 优先从数据库中查询，没有再从网络上查找
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size() > 0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvinces.getProvinceCode();
            int cityCode = selectedCity.getCitycode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            Log.d(TAG, "queryCounties: "+ address);
            queryFromServer(address,"county");
        }
    }

    /***
     * 查询全国所有的省 优先从数据库中查询，没有再从网络上查找
     */
    private void queryProvinces() {
        titleText.setText("中国");
        /***
         * 显示为省份，则隐藏backButton。
         */
        backButton.setVisibility(View.GONE);
        provincesList = DataSupport.findAll(Provinces.class);
        if(provincesList.size() > 0){
            dataList.clear();
            for(Provinces province : provincesList){
                dataList.add(province.getProvinceName());
            }
            /***
             * 刷新adapter数据
             */
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /***
     * 根据传入的地址和类型从服务器查询省市县数据
     * @param address   地址
     * @param type      类型
     */
    private void queryFromServer(final String address, final String type) {
        //网络查询数据,显示progressDialog
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //获取Activity对象 并进行ui操作
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.hanleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.hanleCityResponse(responseText,selectedProvinces.getId());
                }else if("county".equals(type)){
                    result = Utility.hanleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){
                    /***
                     * 解析完成以后再从数据库中查找
                     */
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /***
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /***
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}
