package com.shsgd.vision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javafx.geometry.Rectangle2DBuilder;


/**
 * Created by ryananderson on 3/26/16.
 */
public class PlayScreen implements Screen, ContactListener{

    public static final float PPM = 80;
    public static final float gravity_constant = 18;
    private Main game;
    private int myLevelIndex;
    private OrthographicCamera gameCamera = new OrthographicCamera(), b2drCamera = new OrthographicCamera();
        //game camera operates in pixels
        // b2drCamera in meters
    private Viewport b2drViewport, gameViewport;
    private static float zoomFactor = 0.468f;
    private Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();
    private ShapeRenderer sr = new ShapeRenderer();
    private SpriteBatch sb = new SpriteBatch();
    private MyInputProcessor inputProcessor;

    TmxMapLoader tmxMapLoader;
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;

    private World world; //Box2D world yay!
    private Player player;
    private Goal goal;
    private Array<Platform> platforms = new Array<Platform>();

    //input booleans
    public boolean showb2drLines = false; //this gets true in MyInputProcessor


    public PlayScreen(Main game, int levelIndex){
        //@param levelIndex starts from zero; level1 == index 0

        System.out.println("A new play screen has been created.");
        this.game = game;
        myLevelIndex = levelIndex;
        gameCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor, Main.V_HEIGHT / zoomFactor);
        gameViewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, gameCamera);
        //b2drCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor / PPM, Main.V_HEIGHT / zoomFactor / PPM);
        //b2drViewport = new FitViewport(Main.V_WIDTH/PPM, Main.V_HEIGHT/PPM, gameCamera);

        inputProcessor = new MyInputProcessor(this);

        world = new World(new Vector2(0, -gravity_constant), false);
        world.setContactListener(this);

        tmxMapLoader = new TmxMapLoader();
        map = tmxMapLoader.load("level"+(levelIndex+1)+".tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        //find player spawn coordinates. I use an ellipse object to represent the spawn point (center is exact position)
        EllipseMapObject playerSpawn = (EllipseMapObject)(map.getLayers().get("player-spawn")
                .getObjects().getByType(EllipseMapObject.class).get(0));
        float spawnX = (playerSpawn.getEllipse().x-playerSpawn.getEllipse().width/2) / PPM,
                spawnY = (playerSpawn.getEllipse().y+playerSpawn.getEllipse().height/2) / PPM;
        player = new Player(world, spawnX, spawnY);

        //Make a round sensor body for the goal
        EllipseMapObject goalEllipse = (EllipseMapObject)(map.getLayers().get("goal")
                .getObjects().getByType(EllipseMapObject.class).get(0));
        goal = new Goal(world, (goalEllipse.getEllipse().x + goalEllipse.getEllipse().width/2)/PPM,
                (goalEllipse.getEllipse().y + goalEllipse.getEllipse().height/2)/PPM, goalEllipse.getEllipse().width/2/PPM);



        //Create a Platform for all MapObjects in the Ground layer of my_map.tmx
        for(MapObject object : map.getLayers().get("platforms").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect  = ((RectangleMapObject)object).getRectangle();
            platforms.add(new Platform(world, (rect.x+rect.width/2)/PPM, (rect.y+rect.height/2)/PPM, rect.width/PPM, rect.height/PPM));

        }

        //code for random polygons in fun-shape layer
        for(MapObject object : map.getLayers().get("fun-shape").getObjects().getByType(PolygonMapObject.class)){
            //System.out.println("omg");
            float vertices[] = ((PolygonMapObject)object).getPolygon().getVertices();
            for(int i=0; i<vertices.length; i++) vertices[i] /= PPM;
            PolygonShape shape = new PolygonShape(); shape.set(vertices);
            BodyDef bodyDef = new BodyDef(); bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(((PolygonMapObject)object).getPolygon().getX()/PPM, ((PolygonMapObject)object).getPolygon().getY()/PPM);
            Body body = world.createBody(bodyDef);
            body.createFixture(shape, 1.0f);
        }


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

        mapRenderer.setView(gameCamera);
        mapRenderer.render();

        //render player
        sb.setProjectionMatrix(gameCamera.combined);
        sb.begin();
        sb.draw(player.getTexture(), (player.getBody().getPosition().x-player.getWidth()/2) * PPM, (player.getBody().getPosition().y-player.getHeight()/2)*PPM, player.getWidth()*PPM, player.getHeight()*PPM);
        sb.end();

        if(showb2drLines)box2DDebugRenderer.render(world, b2drCamera.combined);



    }

    public void update(float delta){

        player.update(delta);
        world.step(1 / 60f, 8, 3);    // 60 FPS
    }

    public void handleInput(float delta){

    }


    public void updateCameraPosition(float delta){
        //REMEMBER b2drCamera uses meters and gameCamera uses pixels
        //in this method, I will update b2drCamera's position in meters and gameCamera
        //will copy it's position in pixels
        b2drCamera.position.set(640/PPM, 640/PPM, 0);
        b2drCamera.update();

        gameCamera.position.set(b2drCamera.position.x * PPM, b2drCamera.position.y * PPM, 0);
        gameCamera.update();
    }

    public void restart(){
        System.out.println("Game restarted");
        game.setScreen(new PlayScreen(game, myLevelIndex));
    }

    public void importantPlayerKeyDownEvent(int keycode){
        player.keyDown(keycode);
        if(keycode == Input.Keys.W || keycode == Input.Keys.A || keycode == Input.Keys.D) {
            updateCameraRotationBasedOnPlayerOrientation();
        }
        /*if(keycode== Input.Keys.W){
            gameCamera.rotate(180);
            b2drCamera.rotate(180);
        } else if(keycode == Input.Keys.A){
            gameCamera.rotate(90);
            b2drCamera.rotate(90);
        } else if(keycode == Input.Keys.D){
            gameCamera.rotate(-90);
            b2drCamera.rotate(-90);
        }*/
    }

    public void updateCameraRotationBasedOnPlayerOrientation(){
        gameCamera.up.set(0, 1, 0);
        gameCamera.direction.set(0, 0, -1);
        b2drCamera.up.set(0, 1, 0);
        b2drCamera.direction.set(0, 0, -1);
        gameCamera.rotate(player.getOrientation()*-90);
        b2drCamera.rotate(player.getOrientation()*-90);
    }

    public void importantPlayerKeyUpEvent(int keycode){
        player.keyUp(keycode);
    }

    @Override
    public void resize(int width, int height) {
        //gameViewport.update((int)(width / zoomFactor), (int)(height / zoomFactor));
        //b2drViewport.update((int) (width / zoomFactor /PPM), (int) (height /zoomFactor/ PPM));

        gameCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor, Main.V_HEIGHT / zoomFactor);
        b2drCamera.setToOrtho(false, Main.V_WIDTH / zoomFactor / PPM, Main.V_HEIGHT / zoomFactor / PPM);

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

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        //If player and goal touch, then get to next level
        if(a.getUserData() instanceof Player && b.getUserData() instanceof Goal ||
                a.getUserData() instanceof Goal && b.getUserData() instanceof Player){
            System.out.println("next level is level "+(myLevelIndex+2));
            game.setScreen(new PlayScreen(game, myLevelIndex+1));
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
}
