package com.shsgd.vision.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by ryananderson on 3/26/16.
 */
public class MyInputProcessor implements InputProcessor {

    //Please note that this is only meant for PlayScreen.
    //Other screens implement InputProcessor directly inside the class

    com.shsgd.vision.Screens.PlayScreen playScreen;

    public MyInputProcessor(com.shsgd.vision.Screens.PlayScreen callback) {
        this.playScreen = callback;
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.R){
            playScreen.restart();
        } else if(keycode== Input.Keys.M){
            playScreen.returnToMenu();
        } else if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.LEFT || keycode == Input.Keys.UP){
            playScreen.playerMovementEvent(keycode);
        } else if( keycode == Input.Keys.W || keycode == Input.Keys.A || keycode == Input.Keys.D){
            playScreen.gravityShiftEvent(keycode);
        } else if(keycode == Input.Keys.TAB){
            playScreen.showb2drLines = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.LEFT || keycode == Input.Keys.UP){
            playScreen.importantPlayerKeyUpEvent(keycode);
        } else if (keycode == Input.Keys.TAB){
            playScreen.showb2drLines = false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
