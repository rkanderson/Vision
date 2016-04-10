package com.shsgd.vision.GameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
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
    private Body body;
    private Fixture fixture;

    private static float width=1;
    private static float height=1;

    private static BodyDef bodyDef;
    private static FixtureDef fixtureDef;
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

    public Key(World world, float x, float y) {
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

    public static void defineTextures(Map map){

    }

    public void dispose(){
        rectangle.dispose();
    }

}
