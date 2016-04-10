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
import com.badlogic.gdx.utils.Disposable;
import com.shsgd.vision.Utils.C;


/**
 * Created by ryananderson on 4/6/16.
 */
public class Key implements Disposable {
    private World world;
    private Map map;
    private Body body;
    private Fixture fixture;

    private static float width=0.5f;
    private static float height=0.5f;

    private static BodyDef bodyDef;
    public static FixtureDef fixtureDef;
    private static PolygonShape rectangle;

    private static Texture texture; //TODO define texture

    static {
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;
        rectangle = new PolygonShape();

        fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = C.KEY_BIT;
    }

    public Key(Map map, World world, float x, float y) {
        this.map = map;
        this.world = world;

        //make box2d body and fixture
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        //shape
        rectangle.setAsBox(width/2, height/2);
        fixtureDef.shape = rectangle;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

    }

    public void disable(){
        getCell().setTile(null);
        setCategoryFilter(C.DISABLED_BIT);
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

    public void dispose(){
        rectangle.dispose();
    }

}
