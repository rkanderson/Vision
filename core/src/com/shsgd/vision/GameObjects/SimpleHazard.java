package com.shsgd.vision.GameObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.shsgd.vision.Utils.C;

/**
 * Created by ryananderson on 4/5/16.
 */
public class SimpleHazard {
    //Represents things the player simply can't touch. Such as lava, acid, and WATER (erm... maybe)
    private World world;
    private Body body;
    private Fixture fixture;

    private static BodyDef bodyDef;
    private static FixtureDef fixtureDef;
    private static PolygonShape rectangle;
    static {
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;
        rectangle = new PolygonShape();

        fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = C.SIMPLE_HAZARD_BIT;

    }

    public SimpleHazard(World world, float x, float y, float width, float height) {
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

    public void dispose(){
        rectangle.dispose();
    }
}
