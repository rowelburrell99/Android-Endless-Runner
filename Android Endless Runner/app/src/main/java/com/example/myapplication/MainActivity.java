package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Point;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

    public class MainActivity extends AppCompatActivity {

        // Frame
        private FrameLayout gameFrame;
        private int frameHeight, frameWidth, initialFrameWidth; //Layout of the menu & game
        private LinearLayout startLayout;

        // Image
        private ImageView sprite, spike, coin, heart; //image variables
        private Drawable imagespriteRight, imagespriteLeft;

        // Size
        private int spriteSize;

        // Position
        private float spriteX, spriteY;
        private float spikeX, spikeY;
        private float coinX, coinY;
        private float heartX, heartY;

        // Score
        private TextView scoreLabel, highScoreLabel;
        private int score, highScore, timeCount;
        private SharedPreferences settings;

        // Class
        private Timer timer;
        private Handler handler = new Handler();
        private SoundPlayer soundPlayer;

        // Status
        private boolean start_flg = false;
        private boolean action_flg = false;
        private boolean heart_flg = false;

        //speed
        private int spriteSpeed;
        private int coinSpeed;
        private int heartSpeed;
        private int spikeSpeed;

        public int screenWidth;
        public int screenHeight;
        public int difficulty;


        /**
         * This method creates the activity and initialises all of the objects/sounds.
         * @param savedInstanceState
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            soundPlayer = new SoundPlayer(this);
            setVolumeControlStream(AudioManager.STREAM_MUSIC); //the volume buttons on the phone dictate the volume of the application, when it is visible.

            gameFrame = findViewById(R.id.gameFrame);
            startLayout = findViewById(R.id.startLayout);
            sprite = findViewById(R.id.sprite);
            spike = findViewById(R.id.spike);
            coin = findViewById(R.id.coin);
            heart = findViewById(R.id.heart);
            scoreLabel = findViewById(R.id.scoreLabel);
            highScoreLabel = findViewById(R.id.highScoreLabel);

            imagespriteLeft = getResources().getDrawable(R.drawable.char_left); //associating variable to character sprite left image
            imagespriteRight = getResources().getDrawable(R.drawable.char_right); //associating variable to character sprite right image

            // High Score
            settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
            highScore = settings.getInt("HIGH_SCORE", 0);
            highScoreLabel.setText("High Score : " + highScore);

            WindowManager wm = getWindowManager();
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenWidth = size.x;
            screenHeight = size.y;

            //Nexus 5x w 1080 h 1920 dpi 420
            //heart -20 spike -100 sprite +14 coin -100
            spriteSpeed = Math.round(screenHeight / 137F);
            coinSpeed = Math.round(screenWidth / -10.8F);
            heartSpeed = Math.round(screenWidth / -54F);
            spikeSpeed = Math.round(screenWidth / -10.8F);

            Log.v("spriteSpeed", spriteSpeed+"");
            Log.v("coinSpeed", coinSpeed+"");
            Log.v("heartSpeed", heartSpeed+"");
            Log.v("spikeSpeed", spikeSpeed+"");

        }

        /**
         * This is used to consistenly update the game.
         * Objects are also modified depending on the difficulty.
         * Sound will play if character model and objects collide.
         */
        public void changePos() {

            // Add timeCount
            timeCount += 20; //every 20 milliseconds the timer will call the changePos method, therefore, udpating everything.

            // coin
            coinY += 12; //speed of how fast the coins fall down.

            float coinCenterX = coinX + coin.getWidth() / 2;
            float coinCenterY = coinY + coin.getHeight() / 2;

            if (hitCheck(coinCenterX, coinCenterY)) {
                coinY = frameHeight + 100;


                //modifying objects based on game difficulty.
                switch (difficulty) {
                    case 1:
                        score += 5; //if coin is collected the score will increase by 5.
                        break;
                    case 2:
                        score += 10; //if coin is collected the score will increase by 10.
                        break;
                    case 3:
                        score += 15; //if coin is collected the score will increase by 15.
                        break;
                }

                soundPlayer.playHitcoinSound();
            }

            if (coinY > frameHeight) {
                coinY = -100;
                coinX = (float) Math.floor(Math.random() * (frameWidth - coin.getWidth())); //generates a random number between 0, and the end of the frame width (minus the coin width),so when the coin falls down randomly it will fit within the frame.
            }
            coin.setX(coinX);
            coin.setY(coinY);

            if (!heart_flg && timeCount % 10000 == 0) { //creates a heart every 10 seconds.
                heart_flg = true;
                heartY = -20;
                heartX = (float) Math.floor(Math.random() * (frameWidth - heart.getWidth())); //allows the heart to fall down within the frame.
            }

            if (heart_flg) {

               switch(difficulty) {
                   case 1:
                       heartY += 15; //speed of how fast the hearts fall.
                       break;
                   case 2:
                       heartY += 20; //speed of how fast the hearts fall.
                       break;
                   case 3:
                       heartY += 25; //speed of how fast the hearts fall.
                       break;
               }

                float heartCenterX = heartX + heart.getWidth() / 2;
                float heartCenterY = heartY + heart.getWidth() / 2;

                if (hitCheck(heartCenterX, heartCenterY)) {
                    heartY = frameHeight + 30;
                    score += 30; //increases score by 30.
                    // Change FrameWidth
                    if (initialFrameWidth > frameWidth * 110 / 100) { //increases the framewidth by 10% if a heart is collected.
                        frameWidth = frameWidth * 110 / 100;
                        changeFrameWidth(frameWidth);
                    }
                    soundPlayer.playHitheartSound();
                }

                if (heartY > frameHeight) heart_flg = false;
                heart.setX(heartX);
                heart.setY(heartY);
            }

            // spike
            //modifying objects based on game difficulty.

            switch (difficulty) {
                case 1:
                    spikeY += 18; //speed of how fast the spikes fall.
                    break;
                case 2:
                    spikeY += 25; //speed of how fast the spikes fall.
                    break;
                case 3:
                    spikeY += 32; //speed of how fast the spikes fall.
                    break;
            }

            float spikeCenterX = spikeX + spike.getWidth() / 2;
            float spikeCenterY = spikeY + spike.getHeight() / 2;

            if (hitCheck(spikeCenterX, spikeCenterY)) {
                spikeY = frameHeight + 100;

                // Change FrameWidth

               switch (difficulty) {
                   case 1:
                       frameWidth = frameWidth * 80 / 100; //Decreases the framewidth by 20% if a spike hits the character.
                       break;
                   case 2:
                       frameWidth = frameWidth * 70 / 100; //Decreases the framewidth by 30%
                       break;
                   case 3:
                       frameWidth = frameWidth * 60 / 100; //Decreases the framewidth by 40%
                       break;
               }
                changeFrameWidth(frameWidth);
                soundPlayer.playHitspikeSound();
                if (frameWidth <= spriteSize) { //if the framewidth is less than the spritesize,  the game over method is executed.
                    gameOver();
                }

            }

            if (spikeY > frameHeight) {
                spikeY = -100;
                spikeX = (float) Math.floor(Math.random() * (frameWidth - spike.getWidth())); //allows the spike to fall down within the frame.
            }

            spike.setX(spikeX);
            spike.setY(spikeY);

            // Moves sprite
            if (action_flg) {
                // whilst the screen is being touched
                spriteX += 14; //how fast the character travels right
                sprite.setImageDrawable(imagespriteRight);
            } else {
                // when the touch has been released.
                spriteX -= 14; //how fast the character travels left
                sprite.setImageDrawable(imagespriteLeft);
            }

            // checks the characters position.
            if (spriteX < 0) {
                spriteX = 0;
                sprite.setImageDrawable(imagespriteRight);
            }
            if (frameWidth - spriteSize < spriteX) { //if the character is
                spriteX = frameWidth - spriteSize;
                sprite.setImageDrawable(imagespriteLeft);
            }

            sprite.setX(spriteX);

            scoreLabel.setText("Score : " + score); //score text at the top.

        }

        /**
         * Detects if my characters hitbox has been hit.
         * @param x
         * @param y
         * @return
         */
        public boolean hitCheck(float x, float y) {  //detects whether the character has been hit.
            if (spriteX <= x && x <= spriteX + spriteSize &&  //detects width of the character.
                    spriteY <= y && y <= frameHeight) { //detects height of the character.
                return true;
            }
            return false;
        }

        public void changeFrameWidth(int frameWidth) {
            ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
            params.width = frameWidth;
            gameFrame.setLayoutParams(params);
        }

        /**
         * When the game ends timer is cancelled, therefore, the main menu becomes visible and the game stops.
         * score and Highscore are saved and updated here.
         */
        public void gameOver() {
            // Stops timer.
            timer.cancel(); //when the game ends, the timer stops, meaning the game stops updating every 20 seconds.
            timer = null;
            start_flg = false; //changes boolean from true to false as the game is no longer running.

            // Before showing startLayout, sleep 1 second.
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            changeFrameWidth(initialFrameWidth);

            startLayout.setVisibility(View.VISIBLE); //Menu is now available
            //hides the game images.
            sprite.setVisibility(View.INVISIBLE);
            spike.setVisibility(View.INVISIBLE);
            coin.setVisibility(View.INVISIBLE);
            heart.setVisibility(View.INVISIBLE);

            // Update High Score
            if (score > highScore) { //if score is higher than highscore, the highscore will now become the score.
                highScore = score;
                highScoreLabel.setText("High Score : " + highScore); //text showing high score.

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("HIGH_SCORE", highScore); //keeps the highest score saved.
                editor.commit();
            }
        }

        /**
         * Used to move around the character through touch and release.
         * @param event
         * @return
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (start_flg) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //finger pressing down on screen.
                    action_flg = true;

                } else if (event.getAction() == MotionEvent.ACTION_UP) { //finger raised off screen.
                    action_flg = false;

                }
            }
            return true;
        }

        /**
         * Closes application.
         * @param view
         */
        public void exitGame(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask(); //closes app and removes it from recent task list.
            } else {
                finish();
            }
        }


        /**
         * difficulty of the games.
         * starts the game, therefore, main menu hides and objects are visible.
         * time is started to constantly update the game.
         * @param view
         */
      //Repeat of games but changing the difficulty integer to apply different modifications to the objects.
       public void easyGame (View view) {
            difficulty = 1; //game difficulty is set to 1, meaning the objects will be modified to meet the 'easy' criteria.
            start_flg = true;
            startLayout.setVisibility(View.INVISIBLE); //hides the menu when the game starts.

            if (frameHeight == 0) {
                frameHeight = gameFrame.getHeight();
                frameWidth = gameFrame.getWidth();
                initialFrameWidth = frameWidth;

                spriteSize = sprite.getHeight(); //gets the height  of my character model.
                spriteX = sprite.getX(); //gets x coordinates of my character model.
                spriteY = sprite.getY(); //gets y coordinates of my character model.
            }

            frameWidth = initialFrameWidth;

            sprite.setX(0.0f);   //character starts bottom left.
            spike.setY(3000.0f); //starts out of game frame.
            coin.setY(3000.0f); //starts out of game frame.
            heart.setY(3000.0f); //starts out of game frame.

            spikeY = spike.getY();
            coinY = coin.getY();
            heartY = heart.getY();

            sprite.setVisibility(View.VISIBLE); //object is now visible, when the game starts
            spike.setVisibility(View.VISIBLE); //object is now visible, when the game starts
            coin.setVisibility(View.VISIBLE); //object is now visible, when the game starts
            heart.setVisibility(View.VISIBLE); //object is now visible, when the game starts

            timeCount = 0;
            score = 0;
            scoreLabel.setText("Score : 0"); //sets the score to 0 whenever the game starts.


            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (start_flg) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changePos();
                            }
                        });
                    }
                }
            }, 0, 20);
        }

        //repeat of easy, just difficulty is set to 2.
        public void medGame (View view) {
            difficulty = 2;
            start_flg = true;
            startLayout.setVisibility(View.INVISIBLE);

            if (frameHeight == 0) {
                frameHeight = gameFrame.getHeight();
                frameWidth = gameFrame.getWidth();
                initialFrameWidth = frameWidth;

                spriteSize = sprite.getHeight();
                spriteX = sprite.getX();
                spriteY = sprite.getY();
            }

            frameWidth = initialFrameWidth;

            sprite.setX(0.0f);
            spike.setY(3000.0f);
            coin.setY(3000.0f);
            heart.setY(3000.0f);

            spikeY = spike.getY();
            coinY = coin.getY();
            heartY = heart.getY();

            sprite.setVisibility(View.VISIBLE);
            spike.setVisibility(View.VISIBLE);
            coin.setVisibility(View.VISIBLE);
            heart.setVisibility(View.VISIBLE);

            timeCount = 0;
            score = 0;
            scoreLabel.setText("Score : 0");


            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (start_flg) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changePos();
                            }
                        });
                    }
                }
            }, 0, 20);
        }

        //repeat of easy, just difficulty is set to 3.
        public void hardGame (View view) {
            difficulty = 3;
            start_flg = true;
            startLayout.setVisibility(View.INVISIBLE);

            if (frameHeight == 0) {
                frameHeight = gameFrame.getHeight();
                frameWidth = gameFrame.getWidth();
                initialFrameWidth = frameWidth;

                spriteSize = sprite.getHeight();
                spriteX = sprite.getX();
                spriteY = sprite.getY();
            }

            frameWidth = initialFrameWidth;

            sprite.setX(0.0f);
            spike.setY(3000.0f);
            coin.setY(3000.0f);
            heart.setY(3000.0f);

            spikeY = spike.getY();
            coinY = coin.getY();
            heartY = heart.getY();

            sprite.setVisibility(View.VISIBLE);
            spike.setVisibility(View.VISIBLE);
            coin.setVisibility(View.VISIBLE);
            heart.setVisibility(View.VISIBLE);

            timeCount = 0;
            score = 0;
            scoreLabel.setText("Score : 0");


            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (start_flg) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changePos();
                            }
                        });
                    }
                }
            }, 0, 20);
        }

}

