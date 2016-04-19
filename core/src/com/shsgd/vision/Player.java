package com.shsgd.vision;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.shsgd.vision.Utils.C;

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
    private Fixture foot;
    private Texture texture;
    public int keys = 0;

    //input booleans
    private boolean[] moveRight = {false, false}, moveLeft = {false, false};
    // in each array, first boolean is if the player is ACTUALLY moving that direction
    // 2nd boolean indicates if it is given priority.
    // player moves left, moveLeft == {true, true} . presses right while holding down left and moveRight == {false, true},
    // the moment left key is released, moveRight == {true, true}
    private boolean shouldJump = false, canJump = false;

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
        //PolygonShape rectangle = new PolygonShape();
        //rectangle.setAsBox(width/2, height/2);
        CircleShape rectangle = new CircleShape();
        rectangle.setRadius(width/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rectangle;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.filter.categoryBits = C.PLAYER_BIT;
        fixtureDef.filter.maskBits = C.SOLID_BIT | C.GOAL_BIT | C.KEY_BIT | C.LOCK_BIT | C.SIMPLE_HAZARD_BIT;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        rectangle.dispose();

        //Create the player "foot" sensor
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-width/4, -height/2-0.1f, width/4, -height/2-0.1f);
        fixtureDef.shape = edgeShape;
        fixtureDef.isSensor=true;
        fixtureDef.filter.categoryBits = C.PLAYER_FOOT_BIT;
        fixtureDef.filter.maskBits = C.SOLID_BIT;
        foot = body.createFixture(fixtureDef);
        foot.setUserData(new Foot(this, orientation));
        edgeShape.dispose();

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
        if(shouldJump && canJump){
            shouldJump=false;
            canJump=false;
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

    public void updateFootBasedOnOrientation(){
        //Updates the player foot based on the current orientation.
        //This method should be called whenever the gravity is changed
        ((Foot) foot.getUserData()).setOrientation(orientation);
        EdgeShape footShape = (EdgeShape)foot.getShape();

        switch (orientation){
            //Update the fixture properties
            case 0:
                footShape.set(-width/4, -height/2-0.1f, width/4, -height/2-0.1f);
                break;
            case 1:
                footShape.set(width/2+0.1f, height/4, width/2+0.1f, -height/4);
                break;
            case 2:
                footShape.set(-width/4, height/2+0.1f, width/4, height/2+0.1f);
                break;
            case 3:
                footShape.set(-width/2-0.1f, height/4, -width/2-0.1f, -height/4);
                break;

        }
    }

    //PlayScreen will pass down input events important to the player class
    public void keyDown(int keycode){



            //what about changing gravity? (WASD)



    }

    public void movementEvent(int keycode){
        if(keycode == Input.Keys.UP){
            //Jump
            if(canJump)shouldJump=true;
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
    }

    public void shiftGravity(int keycode){
        //First, change the player orientation based on what key was pressed and the current orientation
        if(keycode == Input.Keys.W){
            //reverse orientation
            switch (orientation){
                case 0: orientation = 2; break;
                case 1: orientation = 3; break;
                case 2: orientation = 0; break;
                case 3: orientation = 1; break;}
        } else if(keycode == Input.Keys.A){
            //counter-clockwise
            if(orientation==0) orientation=3;
            else orientation-=1;
        } else if(keycode == Input.Keys.D){
            //clockwise
            if(orientation==3) orientation=0;
            else orientation+=1;
        }

        updateWorldGravityBasedOnPlayerOrientation();
        body.setLinearVelocity(0, 0); //stop moving just cuz
        body.setAwake(true);
        updateFootBasedOnOrientation();
    }

    public void updateWorldGravityBasedOnPlayerOrientation(){
        if(orientation==0) world.setGravity(new Vector2(0, -C.gravity_constant)); //down
        else if(orientation==1) world.setGravity(new Vector2(C.gravity_constant, 0)); //right
        else if(orientation==2) world.setGravity(new Vector2(0, C.gravity_constant)); //up
        else if(orientation==3) world.setGravity(new Vector2(-C.gravity_constant, 0)); //left
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

    public void dispose(){
        texture.dispose();
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

    public void setCanJump(boolean canJump){this.canJump=canJump;}

    public int getKeys() {
        return keys;
    }

    public void useKey(){
        keys-=1;
    }

    public class Foot {
        public Player player;
        public int orientation;
        public Foot(Player player, int orientation){
            //identity==orientation basically
            this.player = player;
            this.orientation = orientation;
        }
        public void setOrientation(int orientation){this.orientation=orientation;}
    }
}
