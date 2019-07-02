package com.company.njupt.lianliankan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.njupt.lianliankan.board.GameService;
import com.company.njupt.lianliankan.board.impl.GameServiceImpl;
import com.company.njupt.lianliankan.utils.GameConf;
import com.company.njupt.lianliankan.utils.LinkInfo;
import com.company.njupt.lianliankan.utils.SizeUtils;
import com.company.njupt.lianliankan.view.GameView;
import com.company.njupt.lianliankan.view.Piece;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements BaseHandlerCallBack{
    private static final String TAG = "GameActivity";
    //游戏配置对象
    private GameConf config;
    //游戏业务逻辑接口
    private GameService gameService;
    //游戏界面
    private GameView gameView;
    //开始按钮
    private Button startButton;
    //记录剩余时间的TextView
    private TextView timeTextView;
    //失败后弹出的对话框
    private AlertDialog.Builder lostDialog;
    //游戏胜利后的对话框
    private AlertDialog.Builder successDialog;
    //游戏通关后的对话框
    private AlertDialog.Builder passDialog;
    //定时器
    private Timer timer = null;
    //记录游戏的剩余时间
    private int gameTime;
    //记录是否处于游戏状态
    private boolean isPlaying;
    //振动处理类
    private Vibrator vibrator;
    //记录已经选中的方块
    private Piece selectedPiece = null;
    //Handler类，异步处理
    private NoLeakHandler handler;
    //Handler发送消息的ID
    private final int MESSAGE_ID = 0x123;
    //提示音
    private MediaPlayer mp;
    //分数
    private int score = 0;
    //记录分数的TextView
    private TextView scoreTextView;
    //难度
    private int level;
    //关卡
    private int stage;
    //记录关卡的TextView
    private TextView stageTextView;
    //排行榜的姓名
    private String[] nameRank;
    //排行榜的分数
    private String[] scoreRank;
    //是否开启背景音乐
    private boolean bgm;
    //是否开启提示音
    private boolean beep;
    //是否开启震动
    private boolean vibrate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        handler = new NoLeakHandler(this);
        Intent intent = this.getIntent();    //获得当前的Intent
        level = intent.getIntExtra("level", 1);
        bgm = intent.getBooleanExtra("bgm", true);
        beep = intent.getBooleanExtra("beep", true);
        vibrate = intent.getBooleanExtra("vibrate", true);

        init();
        startGame(GameConf.DEFAULT_TIME - 15*(stage-1) );
    }

    // 初始化游戏的方法
    private void init() {
        //适配不同的屏幕，dp转为px
        int beginImageX = SizeUtils.dp2Px(this, GameConf.BEGIN_IMAGE_X);
        int beginImageY = SizeUtils.dp2Px(this, GameConf.BEGIN_IMAGE_Y);

        //初始化分数和获取显示分数文本框
        score = 0;
        scoreTextView = findViewById(R.id.scoreText);
        //初始化关卡和获取显示关卡文本框
        stage = 1;
        stageTextView = findViewById(R.id.stageText);
        //初始化游戏配置
        config = new GameConf(GameConf.PIECE_X_SUM, GameConf.PIECE_Y_SUM, beginImageX, beginImageY, GameConf.DEFAULT_TIME, this, level);
        //获取游戏区域对象
        gameView = findViewById(R.id.gameView);
        //获取显示剩余时间的文本框
        timeTextView = findViewById(R.id.timeText);
        //获取开始按钮
        startButton = this.findViewById(R.id.startButton);
        //获取振动器
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //初始化游戏业务逻辑接口
        gameService = new GameServiceImpl(this.config);
        //设置游戏逻辑的实现类
        gameView.setGameService(gameService);
        //设置提示音
        mp = MediaPlayer.create(this, R.raw.coin);
        //读取排行榜数据
        nameRank = new String[15];
        scoreRank = new String[15];
        try {
            //读取排行榜
            FileInputStream fis = openFileInput("myrank.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            int cnt = 0;
            while((line = reader.readLine()) != null){
                if(cnt%2 == 0)
                    nameRank[cnt/2] = line;
                else scoreRank[cnt/2] = line;
                cnt++;
            }
            while(cnt < 30) {
                if(cnt%2 == 0)
                    nameRank[cnt/2] = "none";
                else scoreRank[cnt/2] = "-1";
                cnt++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //为开始按钮的单击事件绑定事件监听器
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View source) {
                score = 0;
                stage = 1;
                startGame(GameConf.DEFAULT_TIME - 15*(stage-1) );
            }
        });
        //为游戏区域的触碰事件绑定监听器
        this.gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (!isPlaying) {
                    return false;
                }
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    gameViewTouchDown(e);
                }
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    gameViewTouchUp(e);
                }
                return true;
            }
        });
        //初始化游戏失败的对话框
        lostDialog = createDialog(getString(R.string.lost), getString(R.string.lost_restart), R.drawable.lost)
                .setPositiveButton(R.string.dialog_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateRank();
                    }
                });
        //初始化游戏胜利的对话框
        successDialog = createDialog(getString(R.string.success), getString(R.string.success_restart),
                R.drawable.success).setPositiveButton(R.string.dialog_sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame(GameConf.DEFAULT_TIME - 15*(stage-1) );
                    }
                });
        //初始化游戏通关的对话框
        passDialog = createDialog(getString(R.string.pass),getString(R.string.pass_restart), R.drawable.success)
                .setPositiveButton(R.string.dialog_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateRank();
                    }
                });

        //获取屏幕宽度并设置图片大小
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.v("phone_width", "width = " + dm.widthPixels);
        GameConf.PIECE_WIDTH = GameConf.PIECE_HEIGHT = (dm.widthPixels - GameConf.BEGIN_IMAGE_X)/GameConf.PIECE_X_SUM;
        Log.d(TAG, " GameConf.PIECE_WIDTH =" + GameConf.PIECE_WIDTH + "， GameConf.PIECE_HEIGHT =" + GameConf.PIECE_HEIGHT);
    }

    // 按下返回键时停止计时 避免程序崩溃
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.e(TAG, "onBackPressed : 按下了返回键");
            stopTimer();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 触碰游戏区域的处理方法
    private void gameViewTouchDown(MotionEvent event) {
        //获取GameServiceImpl中的Piece[][]数组
        Piece[][] pieces = gameService.getPieces();
        //获取用户点击的x座标
        float touchX = event.getX();
        //获取用户点击的y座标
        float touchY = event.getY();
        //根据用户触碰的座标得到对应的Piece对象
        Piece currentPiece = gameService.findPiece(touchX, touchY);
        //如果没有选中任何Piece对象(即鼠标点击的地方没有图片), 不再往下执行
        if (currentPiece == null) {
            return;
        }
        Log.v("select","当前选择的方块索引是：(" + currentPiece.getIndexX() + "," + currentPiece.getIndexY() + ")");
        //将gameView中的选中方块设为当前方块
        this.gameView.setSelectedPiece(currentPiece);
        //表示之前没有选中任何一个Piece
        if (this.selectedPiece == null) {
            //将当前方块设为已选中的方块, 重新将GamePanel绘制, 并不再往下执行
            this.selectedPiece = currentPiece;
            this.gameView.postInvalidate();
            return;
        }
        //表示之前已经选择了一个
        if (this.selectedPiece != null) {
            //在这里就要对currentPiece和prePiece进行判断并进行连接
            LinkInfo linkInfo = this.gameService.link(this.selectedPiece, currentPiece);
            //两个Piece不可连, linkInfo为null
            if (linkInfo == null) {
                //如果连接不成功
                //1.如果是不同方块，将当前方块设为选中方块
                //2.是过是同一方块，取消当前选中状态
                if(this.selectedPiece == currentPiece)
                {
                    this.gameView.setSelectedPiece(null);
                    this.selectedPiece = null;
                    Log.v("select","取消选中");
                }
                else {
                    Log.v("select"," 没有找到可行解");
                    this.selectedPiece = currentPiece;
                }

                this.gameView.postInvalidate();
            } else {
                //处理成功连接
                Log.v("select"," 成功找到可行解");
                handleSuccessLink(linkInfo, this.selectedPiece, currentPiece, pieces);
            }
        }
    }

    // 触碰游戏区域的处理方法
    private void gameViewTouchUp(MotionEvent e) {
        this.gameView.postInvalidate();
    }

    // 以gameTime作为剩余时间开始或恢复游戏
    private void startGame(int gameTime) {
        //如果之前的timer还未取消，取消timer
        if (this.timer != null) {
            stopTimer();
            Log.v("timer","stop");
        }
        //重新设置游戏时间
        this.gameTime = gameTime;
        //如果游戏剩余时间与总游戏时间相等，即为重新开始新游戏
        if (gameTime == GameConf.DEFAULT_TIME - 15*(stage-1) ) {
            //开始新的游戏游戏
            gameView.startGame();
        }
        isPlaying = true;
        this.timer = new Timer();
        //启动计时器，每隔1秒发送一次消息
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MESSAGE_ID);
                Log.v("timer","one");
            }
        }, 0, 1000);
        //将选中方块设为null。
        this.selectedPiece = null;
    }

    // 成功连接后处理
    private void handleSuccessLink(LinkInfo linkInfo, Piece prePiece, Piece currentPiece, Piece[][] pieces) {
        //它们可以相连, 让GamePanel处理LinkInfo
        this.gameView.setLinkInfo(linkInfo);
        //将gameView中的选中方块设为null
        this.gameView.setSelectedPiece(null);
        this.gameView.postInvalidate();
        //将两个Piece对象从数组中删除
        pieces[prePiece.getIndexX()][prePiece.getIndexY()] = null;
        pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;
        //将选中的方块设置null。
        this.selectedPiece = null;
        //手机振动(100毫秒)
        if(vibrate)
            this.vibrator.vibrate(100);
        //提示音
        if(beep)
            this.mp.start();
        score += this.gameTime;
        this.scoreTextView.setText("分数：" + score);
        //判断是否还有剩下的方块, 如果没有, 游戏胜利
        if (!this.gameService.hasPieces()) {
            //游戏胜利
            stage++;
            if(stage > 5) {
                Log.v("完成","第五关");
                //停止定时器
                stopTimer();
                //更改游戏状态
                isPlaying = false;
                this.passDialog.show();
            } else {
                //停止定时器
                stopTimer();
                //更改游戏状态
                isPlaying = false;
                this.successDialog.show();

            }
        }
    }

    // 创建对话框的工具方法
    private AlertDialog.Builder createDialog(String title, String message, int imageResource) {
        return new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message).setIcon(imageResource).setCancelable(false);
    }

    // 停止计时
    private void stopTimer() {
        //停止定时器
        if(this.timer != null)  this.timer.cancel();
        this.timer = null;
    }

    // 更新排行榜
    private void updateRank() {
        if (this.score > Integer.parseInt(scoreRank[5 * level - 1])) {
            final EditText editText = new EditText(GameActivity.this);
            AlertDialog.Builder inputDialog = new AlertDialog.Builder(GameActivity.this);
            inputDialog.setTitle("大侠，请在排行榜留下您的姓名").setView(editText).setCancelable(false);
            inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int begin = 5 * (level - 1);
                    for (int i = begin; i < begin + 5; i++) {
                        if (score > Integer.parseInt(scoreRank[i])) {
                            //将排行榜向后移一位
                            for (int j = begin + 4; j > i; j--) {
                                nameRank[j] = nameRank[j - 1];
                                scoreRank[j] = scoreRank[j - 1];
                            }
                            nameRank[i] = editText.getText().toString();
                            scoreRank[i] = String.valueOf(score);
                            Log.v("score",scoreRank[i] + score );
                            break;
                        }

                    }
                    //将文件数据写入到应用的内部存储
                    try {
                        FileOutputStream fos = openFileOutput("myrank.txt", Context.MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                        String str = "";
                        for (int i = 0; i < 15; i++) {
                            str += nameRank[i] + "\n";
                            str += scoreRank[i] + "\n";
                        }
                        osw.write(str);
                        osw.close();
                        fos.close();
                        Log.v("rank", "写入完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });
            inputDialog.show();
        }
        else {
            Toast.makeText(GameActivity.this,
                    "亲，很遗憾没有进入排行榜，下次加油哦！",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 定时器回调函数
    @Override
    public void callBack(Message msg) {
        switch (msg.what) {
            case MESSAGE_ID:
                timeTextView.setText(String.format(getString(R.string.remaining_time), gameTime));
                scoreTextView.setText("分数：" + score);
                stageTextView.setText("关卡：" + stage);
                gameTime--; //游戏剩余时间减少
                //时间小于0, 游戏失败
                if (gameTime < 0) {
                    //停止计时
                    stopTimer();
                    //更改游戏的状态
                    isPlaying = false;
                    //失败后弹出对话框
                    lostDialog.show();
                    return;
                }
                break;
            default:
                break;
        }
    }

    private static class NoLeakHandler<T extends BaseHandlerCallBack> extends Handler {
        private WeakReference<T> wr;

        private NoLeakHandler(T t) {
            wr = new WeakReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            T t = wr.get();
            if (t != null) {
                t.callBack(msg);
            }
        }
    }

}
