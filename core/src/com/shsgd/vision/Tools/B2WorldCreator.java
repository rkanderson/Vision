package com.shsgd.vision.Tools;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.shsgd.vision.GameObjects.Goal;
import com.shsgd.vision.GameObjects.Platform;
import com.shsgd.vision.GameObjects.SimpleHazard;
import com.shsgd.vision.Screens.PlayScreen;
import com.shsgd.vision.Player;
import static com.shsgd.vision.Screens.PlayScreen.PPM;

/**
 * Created by ryananderson on 4/3/16.
 */
public class B2WorldCreator {
    Array<Platform> platforms = new Array<Platform>();
    Array<SimpleHazard> simpleHazards = new Array<SimpleHazard>();
    Player player;
    Goal goal;
    public B2WorldCreator(PlayScreen playScreen, World world, Map map) {

        //find player spawn coordinates. I use an ellipse object to represent the spawn point (center is exact position)
        EllipseMapObject playerSpawn = (EllipseMapObject)(map.getLayers().get("player-spawn")
                .getObjects().getByType(EllipseMapObject.class).get(0));
        float spawnX = (playerSpawn.getEllipse().x+playerSpawn.getEllipse().width/2) / PlayScreen.PPM;
        float spawnY = (playerSpawn.getEllipse().y+playerSpawn.getEllipse().height/2) / PlayScreen.PPM;
        player = new Player(world, spawnX, spawnY);

        //Make a round sensor body for the goal
        EllipseMapObject goalEllipse = (EllipseMapObject)(map.getLayers().get("goal")
                .getObjects().getByType(EllipseMapObject.class).get(0));
        goal = new Goal(world, (goalEllipse.getEllipse().x + goalEllipse.getEllipse().width/2)/PPM,
                (goalEllipse.getEllipse().y + goalEllipse.getEllipse().height/2)/PPM, goalEllipse.getEllipse().width/2/PPM);



        //Create a Platform for all MapObjects in the Ground layer of my_map.tmx
        if(map.getLayers().get("platforms")!=null)
        for(MapObject object : map.getLayers().get("platforms").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect  = ((RectangleMapObject)object).getRectangle();
            platforms.add(new Platform(world, (rect.x+rect.width/2)/PlayScreen.PPM,
                    (rect.y+rect.height/2)/PlayScreen.PPM, rect.width/PlayScreen.PPM, rect.height/PlayScreen.PPM));
        }

        //Create simple hazards
        if(map.getLayers().get("simple-hazards")!=null)
        for(MapObject object : map.getLayers().get("simple-hazards").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect  = ((RectangleMapObject)object).getRectangle();
            simpleHazards.add(new SimpleHazard(world, (rect.x+rect.width/2)/PlayScreen.PPM,
                    (rect.y+rect.height/2)/PlayScreen.PPM, rect.width/PlayScreen.PPM, rect.height/PlayScreen.PPM));

        }

        //code for random polygons in fun-shape layer
        if(map.getLayers().get("fun-shape")!=null)
        for(MapObject object : map.getLayers().get("fun-shape").getObjects().getByType(PolygonMapObject.class)){
            //System.out.println("omg");
            float vertices[] = ((PolygonMapObject)object).getPolygon().getVertices();
            for(int i=0; i<vertices.length; i++) vertices[i] /= PPM;
            PolygonShape shape = new PolygonShape(); shape.set(vertices);
            BodyDef bodyDef = new BodyDef(); bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(((PolygonMapObject)object).getPolygon().getX()/PPM, ((PolygonMapObject)object).getPolygon().getY()/PPM);
            Body body = world.createBody(bodyDef);
            body.createFixture(shape, 1.0f);
        }



    }

    public Array<Platform> getPlatforms() {
        return platforms;
    }

    public Player getPlayer() {
        return player;
    }

    public Goal getGoal() {
        return goal;
    }

    public Array<SimpleHazard> getSimpleHazards() {
        return simpleHazards;
    }
}
