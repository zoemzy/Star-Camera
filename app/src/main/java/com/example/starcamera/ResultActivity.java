package com.example.starcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.shixia.colorpickerview.ColorPickerView;
import com.shixia.colorpickerview.OnColorChangeListener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;


public class ResultActivity extends AppCompatActivity {

    private Bitmap resultBitmap;
    private SeekBar seekBarBrightnessThreshold;
    private SeekBar seekBarPointSize;
    private SeekBar seekBarStarSize;
    private SeekBar seekBarTransparency;
    private SeekBar seekBarBrightness;
    private ColorPickerView colorPicker;
    private TabLayout patternTabLayout;
    private ViewPager patternViewPager;
    private int selectedTabPosition = 0;
    private ImageView imageView;
    private Mat originalImage;
    private double brightnessThreshold = 215 ;
    private double pointSize = 40 ;
    private int starSize = 200 ;
    private double transparency = 0.5 ;
    private double brightness = 1.0 ;
    private int colorInt;
    private int red = 255;
    private  int green = 255;
    private int blue = 255;
    private int alpha = 127;
    private int patternNum = 1;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CompletableFuture<Void> future;


    static {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_java490");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonSaveImage = findViewById(R.id.buttonSaveImage);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        seekBarBrightnessThreshold = findViewById(R.id.seekBarBrightnessThreshold);
        seekBarPointSize = findViewById(R.id.seekBarPointSize);
        seekBarStarSize = findViewById(R.id.seekBarStarSize);
        seekBarTransparency = findViewById(R.id.seekBarTransparency);
        seekBarBrightness = findViewById(R.id.seekBarBrightness);
        colorPicker = findViewById(R.id.cpv_color);
        patternTabLayout = findViewById(R.id.patternTabLayout);
        patternViewPager = findViewById(R.id.patternViewPager);

        tabLayout.addTab(tabLayout.newTab().setText("发光点亮度").setIcon(R.drawable.pointbrightness));
        tabLayout.addTab(tabLayout.newTab().setText("发光点大小").setIcon(R.drawable.pointsize));
        tabLayout.addTab(tabLayout.newTab().setText("星星大小").setIcon(R.drawable.size));
        tabLayout.addTab(tabLayout.newTab().setText("星星透明度").setIcon(R.drawable.transparency));
        tabLayout.addTab(tabLayout.newTab().setText("星星亮度").setIcon(R.drawable.brightness));
        tabLayout.addTab(tabLayout.newTab().setText("星星颜色").setIcon(R.drawable.color));
        tabLayout.addTab(tabLayout.newTab().setText("星星样式").setIcon(R.drawable.pattern));

        patternTabLayout.addTab(patternTabLayout.newTab().setIcon(R.drawable.star_pattern_1_));
        patternTabLayout.addTab(patternTabLayout.newTab().setIcon(R.drawable.star_pattern_2_));
        patternTabLayout.addTab(patternTabLayout.newTab().setIcon(R.drawable.star_pattern_3_));
        patternTabLayout.addTab(patternTabLayout.newTab().setIcon(R.drawable.star_pattern_4_));
        patternTabLayout.addTab(patternTabLayout.newTab().setIcon(R.drawable.star_pattern_5_));
        patternTabLayout.addTab(patternTabLayout.newTab().setIcon(R.drawable.star_pattern_6));

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        ConstraintLayout constraintLayout = findViewById(R.id.result_constraint_layout);
        imageView = findViewById(R.id.resultImageView);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                switch (tab.getPosition()) {
                    case 0:
                        params.bottomToTop = seekBarBrightnessThreshold.getId();
                        seekBarBrightnessThreshold.setVisibility(View.VISIBLE);
                        seekBarPointSize.setVisibility(View.GONE);
                        seekBarStarSize.setVisibility(View.GONE);
                        seekBarTransparency.setVisibility(View.GONE);
                        seekBarBrightness.setVisibility(View.GONE);
                        colorPicker.setVisibility(View.GONE);
                        patternTabLayout.setVisibility(View.GONE);

                        break;
                    case 1:
                        params.bottomToTop = seekBarPointSize.getId();
                        seekBarBrightnessThreshold.setVisibility(View.GONE);
                        seekBarPointSize.setVisibility(View.VISIBLE);
                        seekBarStarSize.setVisibility(View.GONE);
                        seekBarTransparency.setVisibility(View.GONE);
                        seekBarBrightness.setVisibility(View.GONE);
                        colorPicker.setVisibility(View.GONE);
                        patternTabLayout.setVisibility(View.GONE);

                        break;
                    case 2:
                        params.bottomToTop = seekBarStarSize.getId();
                        seekBarBrightnessThreshold.setVisibility(View.GONE);
                        seekBarPointSize.setVisibility(View.GONE);
                        seekBarStarSize.setVisibility(View.VISIBLE);
                        seekBarTransparency.setVisibility(View.GONE);
                        seekBarBrightness.setVisibility(View.GONE);
                        colorPicker.setVisibility(View.GONE);
                        patternTabLayout.setVisibility(View.GONE);

                        break;
                    case 3:
                        params.bottomToTop = seekBarTransparency.getId();
                        seekBarBrightnessThreshold.setVisibility(View.GONE);
                        seekBarPointSize.setVisibility(View.GONE);
                        seekBarStarSize.setVisibility(View.GONE);
                        seekBarTransparency.setVisibility(View.VISIBLE);
                        seekBarBrightness.setVisibility(View.GONE);
                        colorPicker.setVisibility(View.GONE);
                        patternTabLayout.setVisibility(View.GONE);

                        break;
                    case 4:
                        params.bottomToTop = seekBarBrightness.getId();
                        seekBarBrightnessThreshold.setVisibility(View.GONE);
                        seekBarPointSize.setVisibility(View.GONE);
                        seekBarStarSize.setVisibility(View.GONE);
                        seekBarTransparency.setVisibility(View.GONE);
                        seekBarBrightness.setVisibility(View.VISIBLE);
                        colorPicker.setVisibility(View.GONE);
                        patternTabLayout.setVisibility(View.GONE);

                        break;
                    case 5:
                        params.bottomToTop = colorPicker.getId();
                        seekBarBrightnessThreshold.setVisibility(View.GONE);
                        seekBarPointSize.setVisibility(View.GONE);
                        seekBarStarSize.setVisibility(View.GONE);
                        seekBarTransparency.setVisibility(View.GONE);
                        seekBarBrightness.setVisibility(View.GONE);
                        colorPicker.setVisibility(View.VISIBLE);
                        patternTabLayout.setVisibility(View.GONE);

                        break;
                    case 6:
                        params.bottomToTop = patternTabLayout.getId();
                        seekBarBrightnessThreshold.setVisibility(View.GONE);
                        seekBarPointSize.setVisibility(View.GONE);
                        seekBarStarSize.setVisibility(View.GONE);
                        seekBarTransparency.setVisibility(View.GONE);
                        seekBarBrightness.setVisibility(View.GONE);
                        colorPicker.setVisibility(View.GONE);
                        patternTabLayout.setVisibility(View.VISIBLE);
                        patternViewPager.setVisibility(View.VISIBLE);
                        patternTabLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                // 获取之前选中的选项卡
                                TabLayout.Tab tab_ = patternTabLayout.getTabAt(selectedTabPosition);
                                if (tab_ != null) {
                                    // 选择该选项卡
                                    patternTabLayout.selectTab(tab_);
                                }
                            }
                        });

                        // 将TabLayout和ViewPager关联起来
                        patternTabLayout.setupWithViewPager(patternViewPager);

                        // 添加patternTabLayout的onTabSelected函数
                        patternTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                // 在这里添加你的代码来处理选项卡被选中的情况
                                switch (tab.getPosition()) {
                                    case 0:
                                        selectedTabPosition = patternTabLayout.getSelectedTabPosition();
                                        patternNum = 1;
                                        updateImage();
                                        break;
                                    case 1:
                                        selectedTabPosition = patternTabLayout.getSelectedTabPosition();
                                        patternNum = 2;
                                        updateImage();
                                        break;
                                    case 2:
                                        selectedTabPosition = patternTabLayout.getSelectedTabPosition();
                                        patternNum = 3;
                                        updateImage();
                                        break;
                                    case 3:
                                        selectedTabPosition = patternTabLayout.getSelectedTabPosition();
                                        patternNum = 4;
                                        updateImage();
                                        break;
                                    case 4:
                                        selectedTabPosition = patternTabLayout.getSelectedTabPosition();
                                        patternNum = 5;
                                        updateImage();
                                        break;
                                    case 5:
                                        selectedTabPosition = patternTabLayout.getSelectedTabPosition();
                                        patternNum = 6;
                                        updateImage();
                                        break;
                                    default:
                                        break;
                                }
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {
                                // 在这里添加你的代码来处理选项卡被取消选中的情况
                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {
                                // 在这里添加你的代码来处理选项卡被重新选中的情况
                            }
                        });

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 然后在OnClickListener中使用resultBitmap
        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
                File storageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                + "/星星相机");
                boolean success = true;
                if (!storageDir.exists()) {
                    success = storageDir.mkdirs();
                }
                if (success) {
                    final File imageFile = new File(storageDir, imageFileName);
                    try {
                        OutputStream fOut = Files.newOutputStream(imageFile.toPath());
                        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Add the image to the system gallery
                    galleryAddPic(imageFile);
                    Toast.makeText(ResultActivity.this, "已保存到相册\"星星相机\"", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ResultActivity.this, "保存失败，请检查相册权限或内存", Toast.LENGTH_LONG).show();
                }
            }

            // Add the image to the system gallery
            private void galleryAddPic(File imageFile) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(imageFile);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
            }
        });

        // Get image Uri from intent
        Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        // Convert Uri to Bitmap
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert Bitmap to Mat
        originalImage = new Mat();
        Utils.bitmapToMat(bitmap, originalImage);

        updateImage();

        // Create a SeekBar for adjustment
        seekBarBrightnessThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // When the user changes the SeekBar value, update the image StarSize
                brightnessThreshold = progress + 175.0;
                luminousPoints=null;
                updateImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts dragging the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when the user stops dragging the SeekBar
            }
        });

        seekBarPointSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // When the user changes the SeekBar value, update the image StarSize
                pointSize = progress;
                luminousPoints=null;
                updateImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts dragging the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when the user stops dragging the SeekBar
            }
        });

        seekBarStarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // When the user changes the SeekBar value, update the image StarSize
                starSize = progress;
                updateImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts dragging the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when the user stops dragging the SeekBar
            }
        });

        seekBarTransparency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // When the user changes the SeekBar value, update the image StarSize
                transparency = progress / 100.0;
                updateImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts dragging the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when the user stops dragging the SeekBar
            }
        });

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // When the user changes the SeekBar value, update the image StarSize
                brightness = progress / 100.0;
                updateImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts dragging the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when the user stops dragging the SeekBar
            }
        });



        colorPicker.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void colorChanged(int color) {
                colorInt=color;
                red = Color.red(colorInt);
                green = Color.green(colorInt);
                blue = Color.blue(colorInt);
                alpha = Color.alpha(colorInt);
                updateImage();
            }
        });

    }
    private List<Point> luminousPoints=null;

    void updateImage() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            if (luminousPoints == null) {
                luminousPoints = processImage(originalImage, brightnessThreshold, pointSize);
            }
            return addStarEffect(originalImage, luminousPoints, starSize, transparency, brightness, blue, green, red, alpha);
        }, executor).thenAcceptAsync(resultImage -> {
            // Convert the processed Mat to Bitmap
            resultBitmap = Bitmap.createBitmap(resultImage.cols(), resultImage.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(resultImage, resultBitmap);

            // Display the processed image in the ImageView
            imageView.setImageBitmap(resultBitmap);
        }, new HandlerExecutor(handler));
    }

    class HandlerExecutor implements Executor {
        private final Handler handler;

        HandlerExecutor(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (future != null) {
            future.cancel(true);  // 取消后台任务
        }
    }

    private List<Point> processImage(Mat image, double brightnessThreshold, double pointSize) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat thresholdedImage = new Mat();
        Imgproc.threshold(grayImage, thresholdedImage, brightnessThreshold, 255, Imgproc.THRESH_BINARY);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresholdedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Point> luminousPoints = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > pointSize) {
                Moments M = Imgproc.moments(contour);
                if (M.m00 > 0) {
                    int cX = (int) (M.m10 / M.m00);
                    int cY = (int) (M.m01 / M.m00);
                    luminousPoints.add(new Point(cX, cY));
                }
            }
        }

        return luminousPoints;
    }

    private Mat addStarEffect(Mat image, List<Point> points, int starSize, double transparency, double brightness, int blue, int green , int red, double alpha) {
        Mat resultImage = image.clone();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = null;
        switch (patternNum) {
            case 1:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_pattern_1, options);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_pattern_2, options);
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_pattern_3, options);
                break;
            case 4:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_pattern_4, options);
                break;
            case 5:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_pattern_5, options);
                break;
            case 6:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star_pattern_6, options);
                break;
            default:
                break;
        }

        Mat starPattern = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, starPattern);

        // 将图像从BGR色彩空间转换为Lab色彩空间
        Mat lab = new Mat();
        Imgproc.cvtColor(starPattern, lab, Imgproc.COLOR_BGR2Lab);

        // 分离通道
        List<Mat> channels = new ArrayList<>();
        Core.split(lab, channels);

        // 创建一个新的颜色矩阵，例如紫色
        Scalar color = new Scalar(red, green, blue, 255); // BGR
        Mat labColor = new Mat();
        Imgproc.cvtColor(new Mat(new Size(1, 1), CvType.CV_8UC3, color), labColor, Imgproc.COLOR_BGR2Lab);
        double a = labColor.get(0, 0)[1];
        double b = labColor.get(0, 0)[2];

        // 创建一个掩码，用于找到所有非黑色的像素
        Mat nonBlackPixels = new Mat();
        Core.inRange(starPattern, new Scalar(1,1,1,0), new Scalar(255,255,255,255), nonBlackPixels);

        // 将所有非黑色的像素的a和b通道设置为新的颜色
        channels.get(1).setTo(new Scalar(a), nonBlackPixels);
        channels.get(2).setTo(new Scalar(b), nonBlackPixels);

        // 将通道重新合并
        Core.merge(channels, lab);

        // 将图像从Lab色彩空间转换回BGR色彩空间
        Imgproc.cvtColor(lab, starPattern, Imgproc.COLOR_Lab2BGR);

        // 将图像从BGR色彩空间转换回BGRA色彩空间
        Imgproc.cvtColor(starPattern, starPattern, Imgproc.COLOR_BGR2BGRA);

        // 调整星星图像的亮度
        Core.convertScaleAbs(starPattern, starPattern, brightness, 0);

        Mat starPatternResized = new Mat();
        Imgproc.resize(starPattern, starPatternResized, new Size(starSize, starSize));

        for (Point point : points) {
            int x = (int) point.x;
            int y = (int) point.y;
            int xStart = Math.max(0, x - starSize / 2);
            int xEnd = Math.min(resultImage.cols(), x + starSize / 2);
            int yStart = Math.max(0, y - starSize / 2);
            int yEnd = Math.min(resultImage.rows(), y + starSize / 2);

            Rect roiRect = new Rect(xStart, yStart, xEnd - xStart, yEnd - yStart);
            Mat roi = new Mat(resultImage, roiRect);

            // 确保starPatternResized的大小和roi相同
            Mat starPatternResizedCropped = new Mat(starPatternResized, new Rect(0, 0, roi.cols(), roi.rows()));

            // 使用addWeighted()函数来合并原始图像和starPattern
            Core.addWeighted(roi, 1.0, starPatternResizedCropped, (alpha / 255) * 0.3 +transparency * 0.7, 0.0, roi);

            roi.copyTo(resultImage.submat(roiRect));
        }

        return resultImage;
    }
}