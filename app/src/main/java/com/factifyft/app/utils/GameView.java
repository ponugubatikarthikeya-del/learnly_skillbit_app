package com.factifyft.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.factifyft.app.R;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private volatile boolean dialogShown = false;
    private volatile boolean isPaused = false;

    public enum Direction {UP, DOWN, LEFT, RIGHT}

    public interface OnScoreChangeListener {
        void onScoreChanged(int newScore);
    }

    private Thread gameThread;
    private volatile boolean running = false;
    private SurfaceHolder holder;

    private final int rows = 15;
    private final int cols = 15;
    private Paint gridPaint, snakePaint, foodPaint, bgPaint, borderPaint;
    private ArrayList<Point> snake;
    private Point food;
    private Direction currentDirection = Direction.RIGHT;
    private final Random random = new Random();
    private int score = 0;
    private volatile boolean isGameOver = false;

    private OnScoreChangeListener scoreListener;

    private SoundPool soundPool;
    private int eatSoundId;
    private int gameOverSoundId;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);

        bgPaint = new Paint();
        bgPaint.setColor(ContextCompat.getColor(getContext(), R.color.snake_bg_dark));

        gridPaint = new Paint();
        gridPaint.setColor(ContextCompat.getColor(getContext(), R.color.snake_grid));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);

        snakePaint = new Paint();
        snakePaint.setColor(ContextCompat.getColor(getContext(), R.color.snake_green));

        foodPaint = new Paint();
        foodPaint.setColor(ContextCompat.getColor(getContext(), R.color.snake_food_red));

        borderPaint = new Paint();
        borderPaint.setColor(ContextCompat.getColor(getContext(), R.color.snake_border));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6);

        initSoundPool();
    }

    private void initSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        int eatResId = getResources().getIdentifier("snake_eat", "raw", getContext().getPackageName());
        if (eatResId != 0) {
            eatSoundId = soundPool.load(getContext(), eatResId, 1);
        }

        int gameOverResId = getResources().getIdentifier("snake_game_over", "raw", getContext().getPackageName());
        if (gameOverResId != 0) {
            gameOverSoundId = soundPool.load(getContext(), gameOverResId, 1);
        }
    }

    private void playSound(int soundId) {
        if (soundPool != null && soundId != 0) {
            soundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public void setOnScoreChangeListener(OnScoreChangeListener listener) {
        this.scoreListener = listener;
    }

    public void changeDirection(Direction dir) {
        if ((currentDirection == Direction.UP && dir == Direction.DOWN) ||
                (currentDirection == Direction.DOWN && dir == Direction.UP) ||
                (currentDirection == Direction.LEFT && dir == Direction.RIGHT) ||
                (currentDirection == Direction.RIGHT && dir == Direction.LEFT)) {
            return;
        }
        currentDirection = dir;
    }

    public void resetGame() {
        dialogShown = false;
        isGameOver = false;
        isPaused = false;
        score = 0;
        notifyScore();
        snake = new ArrayList<>();
        int startX = cols / 2;
        int startY = rows / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));
        snake.add(new Point(startX - 3, startY));
        currentDirection = Direction.RIGHT;
        placeFood();
    }

    private void placeFood() {
        while (true) {
            int x = random.nextInt(cols);
            int y = random.nextInt(rows);
            boolean clash = false;
            if (snake != null) {
                for (Point p : snake) {
                    if (p.x == x && p.y == y) {
                        clash = true;
                        break;
                    }
                }
            }
            if (!clash) {
                food = new Point(x, y);
                break;
            }
        }
    }

    private void notifyScore() {
        if (scoreListener != null) scoreListener.onScoreChanged(score);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (snake == null) {
            resetGame();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        pause();
    }

    public void pause() {
        running = false;
        isPaused = true;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException ignored) {
            }
        }
        gameThread = null;
    }

    public void resume() {
        if (running) return;
        running = true;
        isPaused = false;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long updateDelay = 200;
        long lastTime = System.currentTimeMillis();

        while (running) {
            if (!holder.getSurface().isValid() || isPaused) {
                android.os.SystemClock.sleep(20);
                continue;
            }

            long now = System.currentTimeMillis();

            if (now - lastTime >= updateDelay) {
                update();

                if (isGameOver && !dialogShown) {
                    dialogShown = true;
                    Context context = getContext();
                    if (context instanceof Activity activity) {
                        activity.runOnUiThread(() -> {
                            if (!activity.isFinishing() && !activity.isDestroyed()) {
                                showGameAlertdialog(score);
                            }
                        });
                    }
                }

                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    drawGame(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }

                lastTime = now;
            }
            android.os.SystemClock.sleep(16);
        }
    }

    private void update() {
        if (snake == null || snake.isEmpty() || isGameOver || isPaused) return;

        Point head = new Point(snake.get(0).x, snake.get(0).y);
        switch (currentDirection) {
            case UP: head.y -= 1; break;
            case DOWN: head.y += 1; break;
            case LEFT: head.x -= 1; break;
            case RIGHT: head.x += 1; break;
        }

        if (head.x < 0 || head.x >= cols || head.y < 0 || head.y >= rows) {
            isGameOver = true;
            playSound(gameOverSoundId);
            return;
        }

        for (Point p : snake) {
            if (p.x == head.x && p.y == head.y) {
                isGameOver = true;
                playSound(gameOverSoundId);
                return;
            }
        }

        snake.add(0, head);

        if (food != null && head.x == food.x && head.y == food.y) {
            score += 1;
            notifyScore();
            placeFood();
            playSound(eatSoundId);
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void showGameAlertdialog(int score) {
        Context context = getContext();
        if (!(context instanceof Activity activity)) return;

        final Dialog dialog = new Dialog(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_snake_game_over, activity.findViewById(android.R.id.content), false);

        TextView gameScoreView = dialogView.findViewById(R.id.gameScore);
        TextView exitBtn = dialogView.findViewById(R.id.exitBtn);
        TextView restartBtn = dialogView.findViewById(R.id.restartBtn);

        gameScoreView.setText(context.getString(R.string.score_view, score));

        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.9);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        exitBtn.setOnClickListener(v -> {
            dialog.dismiss();
            AdManager.showInterstitialAd(activity, activity::finish);
        });

        restartBtn.setOnClickListener(v -> {
            dialog.dismiss();
            AdManager.showRewardedAd(activity, reward -> {}, this::resetGame);
        });

        dialog.show();
    }

    private void drawGame(Canvas canvas) {
        if (snake == null) return;

        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.snake_canvas_bg));

        int totalWidth = canvas.getWidth();
        int totalHeight = canvas.getHeight();
        int size = Math.min(totalWidth - 48, totalHeight - 48);
        if (size <= 0) return;

        int left = (totalWidth - size) / 2;
        int top = (totalHeight - size) / 2;
        int right = left + size;
        int bottom = top + size;
        int cellSize = size / cols;

        canvas.drawRect(left, top, right, bottom, bgPaint);

        for (int r = 0; r <= rows; r++) {
            int y = top + r * cellSize;
            canvas.drawLine(left, y, right, y, gridPaint);
        }
        for (int c = 0; c <= cols; c++) {
            int x = left + c * cellSize;
            canvas.drawLine(x, top, x, bottom, gridPaint);
        }

        for (Point p : snake) {
            int px = left + p.x * cellSize;
            int py = top + p.y * cellSize;
            canvas.drawRect(px + 2, py + 2, px + cellSize - 2, py + cellSize - 2, snakePaint);
        }

        if (food != null) {
            float cx = left + food.x * cellSize + cellSize / 2f;
            float cy = top + food.y * cellSize + cellSize / 2f;
            float radius = cellSize * 0.35f;
            canvas.drawCircle(cx, cy, radius, foodPaint);
        }

        canvas.drawRect(left, top, right, bottom, borderPaint);
    }
}
