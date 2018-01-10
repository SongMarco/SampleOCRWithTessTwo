package nova.sampleocrwithtesstwo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


// 문자 인식 기능을 학습하기 위한 예제.
// 타이포셔너리 앱의 '사진에서 단어장 만들기' 기능에 사용했다.

public class MainActivity extends AppCompatActivity {

    Bitmap image; //사용되는 이미지
    private TessBaseAPI mTess; //Tess API reference
    String datapath = "" ; //언어데이터가 있는 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 이미지 디코딩을 위한 초기화.
        // 문자 인식 메소드를 이용하려면 이미지의 비트맵이 필요하다. - 먼저 이미지의 비트맵을 가져온다.
        image = BitmapFactory.decodeResource(getResources(), R.drawable.sample_eng_text); //샘플이미지파일

        //언어 트레이닝 데이터의 경로를 가져온다.
        datapath = getFilesDir()+ "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"));

        //Tesseract API 언어 설정. eng: 영어// kor :한글
        String lang = "eng";

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);

        RelativeLayout layoutOcr = (RelativeLayout)findViewById(R.id.OCRButtonContainer);

        layoutOcr.setOnClickListener(onOcrClickListener);

    }


    // 문자인식 버튼을 클릭하였다. (xml 의 onClick 속성)
    // 태저렉트에서 문자인식 동작을 수행한다.

    View.OnClickListener onOcrClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String OCRresult = null;
            mTess.setImage(image);
            OCRresult = mTess.getUTF8Text();
            TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
            OCRTextView.setText(OCRresult);
        }
    };




    //언어 트레이닝 데이터를 복사한다.
    private void copyFiles() {
        try{
            //파일의 경로를 세팅
            String filepath = datapath + "/tessdata/eng.traineddata";

            // Assets 디렉토리에 파일을 넣기 위해 AssetManager 세팅
            AssetManager assetManager = getAssets();

            // 트레이닝 데이터의 본래 파일을 인풋스트림에서 가져온다.
            InputStream instream = assetManager.open("tessdata/eng.traineddata");

            // 복사할 파일의 경로로 아웃풋 스트림을 내보낸다.
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;

            // 인풋스트림 -> 아웃풋 스트림으로 파일 복사 수행
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }

            // 복사 완료시 아웃풋 스트림을 flush (출력) 하여 파일 복사 완료
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //문자 인식 트레이닝 데이터가 세팅된 지 확인한다.
    private void checkFile(File dir) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
    }
}