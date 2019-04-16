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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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
    private Uri cameraUri;
    private String filePath;
    public EditText editTextKey, editTextValue;
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug","onCreate()");
        setRegistration();
    }

    //登録初期画面
    private void setRegistration(){
        setContentView(R.layout.activity_main);
        Button cameraButton = findViewById(R.id.camera_button);
        checkPermissionExStorage();
        checkPermissionCamera();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Android 6, API 23以上でパーミッシンの確認
                if (Build.VERSION.SDK_INT >= 23) {
                    //checkPermissionExStorage();
                    //checkPermissionCamera();
                    cameraIntent();
                }
                else {
                    cameraIntent();
                }
            }
        });

    }

    private void cameraIntent(){
        Log.d("debug","cameraIntent()");

        // 保存先のフォルダーを作成するケース
//        File cameraFolder = new File(
//                Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES),"IMG");
//        cameraFolder.mkdirs();

        // 保存先のフォルダーをカメラに指定した場合
        File cameraFolder = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM),"Camera");


        // 保存ファイル名
        String fileName = new SimpleDateFormat(
                "ddHHmmss", Locale.US).format(new Date());
        filePath = String.format("%s/%s.jpg", cameraFolder.getPath(),fileName);
        Log.d("debug","filePath:"+filePath);

        // capture画像のファイルパス
        File cameraFile = new File(filePath);
        cameraUri = FileProvider.getUriForFile(
                MainActivity.this,
                getApplicationContext().getPackageName() + ".fileprovider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, RESULT_CAMERA);

        Log.d("debug","startActivityForResult()");
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        if (requestCode == RESULT_CAMERA) {

            if(cameraUri != null){
                setFinalCheck();
            }
            else{
                Log.d("debug","cameraUri == null");
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
    }

    // Runtime Permission check
    private void checkPermissionExStorage(){
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
            Log.i("info","Write external storage permission confirmed.");
        }
        // 拒否していた場合
        else{
            requestPermissionExStorage();
        }
    }

    private void checkPermissionCamera(){
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED){
            Log.i("info","Camera permission confirmed.");
        }
        // 拒否していた場合
        else{
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

        Log.d("debug","onRequestPermissionsResult()");

        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("info","Permission request success");
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "権限を許可しなければアプリを使用できません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
    //画像確認画面
    private void setFinalCheck(){
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

    //データ入力画面
    private void setDataInput(){
        setContentView(R.layout.datainput);
        ImageView imageView = findViewById(R.id.imageView2);
        Button buttonOk = findViewById(R.id.buttonOk);
        Button buttonCan = findViewById(R.id.buttonCan);
        imageView.setImageURI(cameraUri);
        registerDatabase(filePath);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helper == null){
                    helper = new DBOpenHelper(getApplicationContext());
                }

                if(db == null){
                    db = helper.getWritableDatabase();
                }

                String key = editTextKey.getText().toString();
                String value = editTextValue.getText().toString();
                insertData(db, key, Integer.valueOf(value));

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
    private void insertData(SQLiteDatabase db, String com, int price){

        ContentValues values = new ContentValues();
        values.put("company", com);
        values.put("stockprice", price);

        db.insert("testdb", null, values);
    }

}