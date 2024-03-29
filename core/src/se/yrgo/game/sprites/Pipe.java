package se.yrgo.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import se.yrgo.game.SuchJump;
import se.yrgo.utils.Animation;
import se.yrgo.utils.Difficulty;

import java.util.Iterator;

public class Pipe {
    private boolean isScored;
    private Texture kettleImg;
    private Rectangle hitBoxKettle;
    private Rectangle hitBoxChain;
    private float kettleWidth;
    private float kettleHeight;
    private float saladWidth;
    private float saladHeight;

    private Rectangle hitBoxSaladBody;
    private Rectangle hitBoxSaladHand;
    private Vector3 positionKettle;
    private Vector3 positionSalad;
    private Texture saladFingersImg;

    private Animation saladAnimation;

    public Pipe(int x, int y) {
        saladWidth = 55;
        saladHeight = 320;
        saladFingersImg = new Texture("saladFingersAnimation.png");
        saladAnimation = new Animation(new TextureRegion(saladFingersImg), 3, 0.8f);
        hitBoxSaladBody = new Rectangle(x, y, saladWidth - 22, saladHeight);
        hitBoxSaladHand = new Rectangle(x, y, saladWidth, 27);

        positionSalad = new Vector3(x, y, 0);
        positionKettle = new Vector3(x, y + saladHeight + Difficulty.getPipeDistance(), 0);

        kettleWidth = 40;
        kettleHeight = 250;
        kettleImg = new Texture("rustyKettle.png");
        hitBoxKettle = new Rectangle(x, y, kettleWidth, 40f);
        hitBoxChain = new Rectangle(x + (kettleWidth / 2), y, kettleWidth / 7, kettleHeight);

    }


    public void draw(SuchJump game) {
        game.getBatch().draw(getKettleImg(), getPositionKettle().x, getPositionKettle().y, kettleWidth, kettleHeight);
        game.getBatch().draw(getSaladFingersImg(), getPositionSalad().x, getPositionSalad().y, saladFingersImg.getWidth() / 3f, saladFingersImg.getHeight());
    }


    public void move() {
        getPositionSalad().x -= Difficulty.getSpeed() * Gdx.graphics.getDeltaTime();
        getPositionKettle().x -= Difficulty.getSpeed() * Gdx.graphics.getDeltaTime();

        hitBoxKettle.setPosition(getPositionKettle().x, getPositionKettle().y);

        hitBoxChain.setPosition(getPositionKettle().x + (kettleWidth / 2), getPositionKettle().y);
        //Change magicnumber  to offset variable.
        hitBoxSaladBody.setPosition(getPositionSalad().x + 22, getPositionSalad().y);
        hitBoxSaladHand.setPosition(getPositionSalad().x, getPositionSalad().y + saladHeight - 40);

    }


    public void remove(Iterator<Pipe> iter) {
        if (getPositionSalad().x + saladWidth < 0) {
            dispose();
            iter.remove();
        }
    }

    public void dispose() {
        kettleImg.dispose();
        saladFingersImg.dispose();
    }

    public Texture getKettleImg() {
        return kettleImg;
    }

    public TextureRegion getSaladFingersImg() {
        return saladAnimation.getFrame();
    }

    public Rectangle getHitBoxKettle() {
        return hitBoxKettle;
    }

    public Rectangle getHitBoxChain() {
        return hitBoxChain;
    }

    public Rectangle getHitBoxSaladBody() {
        return hitBoxSaladBody;
    }

    public Vector3 getPositionKettle() {
        return positionKettle;
    }

    public Vector3 getPositionSalad() {
        return positionSalad;
    }

    public void setScored(boolean scored) {
        isScored = scored;
    }
    public boolean isScored() {
        return isScored;
    }

    public Animation getSaladAnimation() {
        return saladAnimation;
    }

    public Rectangle getHitBoxSaladHand() {
        return hitBoxSaladHand;
    }
}
