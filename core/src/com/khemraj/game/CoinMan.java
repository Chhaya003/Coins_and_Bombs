package com.khemraj.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man ;
	Texture gamOver;
	Texture gameover;
	int manState = 0;
	int pause = 0;
	float gravity = 0.7f;
	float velocity = 0f;
	int manY = 0;
	int score = 0;
	int gameState = 0;
	Preferences preferences;

	BitmapFont bitmapFont;
	BitmapFont scoreFont;
	BitmapFont fontHighscore;

	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Rectangle manRectangle;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	Texture bomb;
	int bombCount;
	int highScore = 0;
	Random rand;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manY = Gdx.graphics.getHeight()/2;
		rand = new Random();
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		bitmapFont = new BitmapFont();
		bitmapFont.setColor(Color.WHITE);
		bitmapFont.getData().setScale(4);
		gamOver = new Texture("dizzy-1.png");
		gameover = new Texture("gameover.png");
		preferences = Gdx.app.getPreferences("gameHighscore");
		highScore = preferences.getInteger("highscore",0);
		fontHighscore = new BitmapFont();
		fontHighscore.getData().scale(3f);
		fontHighscore.setColor(Color.RED);
		scoreFont = new BitmapFont();
		scoreFont.getData().scale(3f);
		scoreFont.setColor(Color.RED);

	}

	public void makeCoin(){
		float height = rand.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}
	public void makebomb(){
		float height = rand.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1){
			//Game is started
			//for Bombs
			if(bombCount < 150){
				bombCount++;
			}else {
				bombCount = 0;
				makebomb();
			}
			bombRectangles.clear();
			for(int i=0; i<bombXs.size(); i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 10);
				bombRectangles.add(new Rectangle(bombXs.get(i) , bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}


			//for Coins
			if(coinCount < 50){
				coinCount++;
			}else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for(int i=0; i<coinXs.size(); i++){
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 6);
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}



			if(Gdx.input.justTouched()){
				velocity = -15;
			}
			if(pause < 4){
				pause++;
			}else {
				pause = 0;
				if(manState < 3){
					manState++;
				}else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if(manY < 0){
				manY = 0;
			}
			if(manY > Gdx.graphics.getHeight() - man[manState].getHeight()){
				manY = Gdx.graphics.getHeight() - man[manState].getHeight();
			}

		}else if(gameState == 0){
			//Waiting To Start
			if(Gdx.input.justTouched()){
				gameState = 1;
			}

		}else if(gameState == 2){
			//GameOver

			batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2, Gdx.graphics.getHeight()*3/4 - gameover.getHeight()/2);
			fontHighscore.draw(batch, "HighScore: " + String.valueOf(highScore), Gdx.graphics.getWidth()*35/100, Gdx.graphics.getHeight()*3/5);
			scoreFont.draw(batch, "Your Score: " + String.valueOf(score), Gdx.graphics.getWidth()*35/100, Gdx.graphics.getHeight()*3/6);

			if(Gdx.input.justTouched()){

				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinYs.clear();
				coinXs.clear();
				bombYs.clear();
				bombXs.clear();
				coinRectangles.clear();
				bombRectangles.clear();
				coinCount = 0;
				bombCount = 0;

			}
		}




		if(gameState == 2){
			batch.draw(gamOver, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() * 3 / 2, manY);
		}else {

			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() * 3 / 2, manY);
		}
		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()*3/2, manY, man[manState].getWidth(), man[manState].getHeight());

		for(int i=0; i<coinRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
				score++;
				if(score > highScore){
					highScore = score;
					preferences.putInteger("highscore",highScore);
					preferences.flush();
				}

				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}


		for(int i=0; i<bombRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
				gameState = 2;
			}
		}

		bitmapFont.draw(batch, "Score: " + String.valueOf(score), 70,Gdx.graphics.getHeight() - 70);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();

	}
}
