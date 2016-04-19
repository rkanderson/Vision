package com.shsgd.vision.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shsgd.vision.Main;
import com.shsgd.vision.Utils.C;



/**
 * Created by ryananderson on 3/29/16.
 */
public class MenuScreen implements Screen, InputProcessor {

    private Main game;

    private OrthographicCamera cam;
    private Viewport viewport;

    LevelButton[] levelButtons;
    ShapeRenderer sr = new ShapeRenderer();
    TmxMapLoader mapLoader = new TmxMapLoader();
    TiledMap map;

    SpriteBatch sb = new SpriteBatch();

    private BitmapFont bmf;
    private GlyphLayout glyphLayout;

    public MenuScreen(Main main) {
        this.game = main;
        Gdx.input.setInputProcessor(this);

        cam = new OrthographicCamera();
        viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, cam);
        map = mapLoader.load("menu-screen.tmx");

        levelButtons = new LevelButton[Main.LEVEL_COUNT];
        for(RectangleMapObject obj: map.getLayers().get("Buttons").getObjects().getByType(RectangleMapObject.class)){
            Rectangle newRect = obj.getRectangle();
            int leveIndex = Integer.parseInt((String) (obj.getProperties().get("i")));
            levelButtons[leveIndex] = new LevelButton(newRect, leveIndex);

        }

        //define font
        bmf = new BitmapFont(Gdx.files.internal("gudone.fnt"));
        glyphLayout = new GlyphLayout();


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //updating
        //for the camera
        cam.position.set(C.MENU_MAP_WIDTH/2, C.MENU_MAP_HEIGHT/2, 0);
        cam.update();

        //rendering
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for(int i=0;i<levelButtons.length;i++){
            LevelButton b = levelButtons[i];
            if(b==null) continue;
            if(b.isLit) sr.setColor(0, 0.8f, 1, 1);
            else sr.setColor(0, 0.5f, 0.5f, 1);
            sr.rect(b.rect.x, b.rect.y, b.rect.width, b.rect.height);
        }
        sr.end();

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        bmf.setColor(1,1,1,1);
        for(LevelButton btn: levelButtons){
            if(btn==null)continue;
            glyphLayout.setText(bmf, Integer.toString(btn.levelIndex));
            bmf.draw(sb, glyphLayout, btn.rect.x+btn.rect.width/2-glyphLayout.width/2,
                    btn.rect.y+btn.rect.height/2+glyphLayout.height/2);
        }
        sb.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        sr.dispose();
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
        Vector3 newCoords = cam.unproject(new Vector3(screenX, screenY, 0), viewport.getScreenX(),
                viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
        for(int i=0; i<levelButtons.length; i++){
            if(levelButtons[i]==null) continue;
            if(levelButtons[i].rect.contains(newCoords.x, newCoords.y)) game.setScreen(new PlayScreen(game, i));
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

        Vector3 newCoords = cam.unproject(new Vector3(screenX, screenY, 0), viewport.getScreenX(),
                viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
        for(int i=0; i<levelButtons.length; i++){
            if(levelButtons[i]==null) continue;
            levelButtons[i].isLit = levelButtons[i].rect.contains(newCoords.x, newCoords.y);
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public class LevelButton {
        Rectangle rect;
        int levelIndex;
        boolean isLit;

        public LevelButton(Rectangle rect, int levelIndex){
            this.rect = rect;
            this.levelIndex = levelIndex;
            isLit = false;
        }
    }
}
