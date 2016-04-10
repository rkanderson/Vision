package com.shsgd.vision.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shsgd.vision.GameObjects.Goal;
import com.shsgd.vision.GameObjects.Lock;
import com.shsgd.vision.GameObjects.SimpleHazard;
import com.shsgd.vision.GameObjects.StageBounds;
import com.shsgd.vision.Main;
import com.shsgd.vision.GameObjects.Platform;
import com.shsgd.vision.Player;
import com.shsgd.vision.Scenes.Hud;
import com.shsgd.vision.Tools.B2WorldCreator;
import com.shsgd.vision.Utils.C;

/**
 * Created by ryananderson on 3/26/16.
 */
public class PlayScreen implements Screen, ContactListener{

    private Main game;
    private int myLevelIndex;
    private OrthographicCamera gameCamera = new OrthographicCamera(), b2drCamera = new OrthographicCamera();
        //game camera operates in pixels
        // b2drCamera in meters
    private Viewport b2drViewport, gameViewport;
    private static float zoomFactor = 2.343f;
    private Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();
    private ShapeRenderer sr = new ShapeRenderer();
    private SpriteBatch sb = new SpriteBatch(), bgsb=new SpriteBatch();
    private MyInputProcessor inputProcessor;

    TmxMapLoader tmxMapLoader;
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    int shifts;

    private World world; //Box2D world yay!
    private Player player;
    private Goal goal;
    private Array<Platform> platforms = new Array<Platform>();
    private Array<Lock> locks = new Array<Lock>();
    private StageBounds stageBounds;
    private Texture bg;
    private Hud hud;

    //input booleans
    public boolean showb2drLines = false; //this gets true in MyInputProcessor


    public PlayScreen(Main game, int levelIndex){
        //@param levelIndex starts from zero; level1 == index 0

        System.out.println("A new play screen has been created.");
        this.game = game;
        myLevelIndex = levelIndex;
        //gameCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor, Main.V_HEIGHT / zoomFactor);
        gameViewport = new FitViewport(Main.V_WIDTH / zoomFactor, Main.V_HEIGHT / zoomFactor, gameCamera);
        //b2drCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor / PPM, Main.V_HEIGHT / zoomFactor / PPM);
        b2drViewport = new FitViewport(Main.V_WIDTH/zoomFactor/ C.PPM, Main.V_HEIGHT/zoomFactor/C.PPM, b2drCamera);

        inputProcessor = new MyInputProcessor(this);

        world = new World(new Vector2(0, -C.gravity_constant), true);
        world.setContactListener(this);

        tmxMapLoader = new TmxMapLoader();
        map = tmxMapLoader.load("level" + levelIndex + ".tmx");
        shifts = Integer.parseInt((String) map.getProperties().get("allowedShifts"));
        System.out.println("Shifts: "+shifts);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        B2WorldCreator creator = new B2WorldCreator(this, world, map);
        player = creator.getPlayer();
        goal = creator.getGoal();
        platforms = creator.getPlatforms();
        locks = creator.getLocks();
        stageBounds = new StageBounds(world, C.MAP_WIDTH/C.PPM/2, C.MAP_HEIGHT/C.PPM/2);

        bg = null; //The background is optional.
        //bg = new Texture("images/hills.jpg");

        //create our game HUD for scores/timers/level info
        hud = new Hud(sb, shifts, levelIndex+1, 1);


    }

    @Override
    public void render(float delta) {
        //This render method will consist of 2 parts
        //1) updating the game state (different method called from this method)
        //2) rendering (drawing to screen)


        //---PART I--- The updating!
        handleInput(delta);
        update(delta);
        updateCameraPosition(delta);

        //---PART II--- The rendering!
        //Clear the game screen with Black

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(bg!=null){
        bgsb.begin();
        bgsb.draw(bg, 0, 0, Main.V_WIDTH, Main.V_HEIGHT); //code toc draw background
        bgsb.end();}

        mapRenderer.setView(gameCamera);
        mapRenderer.render();

        //render player
        sb.setProjectionMatrix(gameCamera.combined);
        sb.begin();
        sb.draw(player.getTexture(), (player.getBody().getPosition().x-player.getWidth()/2) * C.PPM, (player.getBody().getPosition().y-player.getHeight()/2)*C.PPM, player.getWidth()*C.PPM, player.getHeight()*C.PPM);
        sb.end();

        if(showb2drLines)box2DDebugRenderer.render(world, b2drCamera.combined);

        //Set our batch to now draw what the Hud camera sees.
        sb.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();


    }

