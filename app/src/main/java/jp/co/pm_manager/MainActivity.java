package jp.co.pm_manager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    public static Uri cameraUri;
    private static String filePath;
    private DBOpenHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "onCreate()");

        setContentView(R.layout.activity_main);
        OriginalFragmentPagerAdapter adapter = new OriginalFragmentPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        checkPermissionExStorage();
        checkPermissionCamera();
        setRegistration();
    }

    //登録初期画面
    private void setRegistration() {
        setContentView(getLayoutInflater().inflate(R.layout.activity_main, null));
    }

    public void cameraIntent() {
        Log.d("debug", "cameraIntent()");

          // 保存先のフォルダーを【作成するケース】
                File cameraFolder = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES),"IMG");
                cameraFolder.mkdirs();

//        // 保存先のフォルダーを【カメラに指定するケース】
//        File cameraFolder = new File(
//                Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DCIM), "Camera");

        // 保存ファイル名
        String fileName = new SimpleDateFormat(
                "yyyy_MM_dd__HH_mm_ss", Locale.JAPAN).format(new Date());
        filePath = String.format("%s/%s.jpg", cameraFolder.getPath(), fileName);
        Log.d("debug", "filePath:" + filePath);

        // capture画像のファイルパス
        File cameraFile = new File(filePath);
        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                getApplicationContext().getPackageName() + ".fileprovider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);
        Log.d("debug", "startActivityForResult()");
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        if (requestCode == RESULT_CAMERA) {

            if (cameraUri != null) {
                setDataInput();
            } else {
                Log.d("debug", "cameraUri == null");
            }
        }
    }

    // アンドロイドのデータベースへ登録する
    private void registerDatabase(String file) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file);
        contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    // Runtime Permission check
    private void checkPermissionExStorage() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.i("info", "Write external storage permission confirmed.");
        }
        // 拒否していた場合
        else {
            requestPermissionExStorage();
        }
    }

    private void checkPermissionCamera() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.i("info", "Camera permission confirmed.");
        }
        // 拒否していた場合
        else {
            requestPermissionCamera();
        }
    }

    // 許可を求める
    private void requestPermissionExStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //EXTERNAL_STORAGEのPermissionダイアログ表示
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません",
                    Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    REQUEST_PERMISSION);

        }
    }

    private void requestPermissionCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            //CAMERAのPermissionダイアログ表示
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません",
                    Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,},
                    REQUEST_PERMISSION);

        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        Log.d("debug", "onRequestPermissionsResult()");

        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("info", "Permission request success");
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "権限を許可しなければアプリを使用できません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

/*
    //画像確認画面
    private void setFinalCheck() {
        setContentView(R.layout.finalcheck);
        ImageView imageView = findViewById(R.id.image_view);
        Button buttonYes = findViewById(R.id.buttonYes);
        Button buttonNo = findViewById(R.id.buttonNo);
        imageView.setImageURI(cameraUri);
        registerDatabase(filePath);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataInput();
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegistration();
            }
        });

    }
*/

    //データ入力画面
    public void setDataInput() {
        setContentView(R.layout.datainput);
        ImageView imageView = findViewById(R.id.imageView2);
        Button buttonOk = findViewById(R.id.buttonOk);
        Button buttonCan = findViewById(R.id.buttonCan);

        final EditText[] allEditText = {
                findViewById(R.id.editText_Type),
                findViewById(R.id.editText_Number),
                findViewById(R.id.editText_Name),
                findViewById(R.id.editText_Color),
                findViewById(R.id.editText_ColorType),
                findViewById(R.id.editText_Size1),
                findViewById(R.id.editText_Size2),
                findViewById(R.id.editText_Size3),
                findViewById(R.id.editText_Price),
                findViewById(R.id.editText_Comment)
        };
        final String[] tag = {"type", "number", "color", "colortype", "size1", "size2", "size3", "price", "comment"};
        imageView.setImageURI(cameraUri);
        registerDatabase(filePath);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper == null) {
                    helper = new DBOpenHelper(getApplicationContext());
                }

                if (db == null) {
                    db = helper.getWritableDatabase();
                }
                String[] value = new String[10];
                for (int i = 0; i < 10; i++) {
                    value[i] = allEditText[i].getText().toString();
                }
                insertData(db, "Main_DB", tag, value);

                cameraIntent();
            }
        });
        buttonCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegistration();
            }
        });
    }

    private void insertData(SQLiteDatabase db, String dbName, String[] tag, String[] value) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < tag.length; i++) {
            values.put(tag[i], value[i]);
            db.insert(dbName, null, values);
        }
    }
}
