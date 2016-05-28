package com.shsgd.vision.Tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.shsgd.vision.Player;

public class PlayerAnimation {
    Player player;
    private Array<TextureRegion>[] frameSets = new Array[3];
    private int animationState = 1; //ranges from 0 to 2
                                    // 0 = idleFrames, 1 = walking frames, 2 = jumping frames

    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCount;
    private int frame;

    public PlayerAnimation(Player player, Texture region, int frameCount, float cycleTime){
        this.player = player;
        int frameWidth = region.getWidth()/frameCount;
        frameSets[0] = new Array<TextureRegion>();
        frameSets[0].add(new TextureRegion(region, 0*frameWidth, 0, frameWidth, region.getHeight()));

        frameSets[1] = new Array<TextureRegion>();
        frameSets[1].add(new TextureRegion(region, 1*frameWidth, 0, frameWidth, region.getHeight()));
        frameSets[1].add(new TextureRegion(region, 2*frameWidth, 0, frameWidth, region.getHeight()));
        //frameSets[1].add(new TextureRegion(region, 4*frameWidth, 0, frameWidth, region.getHeight()));
        frameSets[1].add(new TextureRegion(region, 2*frameWidth, 0, frameWidth, region.getHeight()));


        frameSets[2] = new Array<TextureRegion>();
        frameSets[2].add(new TextureRegion(region, 1*frameWidth, 0, frameWidth, region.getHeight()));

        this.frameCount = frameCount;
        maxFrameTime = cycleTime/frameCount;
        frame = 0;
    }

    public void update(float dt){

        //handle if the animation should flip
        setFramesetDirections(player.isFacingRight());

        currentFrameTime+=dt;

        if (currentFrameTime >= maxFrameTime){
            frame++;
            currentFrameTime = 0;
        }
        if (frame >= frameSets[animationState].size) frame = 0;
    }

    public TextureRegion getFrame(){
        return frameSets[animationState].get(frame);
    }

    public void setAnimationState(int animationState) {
        this.animationState = animationState;
    }

   public void setFramesetDirections(boolean facingRight){
       if(!frameSets[0].get(0).isFlipX()==facingRight) return;
        for(Array<TextureRegion> frameSet: frameSets){
            for(TextureRegion eachTextureRegion: frameSet){
                eachTextureRegion.flip(true, false);
            }
        }
    }
}