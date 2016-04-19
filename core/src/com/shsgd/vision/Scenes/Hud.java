
package com.shsgd.vision.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.shsgd.vision.Main;

/**
 * Created by brentaureli on 8/17/15.
 */
public class Hud implements Disposable{

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //Mario score/time Tracking Variables
    private Integer shifts;
    private int level;
    private int land;

    private Color textColor = new Color(0, 1, 0, 1);
    //Scene2D widgets
    private Label levelLandLabel;
    private Label shiftsLabel;

    public Hud(SpriteBatch sb, int shifts, int level, int land){
        //define our tracking variables
        this.shifts = shifts;
        this.level = level;
        this.land = land;


        //setup the HUD viewport using a new camera seperate from our gamecam
        //define our stage using that viewport and our games spritebatch
        viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        /*//define a table used to organize our hud's labels
        Table table = new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);*/

        //define our labels using the String, and a Label style consisting of a font and color
        levelLandLabel = new Label(String.format("Lvl %02d-%02d", land, level), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        shiftsLabel = new Label("Shifts  "+shifts, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        //add our labels to our table, padding the top, and giving them all equal width with expandX

        /*table.padLeft(10);
        table.add(levelLandLabel).expandX().padTop(10);
        //table.add(levelLandLabel).left().padTop(10);*/

 //       table.add(new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX().padTop(10);
   //     table.add(new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX().padTop(10);        table.add(new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX().padTop(10);



        //table.padRight(10);
       // table.add(shiftsLabel).expandX().padTop(10);
        //table.add(shiftsLabel).padTop(10);*/
        Table t1 = new Table();
        t1.setFillParent(true);
        t1.top(); t1.left();
        levelLandLabel.setColor(textColor);
        t1.add(levelLandLabel).padTop(10).padLeft(10);

        Table t2 = new Table();
        t2.setFillParent(true);
        t2.top(); t2.right();
        shiftsLabel.setColor(textColor);
        t2.add(shiftsLabel).padTop(10).padRight(10);

        //add our table to the stage
        stage.addActor(t1);
        stage.addActor(t2);

    }

    public void update(float dt){

    }

    public void setShifts(int shifts){
        this.shifts = shifts;
        shiftsLabel.setText("Shifts  "+shifts);
    }

    @Override
    public void dispose() { stage.dispose(); }

}
