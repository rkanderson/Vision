package com.shsgd.vision.GameObjects;

import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.shsgd.vision.Utils.C;

/**
 * Created by ryananderson on 3/27/16.
 */
public class Goal {

    //The circular goal!

    private World world;
    private Body body;
    private Fixture fixture;

    public Goal(World world, float xCenter, float yCenter, float radius) {
        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type= BodyDef.BodyType.StaticBody;
        bodyDef.position.set(xCenter, yCenter);
        body = world.createBody(bodyDef);
        //System.out.println(body);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = C.GOAL_BIT;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();
    }

    public  void dispose(){

    }



}
