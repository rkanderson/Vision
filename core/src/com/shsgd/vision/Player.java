package com.shsgd.vision;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by ryananderson on 3/26/16.
 */
public class Player {

    private float width = 0.9f, height = 0.9f;
    private int moveSpeed = 4;
    private int jumpForce = 8;
    private int orientation; //0-3 gravity direction. 0=down, 1=right 2=up 3=left

    private World world;
    private Body body;
    private Fixture fixture;
    private Texture texture;

    //input booleans
    private boolean[] moveRight = {false, false}, moveLeft = {false, false};
    // in each array, first boolean is if the player is ACTUALLY moving that direction
    // 2nd boolean indicates if it is given priority.
    // player moves left, moveLeft == {true, true} . presses right while holding down left and moveRight == {false, true},
    // the moment left key is released, moveRight == {true, true}
    private boolean shouldJump = false;

    public Player(World world, float x, float y) {
        this.world = world;
        orientation = 0; //zero by default
        texture = new Texture("character.png");

        //make box2d body and fixture
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        //shape
        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox(width/2, height/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rectangle;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.friction = 0.0f;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

    }

    public void update(float delta){
        //I will have to move in different ways depending on the player orientation
        if(orientation==0){
            if(moveRight[0]) body.setLinearVelocity(moveSpeed, body.getLinearVelocity().y);
            else if(moveLeft[0]) body.setLinearVelocity(-moveSpeed, body.getLinearVelocity().y);
            else body.setLinearVelocity(0, body.getLinearVelocity().y);
        } else if(orientation==1){
            if(moveRight[0]) body.setLinearVelocity(body.getLinearVelocity().x, moveSpeed);
            else if(moveLeft[0]) body.setLinearVelocity(body.getLinearVelocity().x, -moveSpeed);
            else body.setLinearVelocity(body.getLinearVelocity().x, 0);
        } else if(orientation==2){
            if(moveRight[0]) body.setLinearVelocity(-moveSpeed, body.getLinearVelocity().y);
            else if(moveLeft[0]) body.setLinearVelocity(moveSpeed, body.getLinearVelocity().y);
            else body.setLinearVelocity(0, body.getLinearVelocity().y);
        } else if(orientation==3){
            if(moveRight[0]) body.setLinearVelocity(body.getLinearVelocity().x, -moveSpeed);
            else if(moveLeft[0]) body.setLinearVelocity(body.getLinearVelocity().x, moveSpeed);
            else body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }

        //Perhaps I shouldJump too :). again, varies with orientation
        if(shouldJump){
            shouldJump=false;
            switch (orientation){
                case 0:
                    body.applyLinearImpulse(0, jumpForce, 0, 0 ,true);
                    break;
                case 1:
                    body.applyLinearImpulse(-jumpForce, 0, 0, 0, true);
                    break;
                case 2:
                    body.applyLinearImpulse(0, -jumpForce, 0, 0, true);
                    break;
                case 3:
                    body.applyLinearImpulse(jumpForce, 0, 0, 0, true);
                    break;
            }
        }

    }

    //PlayScreen will pass down input events important to the player class
    public void keyDown(int keycode){
        if(keycode == Input.Keys.UP){
            //Jump
            shouldJump=true;
        } else if(keycode == Input.Keys.RIGHT){
            //move right
            moveRight[1] = true; //that's for certain
            if(!moveLeft[0]) moveRight[0] = true;
            //body.setLinearVelocity(moveSpeed, body.getLinearVelocity().y);
        } else if(keycode == Input.Keys.LEFT){
            //move left
            moveLeft[1] = true;
            if(!moveRight[0]) moveLeft[0] = true;
            //body.setLinearVelocity(-moveSpeed, body.getLinearVelocity().y);
        }


        //what about changing gravity? (WASD)
        else if(keycode == Input.Keys.W){
            //reverse orientation
            switch (orientation){
                case 0: orientation = 2; break;
                case 1: orientation = 3; break;
                case 2: orientation = 0; break;
                case 3: orientation = 1; break;
            }
            updateWorldGravityBasedOnPlayerOrientation();
            body.setLinearVelocity(0,0); //stop moving just cuz
        } else if(keycode == Input.Keys.A){
            //set gravity to the left (counter-clockwise) of the current down
            if(orientation==0) orientation=3;
            else orientation-=1;
            updateWorldGravityBasedOnPlayerOrientation();
            body.setLinearVelocity(0,0); //stop moving just cuz
        } else if(keycode == Input.Keys.D){
            if(orientation==3) orientation=0;
            else orientation+=1;
            updateWorldGravityBasedOnPlayerOrientation();
            body.setLinearVelocity(0,0); //stop moving just cuz
        }
    }

    public void updateWorldGravityBasedOnPlayerOrientation(){
        if(orientation==0) world.setGravity(new Vector2(0, -PlayScreen.gravity_constant)); //down
        else if(orientation==1) world.setGravity(new Vector2(PlayScreen.gravity_constant, 0)); //right
        else if(orientation==2) world.setGravity(new Vector2(0, PlayScreen.gravity_constant)); //up
        else if(orientation==3) world.setGravity(new Vector2(-PlayScreen.gravity_constant, 0)); //left
    }

    public void keyUp(int keycode){
        if(keycode == Input.Keys.RIGHT){
            moveRight[0] = false; moveRight[1] = false;
            if(moveLeft[1]) moveLeft[0] = true;
        } else if(keycode == Input.Keys.LEFT){
            moveLeft[0] = false; moveLeft[1] = false;
            if(moveRight[1]) moveRight[0] = true;
        }
    }

    public Body getBody() {
        return body;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getOrientation() {
        return orientation;
    }
}
