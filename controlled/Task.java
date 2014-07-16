package controlled;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class Task {
	private static Queue<File> caminhos = new LinkedList<File>();
	
	public static synchronized File getTask() {
		return caminhos.poll();
	}
	
	public static synchronized void setTask(File caminho) {
		caminhos.add(caminho);
	}
}