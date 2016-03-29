package com.shsgd.vision.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.shsgd.vision.Main;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Main.V_WIDTH, Main.V_HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Main();
        }
}