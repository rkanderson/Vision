package com.shsgd.vision;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.shsgd.vision.Tools.PlayerAnimation;
import com.shsgd.vision.Utils.C;

/**
 * Created by ryananderson on 3/26/16.
 */
public class Player {

    private float width = 1f, height = 1f;
    private float moveSpeed = 4;
    private float jumpForce = 6f;
    private int orientation; //0-3 gravity direction. 0=down, 1=right 2=up 3=left

    private World world;
    private Body body;
    private Fixture fixture;
    private Fixture foot;
    private Texture texture;
    private PlayerAnimation animation;
    public int keys = 0;
    boolean facingRight = true;

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
        texture = new Texture("demon_spritesheet_full.png");
        animation = new PlayerAnimation(this, texture, 6, 0.7f);

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

        if(moveRight[0])facingRight=true;
        else if(moveLeft[0])facingRight=false;

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

        //change animations states as appropriate
        if(!canJump) animation.setAnimationState(2);

        else if(moveLeft[0] || moveRight[0]) animation.setAnimationState(1);

        else if(!moveRight[0] && !moveLeft[0] && canJump) {
            animation.setAnimationState(0);
        }

        animation.update(delta);

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
        //updateFootBasedOnOrientation();
        body.setTransform(body.getPosition().x, body.getPosition().y, (float)(orientation*Math.PI/2));
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

    public TextureRegion getTexture() {
        return animation.getFrame();
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

    public boolean isFacingRight() {
        return facingRight;
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
