package com.yuanshi.hiorange.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.model.PresenterFactory;
import com.yuanshi.hiorange.util.TimesCalculator;
import com.yuanshi.hiorange.activity.MainActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author zyc
 * @date 2018/2/9
 */

public class LocationFragment extends Fragment implements AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener
        , ILocationView {

    private static final String TAG = "LocationFragment";
    @BindView(R.id.map)
    MapView mMap;
    @BindView(R.id.iv_location_path)
    ImageView mIvLocationPath;
    @BindView(R.id.iv_location_location)
    ImageView mIvLocationLocation;
    @BindView(R.id.iv_location_box)
    ImageView mIvLocationBox;
    Unbinder unbinder;

    MyLocationStyle mMyLocationStyle;
    AMapLocationClient mAMapLocationClient;
    AMapLocationClientOption mOption;

    private AMap mAMap;
    private double personLat;
    private double personLng;

    private double boxLat;
    private double boxLng;

    private String mSnippet;

    private Activity mActivity;
    private String mPhoneNumber;
    private String mBoxId;
    private AlertDialog mGpsDialog;
    private final long TIME_OUT = 10 * 1000;
    private final long DISABLE_TIME = 60 * 1000;
    private String getTime;
    private PresenterFactory.GetGPSPresenter mGetGPSPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity != null) {
            mPhoneNumber = ((MainActivity) mActivity).getPhoneNumber();
            mBoxId = ((MainActivity) mActivity).getBoxId();
        }
        Log.e(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_fragment, null);
        unbinder = ButterKnife.bind(this, view);
        mMap.onCreate(savedInstanceState);
        mAMap = mMap.getMap();
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnInfoWindowClickListener(this);

        mGpsDialog = new AlertDialog.Builder(mActivity).setCancelable(false).setMessage("获取箱子位置中").create();
        mGetGPSPresenter = PresenterFactory.createGetGPSPresenter(mPhoneNumber, mBoxId);

        initAmap();
        initLocation();
        return view;
    }


    @OnClick({R.id.iv_location_box, R.id.iv_location_location, R.id.iv_location_path})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_location_box:
                mGpsDialog.show();
                getTime = TimesCalculator.getStringDate();
                mGetGPSPresenter.doRequest( this);
                break;
            case R.id.iv_location_location:

                mAMapLocationClient.startLocation();
                break;
            case R.id.iv_location_path:
                //test

                break;
            default:
                break;
        }
    }

    /**
     * 自定义marker样式
     */
    private void initMarker() {

        List<Marker> marker = mAMap.getMapScreenMarkers();
        for (Marker marker1 : marker) {
            marker1.remove();
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_icon_box);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(boxLat, boxLng));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.draggable(true);
        markerOptions.visible(true);
        markerOptions.period(50);
        //设置marker平贴地图效果
        markerOptions.setFlat(true);


        //获取箱子位置坐标
        mAMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(22.590000, 113.992922)));
        Marker markerBox = mAMap.addMarker(markerOptions);

        Animation animation = new RotateAnimation(markerBox.getRotateAngle(), markerBox.getRotateAngle() + 360, 0, 0, 0);
        long duration = 500L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        markerBox.setAnimation(animation);
        markerBox.startAnimation();
    }

    /**
     * 监听Marker信息窗点击事件
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
    }

    /**
     * 监听Marker点击事件
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        marker.showInfoWindow();
        marker.setSnippet(mSnippet);

        Log.e(TAG, "onMarkerClick: " + marker.getSnippet());
        return false;
    }

    /**
     * 初始化地图Amap
     */
    private void initAmap() {
        mMyLocationStyle = new MyLocationStyle();
        mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        mMyLocationStyle.showMyLocation(true);
        mMyLocationStyle.strokeColor(Color.TRANSPARENT);
//        自定义icon
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_icon_normal);
        mMyLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        mAMap.setMyLocationStyle(mMyLocationStyle);
        mAMap.setMyLocationEnabled(true);
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);

    }

    /**
     * 初始化AmapLocationClient
     */
    private void initLocation() {
        mAMapLocationClient = new AMapLocationClient(getActivity());

        mOption = new AMapLocationClientOption();
        mOption.setNeedAddress(true);
        mAMapLocationClient.setLocationListener(this);
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setOnceLocation(true);
        mAMapLocationClient.setLocationOption(mOption);
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getAddress();
                mSnippet = amapLocation.getAoiName();
                //获取纬度
                personLat = amapLocation.getLatitude();
                //获取经度
                personLng = amapLocation.getLongitude();
                Log.e(TAG, "定位OK" + "纬度： " + personLat + "经度：" + personLng);

                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(personLat, personLng), 18));


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);
        if (mAMapLocationClient != null) {
            if (hidden) {
                mAMapLocationClient.stopLocation();
                return;
            }
            mAMapLocationClient.startLocation();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onDestroy() {
        if (mGpsDialog != null && mGpsDialog.isShowing()) {
            mGpsDialog.dismiss();
            mGpsDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMap.onDestroy();
        mAMapLocationClient.stopLocation();
        unbinder.unbind();
    }

    @Override
    public void getGPSSucceed(String result, @NonNull String lat, @NonNull String lng, @NonNull String cmdTime) {

        String currentTime = TimesCalculator.getStringDate();
        //从开始申请到现在所花的时间
        long spendTime = TimesCalculator.calculateSeconds(currentTime, getTime);
        //服务器指令上传的时间和申请的时间
        long difference = TimesCalculator.calculateSeconds(getTime, cmdTime);
        //若获取时间大于 DISABLE_TIME ，属于无效数据，重新获取
        if (spendTime < TIME_OUT) {
            if (difference > DISABLE_TIME && !("").equals(lat) && !("").equals(lng)) {
                boxLat = Double.valueOf(lat);
                boxLng = Double.valueOf(lng);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGpsDialog != null && mGpsDialog.isShowing()) {
                            mGpsDialog.dismiss();
                        }
                        initMarker();
                    }
                });
            } else {
                try {
                    Thread.sleep(1000);
                    mGetGPSPresenter.doRequest(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //超时
            ((MainActivity) mActivity).showToast(mActivity, "箱子无响应，获取箱子位置超时");
            getGPSFailed("箱子无响应，获取箱子位置超时");
        }
    }

    @Override
    public void getGPSFailed(String result) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mGpsDialog != null && mGpsDialog.isShowing()) {
                    mGpsDialog.dismiss();
                }
            }
        });

    }
}
