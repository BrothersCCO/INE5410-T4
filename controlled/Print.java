package controlled;

import java.io.File;
import java.io.IOException;

public class Print extends Thread {
	private boolean stop = false;
	
	public void run() {
		while (!stop) {
			try {
				File f = Main.encontrados.take();
				System.out.println(f.getCanonicalPath());
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stopp() {
		stop = true;
	}
}
