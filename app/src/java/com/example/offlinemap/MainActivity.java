package com.example.offlinemap;

import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 配置 osmdroid 读取你手机里的离线包
        Configuration.getInstance().setOsmdroidBasePath(
            new File(Environment.getExternalStorageDirectory(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(
            new File(Environment.getExternalStorageDirectory(), "osmdroid/tiles"));

        setContentView(R.layout.activity_main);
        map = findViewById(R.id.map);

        // 【关键】名字必须是 "MapQuest"，后缀必须是 ".jpg"
        // 因为 MOBAC 生成的 zip 里面文件夹叫 MapQuest，图片是 .jpg
        XYTileSource offlineSource = new XYTileSource(
            "MapQuest", 0, 18, 256, ".jpg", new String[]{}
        );
        map.setTileSource(offlineSource);
        map.setUseDataConnection(false); // 强制离线
        map.setMultiTouchControls(true);

        // 设置中心点（改成你 MOBAC 选的区域中心，这里是巴格达）
        map.getController().setZoom(13.0);
        map.getController().setCenter(new GeoPoint(33.3152, 44.3661));
    }

    @Override protected void onResume() { super.onResume(); map.onResume(); }
    @Override protected void onPause() { super.onPause(); map.onPause(); }
}
