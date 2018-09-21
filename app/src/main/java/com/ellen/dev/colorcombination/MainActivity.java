package com.ellen.dev.colorcombination;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 할일 1 : 색조합 : (a+b)/n
 * 할일 2 : 어두운색 거르기
 */
public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static final int PICK_FROM_ALBUM = 1080;
    private static final int CROP_FROM_IMAGE = 1081;

    ImageView image;
    ArrayList<MyColor> colors;
    ArrayList<MyColor> colors2;
    ArrayList<MyColor> colors_total;

    ArrayList<Bitmap> bg_image;

    Bitmap image_bitmap = null;
    int horizontal = 8;
    int vertical = 4;

    TextView tv_x_coordinate;
    TextView tv_y_coordinate;

    Button btn_picked_color;

    RecyclerView rv_images;

    boolean isFirst = true;
    boolean isCoord = false;


    int endX = 0;
    int endY = 0;
    int startX = 0;
    int startY = 0;

    int imageCount = 0;
    int rgb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        image = (ImageView) findViewById(R.id.iv_image);
        tv_x_coordinate = (TextView) findViewById(R.id.tv_x_coordinate);
        tv_y_coordinate = (TextView) findViewById(R.id.tv_y_coordinate);
        btn_picked_color = findViewById(R.id.btn_picked_color);

        rv_images = findViewById(R.id.rv_images);
/*

        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeBubble();
        }
*/

        findViewById(R.id.btn_img_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbum();
            }
        });


        btn_picked_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "click pick color", Toast.LENGTH_SHORT).show();
                // size : 8 * 4

                /**
                 * first square is
                 * 0, endX * (1/8) && 0, endY * (1/4)
                 * first square's center point is
                 * 0, endX * (1/16) && 0, endY * (1/8)
                 *
                 * second square is
                 * 0, endX * (2/8) && 0, endY * (1/4) (+1)
                 * second square's center point is
                 * 0, endX * (3/16) && 0, endY * (1/8) (+2)
                 *
                 * (1,1) square is
                 *  endX * (1/16), endY * (1/8)
                 *
                 * (1,2) square is
                 *  endX * (1/16), endY * (3/8)
                 *
                 *  (1,3) square is
                 *  endX * (1/16), endY * (5/8)
                 *
                 *  (1,4) square is
                 *  endX * (1/16), endY * (7/8)
                 *
                 *  (2,1) square is
                 *  endX * (3/16), endY * (1/8)
                 *
                 *  (2,2) square is
                 *  endX * (3/16), endY * (3/8)
                 */


                if (imageCount == 0) {
                    imageCount++;
                    colors = new ArrayList<>();

                    for (int i = 0; i < bg_image.size(); i++) {
                        Palette.from(bg_image.get(i)).maximumColorCount(bg_image.size()).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                rgb = 0;
                                if (palette.getVibrantSwatch() != null) {
                                    rgb = palette.getVibrantSwatch().getRgb();
                                } else if (palette.getLightVibrantSwatch() != null) {
                                    rgb = palette.getLightVibrantSwatch().getRgb();
                                } else if (palette.getDarkVibrantSwatch() != null) {
                                    rgb = palette.getDarkVibrantSwatch().getRgb();
                                }

                                MainActivity.MyColor color = new MyColor();
//                                    color.setX_coordinate(i+"");
//                                    color.setY_coordinate(j + "");
                                color.setColor(rgb);
                                colors.add(color);
                            }
                        });

                    }

                    RecyclerView rv_color = findViewById(R.id.rv_color);

                    //case 1: color
                    ColorGridAdpater adapter = new ColorGridAdpater(colors);
                    //case 2: image
