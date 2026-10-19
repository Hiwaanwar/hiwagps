package com.example.offlinemap;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. إعداد مسار التخزين لـ osmdroid
        Configuration.getInstance().setOsmdroidBasePath(
                new File(Environment.getExternalStorageDirectory(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(
                new File(Environment.getExternalStorageDirectory(), "osmdroid/tiles"));

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        downloadBtn = findViewById(R.id.downloadBtn);

        // 2. تعيين مصدر الخريطة إلى MapQuest المحلي
        //    يجب أن يتطابق الاسم "MapQuest" والامتداد ".jpg"
        //    مع ما هو موجود داخل ملف zip (إذا كان لديك ملف zip)
        XYTileSource offlineSource = new XYTileSource(
                "MapQuest", 0, 18, 256, ".jpg", new String[]{}
        );
        map.setTileSource(offlineSource);
        map.setMultiTouchControls(true);

        // 3. في البداية اسمح بالشبكة (لأنك بحاجة لتنزيل البلاطات أول مرة)
        map.setUseDataConnection(true);

        // 4. نقطة المركز (استبدلها بإحداثيات منطقتك، هنا بغداد كمثال)
        map.getController().setZoom(13.0);
        map.getController().setCenter(new GeoPoint(33.3152, 44.3661));

        // 5. عند النقر على الزر، ابدأ التنزيل
        downloadBtn.setOnClickListener(v -> downloadMyArea());
    }

    /**
     * تنزيل بلاطات الخريطة للمنطقة المحددة
     */
    public void downloadMyArea() {
        // ⚠️ استبدل هذه الإحداثيات بحدود منطقتك الفعلية
        // الترتيب: شمال، جنوب، شرق، غرب
        BoundingBox box = new BoundingBox(
                33.40,  // north
                33.25,  // south
                44.45,  // east
                44.25   // west
        );

        CacheManager cm = new CacheManager(map);

        Toast.makeText(this, "بدأ التنزيل...", Toast.LENGTH_SHORT).show();

        // المنطقة، الحد الأدنى للتكبير 10، الحد الأقصى 16
        cm.downloadAreaAsync(this, box, 10, 16, new CacheManager.CacheManagerCallback() {
            @Override
            public void onTaskComplete() {
                // بعد اكتمال التنزيل، أوقف الشبكة، وادخل وضع عدم الاتصال
                map.setUseDataConnection(false);
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this,
                                "اكتمل التنزيل! أصبح التطبيق دون اتصال الآن.",
                                Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onTaskFailed(int errors) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this,
                                "فشل التنزيل، عدد الأخطاء: " + errors,
                                Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel,
                                       int zoomMin, int zoomMax) {
                // يمكنك إضافة شريط تقدم هنا إذا أردت
            }

            @Override
            public void downloadStarted() { }

            @Override
            public void setPossibleTilesInArea(int total) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
