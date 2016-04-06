package com.shsgd.vision.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.shsgd.vision.Main;

/**
 * Created by ryananderson on 3/29/16.
 */
public class MenuScreen implements Screen, InputProcessor {

    private Main game;
    Rectangle[] levelButtons;
    boolean[] lightUp; //for lighting up buttons
    ShapeRenderer sr = new ShapeRenderer();
    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map;

    public MenuScreen(Main main) {
        this.game = main;
        Gdx.input.setInputProcessor(this);
        levelButtons = new Rectangle[Main.LEVEL_COUNT];
        lightUp = new boolean[25]; for(int i=0; i<lightUp.length; i++)lightUp[i]=false;
        map = mapLoader.load("menu-screen.tmx");
        for(RectangleMapObject obj: map.getLayers().get("Buttons").getObjects().getByType(RectangleMapObject.class)){
            Rectangle newRect = obj.getRectangle();
            int leveIndex = Integer.parseInt((String) (obj.getProperties().get("i")));
            levelButtons[leveIndex] = newRect;

        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for(int i=0;i<levelButtons.length;i++){
            Rectangle r = levelButtons[i];
            if(r==null) continue;
            if(lightUp[i]) sr.setColor(0, 0.5f, 1, 1);
            else sr.setColor(0, 0.8f, 0.8f, 1);
            sr.rect(r.x, r.y, r.width, r.height);
        }
        sr.end();

    }

    @Override
    public void resize(int width, int height) {

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

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //calculate x and y vals as if screen is V_WIDTH and V_HEIGHT with proportions
        int x = screenX*Main.V_WIDTH/Gdx.graphics.getWidth();
        int y = (Main.V_HEIGHT-screenY)*Main.V_HEIGHT/Gdx.graphics.getHeight();
        for(int i=0; i<levelButtons.length; i++){
            if(levelButtons[i]==null) continue;
            if(levelButtons[i].contains(x, y)) game.setScreen(new PlayScreen(game, i));
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //calculate x and y vals as if screen is V_WIDTH and V_HEIGHT with proportions
        int x = screenX*Main.V_WIDTH/Gdx.graphics.getWidth();
        int y = (Main.V_HEIGHT-screenY)*Main.V_HEIGHT/Gdx.graphics.getHeight();
        for(int i=0; i<levelButtons.length; i++){
            if(levelButtons[i]==null) continue;
            lightUp[i] = levelButtons[i].contains(x, y);
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