//                ImageGridAdpater adapter = new ImageGridAdpater(bg_image, getApplicationContext());
                    GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 8);
                    rv_color.setLayoutManager(layoutManager);
                    rv_color.setAdapter(adapter);
                } else if (imageCount == 1) {
                    imageCount++;
                    colors2 = new ArrayList<>();
                    for (int i = 1; i <= horizontal; i++) {
                        for (int j = 1; j <= vertical; j++) {
                            MyColor color = new MyColor();
                            color.setX_coordinate(i + "");
                            color.setY_coordinate(j + "");
                            int total = endX - startX;
                            int top = (i * 2) - 1;
                            float point = total * top / 16;
                            float real = point + startX;
                            float x = ((endX - startX) * ((i * 2) - 1) / 16) + startX;
                            float y = ((endY - startY) * ((j * 2) - 1) / 8) + startY;


                            float imgx = (x / endX) * image_bitmap.getWidth();
                            float imgy = (y / endY) * image_bitmap.getHeight();
                            Log.e("=====BITMAP SIZE=======", image_bitmap.getWidth() + ", " + image_bitmap.getHeight());
                            Log.e("=====POINT" + i + j + "=======", imgx + ", " + imgy);


                            int rgb = image_bitmap.getPixel((int) imgx, (int) imgy);
                            color.setColor(rgb);
                            colors2.add(color);
                        }
                    }
                    RecyclerView rv_color_2 = findViewById(R.id.rv_color_2);

                    ColorGridAdpater adapter = new ColorGridAdpater(colors2);
                    GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 8);
                    rv_color_2.setLayoutManager(layoutManager);
                    rv_color_2.setAdapter(adapter);
                } else if (imageCount == 2) {
                    //i have to mix color
                    colors_total = new ArrayList<>();
                    for (int i = 0; i < colors.size(); i++) {
                        for (int j = 0; j < colors2.size(); j++) {
                            MyColor color = new MyColor();
                            color.setX_coordinate(colors.get(i).getX_coordinate() + ", " + colors.get(i).getY_coordinate());
                            color.setY_coordinate(colors2.get(j).getX_coordinate() + ", " + colors2.get(j).getY_coordinate());
                            int rgb = (colors.get(i).getColor() + colors2.get(j).getColor()) / 2;
                            color.setColor(rgb);
                            colors_total.add(color);
                        }
                    }

                    RecyclerView rv_color_total = findViewById(R.id.rv_color_total);
                    ColorGridAdpater adapter = new ColorGridAdpater(colors_total);
                    GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 8);
                    rv_color_total.setLayoutManager(layoutManager);

                    ItemClickSupport.addTo(rv_color_total).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            Toast.makeText(getApplicationContext(), colors_total.get(position).getX_coordinate() + ", " + colors_total.get(position).getY_coordinate(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    rv_color_total.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(), "imageCount error", Toast.LENGTH_SHORT).show();

                }


            }
        });

        findViewById(R.id.btn_pick_coordinate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "isCoord is " + isCoord + " >> " + !isCoord, Toast.LENGTH_SHORT).show();
                isCoord = !isCoord;
            }
        });

        image.setOnTouchListener(this);

    }

    /**
     * Set and initialize the view elements.
     */
    private void initializeBubble() {
//        findViewById(R.id.btn_bubble).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startService(new Intent(MainActivity.this, FloatingWidgetService.class));
//                finish();
//            }
//        });
    }


    private void doTakeAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, PICK_FROM_ALBUM);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case CODE_DRAW_OVER_OTHER_APP_PERMISSION:
                initializeBubble();
                break;
            case PICK_FROM_ALBUM:
                //Uri에서 이미지 이름을 얻어온다.
                //String name_Str = getImageNameToUri(data.getData());

                //이미지 데이터를 비트맵으로 받아온다.
                try {
                    Bitmap org_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    /**이미지가 화면에 맞게 조절되서 실제 비트맵 이미지를 화면에 맞게 축소시킴.*/
                    // Get size
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int viewWidth = size.x;


                    float image_width = org_bitmap.getWidth();
                    float image_height = org_bitmap.getHeight();


                    // Calculate image's size by maintain the image's aspect ratio
                    if (image_width > viewWidth) {
                        float percente = (float) (image_width / 100);
                        float scale = (float) (viewWidth / percente);
                        image_width *= (scale / 100);
                        image_height *= (scale / 100);
                    }

                    image_bitmap = Bitmap.createScaledBitmap(org_bitmap, (int) image_width, (int) image_height, true);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case CROP_FROM_IMAGE:
                break;
        }
//            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //event
        //event 종류/각각의 특성

        if (event.getAction() == MotionEvent.ACTION_DOWN && isCoord) {

            if (isFirst) {
                isFirst = false;
                startX = (int) event.getX();
                startY = (int) event.getY();

                tv_x_coordinate.setText(startX + " ,  " + startY);

            } else {
                isFirst = true;
                isCoord = false;

                endX = (int) event.getX();
                endY = (int) event.getY();
                tv_y_coordinate.setText(endX + " ,  " + endY);

                int width = endX - startX;
                int height = endY - startY;

                //터치해서 자른 전체크기 이미지
                Bitmap aa = Bitmap.createBitmap(image_bitmap, (int) startX, (int) startY, width, height);
//                ImageView temp = (ImageView) findViewById(R.id.iv_image_spot);
//                temp.setImageBitmap(aa);


                bg_image = new ArrayList<>();

                for (int i = 1; i <= vertical; i++) {
                    for (int j = 1; j <= horizontal; j++) {
                        // 박스크기
                        float box_width = width / horizontal;
                        float box_height = height / vertical;

                        float x = startX + (box_width * (j - 1));
                        float y = startY + (box_height * (i - 1));


                        //제대로 이미지 들어가나 테스트용
                        Bitmap spot_bitmap = Bitmap.createBitmap(image_bitmap, (int) x, (int) y, (int) box_width, (int) box_height);
                        bg_image.add(spot_bitmap);
                    }
                }

                ImageGridAdpater adapter = new ImageGridAdpater(bg_image, getApplicationContext());
                GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 8);
                rv_images.setLayoutManager(layoutManager);
                rv_images.setAdapter(adapter);

            }


        }


//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if(isCoord){
//                float x = event.getRawX();
//                float y = event.getRawY();
//
//                String msg = "터치를 입력받음 : " + x + " / " + y + "    일반" + event.getX() + " / " + event.getY();
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//        }

        return false;
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

    public class MyColor {
        String x_coordinate;
        String y_coordinate;
        int color;

        int r;
        int g;
        int b;


        public String getX_coordinate() {
            return x_coordinate;
        }

        public void setX_coordinate(String x_coordinate) {
            this.x_coordinate = x_coordinate;
        }

        public String getY_coordinate() {
            return y_coordinate;
        }

        public void setY_coordinate(String y_coordinate) {
            this.y_coordinate = y_coordinate;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
