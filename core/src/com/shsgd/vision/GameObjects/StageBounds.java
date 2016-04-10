package com.shsgd.vision.GameObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.shsgd.vision.Screens.PlayScreen;
import com.shsgd.vision.Utils.C;

import static com.shsgd.vision.Utils.C.MAP_WIDTH;
import static com.shsgd.vision.Utils.C.MAP_HEIGHT;
import static com.shsgd.vision.Utils.C.PPM;

/**
 * Created by ryananderson on 4/4/16.
 */
public class StageBounds {
    //Represents the outer bounds of the stage.
    private World world;
    private Body body;
    private Fixture fixtureTop, fixtureLeft, fixtureRight, fixtureDown; //Fixture down will be a sensor so the player can fall off the stage. The others will be solid.
    public StageBounds(World world, float x, float y){
        this.world = world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();

        //This one is the YurScrewedLine
        edgeShape.set(-MAP_WIDTH/PPM/2, -MAP_HEIGHT/PPM/2-3/PPM, MAP_WIDTH/PPM/2, -MAP_HEIGHT/2/PPM-3/PPM);
        fixtureDef.shape = edgeShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = C.SIMPLE_HAZARD_BIT;
        fixtureDown = body.createFixture(fixtureDef);
        fixtureDown.setUserData(new YurScrewedLine());

        //Then we have the rest of the walls which are solid
        fixtureDef.isSensor = false;
        fixtureDef.filter.categoryBits = C.SOLID_BIT;
        edgeShape.set(MAP_WIDTH/PPM/2, -MAP_HEIGHT/PPM/2, MAP_WIDTH/PPM/2, MAP_HEIGHT/PPM/2);
        fixtureRight = body.createFixture(fixtureDef);

        edgeShape.set(-MAP_WIDTH/PPM/2, MAP_HEIGHT/PPM/2, MAP_WIDTH/PPM/2, MAP_HEIGHT/PPM/2);
        fixtureTop = body.createFixture(fixtureDef);

        edgeShape.set(-MAP_WIDTH/PPM/2, MAP_HEIGHT/PPM/2, -MAP_WIDTH/PPM/2, -MAP_HEIGHT/PPM/2);
        fixtureLeft = body.createFixture(fixtureDef);

    }

    public void setOrientation(int orientation){
        //similar to player. 0 is down. 1 is right, 2 is up, 3 is left
        float angle = orientation * (float)Math.toRadians(90);
        //System.out.println("stage bounds orientation set to "+orientation);
        body.setTransform(body.getPosition().x, body.getPosition().y, angle);
    }

    public class YurScrewedLine{}

}
