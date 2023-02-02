package se.yrgo.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import se.yrgo.game.Main;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(480, 800);
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setTitle("jumpyBirb");
		new Lwjgl3Application(new JumpyBirb(), config);
	}
}
