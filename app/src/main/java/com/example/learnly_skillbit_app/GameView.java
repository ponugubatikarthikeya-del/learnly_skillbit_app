package com.example.learnly_skillbit_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private boolean dailogShown = false;

    // Public direction enum so activity can set
    public enum Direction {UP, DOWN, LEFT, RIGHT}

    // Listener interface for score update
    public interface OnScoreChangeListener {
        void onScoreChanged(int newScore);
    }

    private Thread gameThread;
    private volatile boolean running = false;
    private SurfaceHolder holder;

    private final int rows = 15;         // grid size (15x15)
    private final int cols = 15;
    private Paint gridPaint, snakePaint, foodPaint, bgPaint, borderPaint;
    private ArrayList<Point> snake;
    private Point food;
    private Direction currentDirection = Direction.RIGHT;
    private Random random = new Random();
    private int score = 0;
    private boolean isGameOver = false;

    private OnScoreChangeListener scoreListener;

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
        bgPaint.setColor(Color.parseColor("#113033")); // dark board

        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#1E3B3F"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);

        snakePaint = new Paint();
        snakePaint.setColor(Color.parseColor("#2FA74E")); // green

        foodPaint = new Paint();
        foodPaint.setColor(Color.parseColor("#E63A3A")); // red

        borderPaint = new Paint();
        borderPaint.setColor(Color.parseColor("#0B2B3A"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6);
    }

    // Allow MainActivity to set a callback to show score
    public void setOnScoreChangeListener(OnScoreChangeListener listener) {
        this.scoreListener = listener;
    }

    public void changeDirection(Direction dir) {
        // Prevent immediate reverse (classic rule)
        if ((currentDirection == Direction.UP && dir == Direction.DOWN) ||
                (currentDirection == Direction.DOWN && dir == Direction.UP) ||
                (currentDirection == Direction.LEFT && dir == Direction.RIGHT) ||
                (currentDirection == Direction.RIGHT && dir == Direction.LEFT)) {
            return;
        }
        currentDirection = dir;
    }

    public void resetGame() {
        dailogShown = false;
        isGameOver = false;
        score = 0;
        notifyScore();
        snake = new ArrayList<>();
        // start with length 4 in center
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
        // pick random empty spot
        while (true) {
            int x = random.nextInt(cols);
            int y = random.nextInt(rows);
            boolean clash = false;
            for (Point p : snake) {
                if (p.x == x && p.y == y) {
                    clash = true;
                    break;
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
    public void surfaceCreated(SurfaceHolder holder) {
        resetGame();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    public void pause() {
        running = false;
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
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Core loop
    @Override
    public void run() {
        long updateDelay = 200; // movement speed: 200ms
        long lastTime = System.currentTimeMillis();

        while (running) {
            if (!holder.getSurface().isValid()) continue;

            long now = System.currentTimeMillis();
            if (now - lastTime >= updateDelay) {
                update();      // update game state
                if (isGameOver && !dailogShown) {
                    dailogShown = true;
                    ((Activity) getContext()).runOnUiThread(() -> {
                       try{ Dialog dialog =showGameOverdialog(score);
                        dialog.show();}
                       catch(Exception e){
                           Log.e("GameView", "Error showing dialog: " + e.getMessage());
                        }
                    });

                }
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    drawGame(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }
                lastTime = now;
            } else {
                // small sleep to reduce CPU
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    // Update snake position, check collisions, food eaten
    private void update() {
        if(isGameOver) return;
        // compute new head position
        Point head = new Point(snake.get(0).x, snake.get(0).y);
        switch (currentDirection) {
            case UP:
                head.y -= 1;
                break;
            case DOWN:
                head.y += 1;
                break;
            case LEFT:
                head.x -= 1;
                break;
            case RIGHT:
                head.x += 1;
                break;
        }

        // Check wall collision => resetGame (game over behavior)
        if (head.x < 0 || head.x >= cols || head.y < 0 || head.y >= rows) {
            // game over: restart
            isGameOver = true;
            return;
        }

        // Check self-collision
        for (Point p : snake) {
            if (p.x == head.x && p.y == head.y) {
                isGameOver = true;
                return;
            }
        }

        // Add head
        snake.add(0, head);

        // Check for food
        if (food != null && head.x == food.x && head.y == food.y) {
            score += 1;
            notifyScore();
            // grow: do not remove tail
            placeFood();
        } else {
            // normal move: remove tail
            snake.remove(snake.size() - 1);
        }
    }

    private Dialog showGameOverdialog(int score) {
        // Use the Activity context safely
        Context context = getContext();
        if (!(context instanceof Activity)) return null;
        Activity activity = (Activity) context;

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.gameover_dialog, null);

        // Find views inside custom dialog
        TextView gameScoreView = dialogView.findViewById(R.id.gameScore);
        TextView exitBtn = dialogView.findViewById(R.id.exitBtn);
        TextView restartBtn = dialogView.findViewById(R.id.restartBtn);

        // Set score dynamically
        gameScoreView.setText("Score: " + score);

        // Create dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        // Set dialog size and animation
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Button listeners
        exitBtn.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish(); // closes app
        });

        restartBtn.setOnClickListener(v -> {
            dialog.dismiss();
            resetGame(); // restart game
        });

        // Must run on UI thread since GameView is not UI thread
        activity.runOnUiThread(dialog::show);

        return dialog;
    }


    // Draw everything
    private void drawGame(Canvas canvas) {
        // clear background
        canvas.drawColor(Color.parseColor("#E8F7FF"));

        int width = canvas.getWidth() - 48; // margins
        int height = canvas.getHeight() - 48;
        int size = Math.min(width, height);
        int left = 24;
        int top = 24;
        int right = left + size;
        int bottom = top + size;

        // compute cell size
        // computed at runtime
        int cellSize = size / cols;

        // draw board background
        canvas.drawRect(left, top, right, bottom, bgPaint);

        // draw grid lines
        for (int r = 0; r <= rows; r++) {
            int y = top + r * cellSize;
            canvas.drawLine(left, y, right, y, gridPaint);
        }
        for (int c = 0; c <= cols; c++) {
            int x = left + c * cellSize;
            canvas.drawLine(x, top, x, bottom, gridPaint);
        }

        // draw snake
        for (Point p : snake) {
            int px = left + p.x * cellSize;
            int py = top + p.y * cellSize;
            canvas.drawRect(px + 4, py + 4, px + cellSize - 4, py + cellSize - 4, snakePaint);
        }

        // draw food
        if (food != null) {
            float cx = left + food.x * cellSize + cellSize / 2f;
            float cy = top + food.y * cellSize + cellSize / 2f;
            float radius = cellSize * 0.35f;
            canvas.drawCircle(cx, cy, radius, foodPaint);
        }

        // border
        canvas.drawRect(left, top, right, bottom, borderPaint);
    }
}