    public void update(float delta){

        player.update(delta);
        world.step(1 / 60f, 8, 3);    // 60 FPS
        hud.update(delta);
    }

    public void handleInput(float delta){

    }


    public void updateCameraPosition(float delta){
        //REMEMBER b2drCamera uses meters and gameCamera uses pixels
        //in this method, I will update b2drCamera's position in meters and gameCamera
        //will copy it's position in pixels
        b2drCamera.position.set(C.MAP_WIDTH/C.PPM/2, C.MAP_HEIGHT/C.PPM/2, 0);
        b2drCamera.update();

        gameCamera.position.set(b2drCamera.position.x * C.PPM, b2drCamera.position.y * C.PPM, 0);
        gameCamera.update();
    }

    public void restart(){
        System.out.println("Level restarted");
        game.setScreen(new PlayScreen(game, myLevelIndex));
    }

    public void lose(){
        //Play sad sound effect
        restart();
    }

    public void returnToMenu(){
        game.setScreen(new com.shsgd.vision.Screens.MenuScreen(game));
    }


    public void gravityShiftEvent(int keycode){
        if(shifts<=0) return;
        player.shiftGravity(keycode);
        updateCameraRotationBasedOnPlayerOrientation();
        stageBounds.setOrientation(player.getOrientation());
        shifts-=1;
        hud.setShifts(shifts);
    }

    public void playerMovementEvent(int keycode){
        player.movementEvent(keycode);
    }

    public void updateCameraRotationBasedOnPlayerOrientation(){
        gameCamera.up.set(0, 1, 0);
        gameCamera.direction.set(0, 0, -1);
        b2drCamera.up.set(0, 1, 0);
        b2drCamera.direction.set(0, 0, -1);
        gameCamera.rotate(player.getOrientation()*-90);
        b2drCamera.rotate(player.getOrientation() * -90);
    }

    public void importantPlayerKeyUpEvent(int keycode){
        player.keyUp(keycode);
    }

    @Override
    public void resize(int width, int height) {
        //gameViewport.update((int)(width / zoomFactor), (int)(height / zoomFactor));
        //b2drViewport.update((int) (width / zoomFactor /PPM), (int) (height /zoomFactor/ PPM));

        gameViewport.update(width, height);
        b2drViewport.update(width, height);
        //gameCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor, Main.V_HEIGHT / zoomFactor);
        //b2drCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor / PPM, Main.V_HEIGHT / zoomFactor / PPM);

    }


    @Override
    public void show() {

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
        world.dispose();
        box2DDebugRenderer.dispose();
        sb.dispose();
        bgsb.dispose();
        map.dispose();
        mapRenderer.dispose();
        player.dispose();
        goal.dispose();
        for(Platform platform: platforms) platform.dispose();
        bg.dispose();
        hud.dispose();

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        int cDef = a.getFilterData().categoryBits | b.getFilterData().categoryBits;
        switch(cDef) {
            //If a player foot and a platform touch, I should enable the player to jump
            case C.PLAYER_FOOT_BIT | C.SOLID_BIT:
                player.setCanJump(true);
                break;


            //If the player touches a hazard (or the YurScrewedLine), then kill the player and lose()
            case C.PLAYER_BIT | C.SIMPLE_HAZARD_BIT:
                lose();
                break;

            case C.PLAYER_BIT | C.LOCK_BIT:
                Lock lock = a.getFilterData().categoryBits == Lock.fixtureDef.filter.categoryBits
                        ? (Lock)a.getUserData() : (Lock)b.getUserData();
                for(Lock l : locks){
                    if(l.getId() == lock.getId()) l.destroy();
                }
                break;


            //If player and goal touch, then get to next level
            case C.PLAYER_BIT | C.GOAL_BIT:
                if (myLevelIndex + 1 + 1 > Main.LEVEL_COUNT) {
                    game.setScreen(new PlayScreen(game, -1));
                } else {
                    game.setScreen(new PlayScreen(game, myLevelIndex + 1));
                }
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public TiledMap getMap() {
        return map;
    }
}
