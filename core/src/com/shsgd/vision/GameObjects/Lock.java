package com.shsgd.vision.GameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.shsgd.vision.Screens.PlayScreen;
import com.shsgd.vision.Utils.C;

import javax.xml.soap.Text;

/**
 * Created by ryananderson on 4/5/16.
 */
public class Lock {
    private World world;
    private Body body;
    private Fixture fixture;
    private Map map;

    private static BodyDef bodyDef;
    public static FixtureDef fixtureDef;
    private static PolygonShape rectangle;

    int id;
    boolean isOpened = false;

    static {
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;
        rectangle = new PolygonShape();

        fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.filter.categoryBits = C.LOCK_BIT | C.SOLID_BIT;
    }

    public Lock(PlayScreen ps, World world, float x, float y, float width, float height, int id) {
        this.world = world;
        this.map = ps.getMap();

        //make box2d body and fixture
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        //shape
        rectangle.setAsBox(width/2, height/2);
        fixtureDef.shape = rectangle;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        this.id = id;

    }

    public void dispose(){
        rectangle.dispose();
    }

    public void setIsOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    public void disable() {
        getCell().setTile(null);
        setCategoryFilter(C.DISABLED_BIT);
        isOpened = true;
    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("graphics-main");
        return layer.getCell((int)(body.getPosition().x * C.PPM / C.TILE_WIDTH),
                (int)(body.getPosition().y * C.PPM / C.TILE_HEIGHT));
    }

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public int getId() {
        return id;
    }
}
