package se.yrgo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import se.yrgo.game.JumpyBirb;
import se.yrgo.game.objects.*;
import se.yrgo.game.utils.Score;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class GameScreen implements Screen {
    private final JumpyBirb game;
    private Doge doge;
    private Array<Ground> groundArray;
    private Music music;
    private OrthographicCamera camera;
    private FitViewport vp;
    private Array<Pipe> pipeArray;
    private long pipeSpawnTime;
    private long groundSpawnTime;
    private boolean isDead;
    private Score score;

    public GameScreen(final JumpyBirb game, Score score) {
        this.game = game;
        //create doge & ground object with x & y position
        doge = new Doge(20, game.CAMY / 2);

        // background music
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.CAMX, game.CAMY);
        vp = new FitViewport(game.CAMX, game.CAMY, camera);

        // Array av ground
        Ground firstGround = new Ground(0, -75);
        groundArray = new Array<Ground>();
        //Array av pipes
        pipeArray = new Array<Pipe>();

        groundArray.add(firstGround);
        spawnGround();
        spawnPipes();
        isDead = false;

        this.score = score;
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        vp.apply();
        camera.update();
        game.batch.setProjectionMatrix(vp.getCamera().combined);

        game.batch.begin();
        game.batch.draw(game.backGround, 0, 0, game.CAMX, game.CAMY);

        game.batch.draw(doge.getTexture(), doge.getPosition().x, doge.getPosition().y, doge.getTexture().getWidth(), doge.getTexture().getHeight());
        drawPipes();
        drawGround();
        game.font.draw(game.batch, score.getLayout(), score.getX(), score.getY());
        game.batch.end();

        //spawn pipes in the given time
        if (TimeUtils.nanoTime() - pipeSpawnTime > 3000000000L) {
            spawnPipes();
        }
        if (TimeUtils.nanoTime() - groundSpawnTime > 3_350_000_000L) spawnGround();

        loopOverGround();
        loopOverPipes();

        doge.update(delta);

        checkPlayerInput(delta);

        checkIfHitCeiling();

        checkIfDead();

        score.getLayout().setText(game.font, score.scoreToString());
    }


    @Override
    public void show() {
        //music.play();
        score.resetScore();

    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        music.dispose();
        doge.dispose();
    }

    private void spawnPipes() {
        int isAdding = ThreadLocalRandom.current().nextInt(2);
        int middleSpace = ThreadLocalRandom.current().nextInt(Pipe.getDISTANCE());
        Pipe pipe = new Pipe(game.CAMX, game.CAMY / 2 - game.CAMY + (isAdding == 1 ? middleSpace / 2 : -middleSpace / 2));
        pipeArray.add(pipe);
        pipeSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnGround() {
        Ground ground = new Ground(game.CAMX, -75);
        groundArray.add(ground);
        groundSpawnTime = TimeUtils.nanoTime();
    }

    private void loopOverPipes() {
        for (Iterator<Pipe> iter = pipeArray.iterator(); iter.hasNext(); ) {
            Pipe pipe = iter.next();

            pipe.move();

            removePipe(iter, pipe);

            checkCollision(pipe);

            updateScore(pipe);
        }
    }

    private void loopOverGround() {
        for (Iterator<Ground> iter = groundArray.iterator(); iter.hasNext(); ) {
            Ground ground = iter.next();

            ground.move();

            removeGround(iter, ground);

            checkCollisionGround(ground);
        }
    }

    private void checkCollision(Pipe pipe) {
        if (doge.isCollided(pipe.getHitBoxTop()) || doge.isCollided(pipe.getHitBoxBottom())) {
            isDead = true;
        }
    }

    private void checkCollisionGround(Ground ground) {
        if(doge.isCollided(ground.getHitBox())) isDead = true;
    }


    private static void removePipe(Iterator<Pipe> iter, Pipe pipe) {
        if (pipe.getPositionTop().x + pipe.getHitBoxTop().getWidth() < 0
                || pipe.getPositionBottom().x + pipe.getHitBoxBottom().getWidth() < 0) {
            pipe.dispose();
            iter.remove();
        }
    }

    private void removeGround(Iterator<Ground> iter, Ground ground) {
        if (ground.getPosition().x + ground.getHitBox().getWidth() * 2 < 0) {
            ground.dispose();
            iter.remove();
        }
    }

    private void updateScore(Pipe pipe) {
        if ((pipe.getPositionBottom().x + pipe.getHitBoxBottom().x) < doge.getPosition().x && !pipe.isScored()) {
            score.score();
            pipe.setScored(true);
        }
    }

    private void drawPipes() {
        for (Pipe pipe : pipeArray) {
            game.batch.draw(pipe.getTopPipeImg(), pipe.getPositionTop().x, pipe.getPositionTop().y);
            game.batch.draw(pipe.getBottomPipeImg(), pipe.getPositionBottom().x, pipe.getPositionBottom().y);
        }
    }

    private void drawGround() {
        for (Ground ground : groundArray) {
            game.batch.draw(ground.getTexture(), ground.getPosition().x, ground.getPosition().y, ground.getTexture().getWidth() * 2, ground.getTexture().getHeight());
        }

    }

    private void checkIfDead() {
        if (isDead) {
            score.newHighScore();
            game.setScreen(new DeathScreen(game, score));
            dispose();
        }
    }

    private void checkIfHitCeiling() {
        if (doge.getPosition().y >= (game.CAMY - doge.getTexture().getHeight())) {
            doge.getPosition().y = (game.CAMY - doge.getTexture().getHeight());
            doge.resetVelocity();
        }
    }

    private void checkPlayerInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
            doge.jump(delta);
        }
    }
}
