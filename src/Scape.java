import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import java.io.File;

public class Scape {

	static Model model;

	public static void main(String[] args) throws Exception {
		initDisplay();
		model = new Model();
		initGL();
		while (!Display.isCloseRequested()) {
			GL11.glClearColor(0f, 0f, 0f, 1f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			model.draw();
			Display.update();
			Display.sync(60); //Limit to 60 fps
		}
		model.delete();
		Display.destroy();
	}

	public static void initDisplay() throws LWJGLException {
		String natives = nativesPath();
		System.setProperty("org.lwjgl.librarypath", natives);
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		Display.create();
	}

	public static void initGL() {
		model.create();
	}

	public static String nativesPath() {
		System.out.println("Setting Natives");
		String os = System.getProperty("os.name").toLowerCase();
		String suffix = "";
		if (os.contains("win")) {
			suffix = "windows";
		} else if (os.contains("mac")) {
			suffix = "macosx";
		} else {
			suffix = "linux";
		}
		return System.getProperty("user.dir") + File.separator + "native" + File.separator + suffix;
	}

}

