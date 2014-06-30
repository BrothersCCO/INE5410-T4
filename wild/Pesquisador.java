package wild;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class Pesquisador extends Thread {
	String nome;
	Long tam_min, tam_max;
	Date data_min, data_max;
	String conteudo;
	File path;

	public Pesquisador(String nome, Long tam_min, Long tam_max, Date data_min,
			Date data_max, String conteudo, File file) {
		super();
		this.nome = nome;
		this.tam_min = tam_min;
		this.tam_max = tam_max;
		this.data_min = data_min;
		this.data_max = data_max;
		this.conteudo = conteudo;
		this.path = file;
	}

	public void run() {
		if (!path.canRead()) {
			return;
		}
		try {
			File[] subdirs = path.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			for (File subdir : subdirs) {
				Pesquisador aux = new Pesquisador(nome, tam_min, tam_max,
						data_min, data_max, conteudo, subdir);
				Main.pesquisadores.add(aux);
				aux.start();
			}

			File[] matches = path.listFiles(new FilenameFilter() {
				public boolean accept(File arg0, String arg1) {
					Long tam = new Long(arg0.length());
					Date modif = new Date(arg0.lastModified());
					return arg1.matches(nome)
							&& tam >= tam_min
							&& tam <= tam_max
							&& (modif.after(data_min) || modif.equals(data_min))
							&& (modif.before(data_max) || modif.equals(data_max));
				}
			});
			for (File arq : matches) {
				if (!conteudo.isEmpty()) {
					Scanner f = new Scanner(arq);
					while (f.hasNextLine()) {
						if (f.nextLine().contains(conteudo)) {
							System.out.println(arq.getCanonicalPath());
							break;
						}
					}
					f.close();
				} else {
					System.out.println(arq.getCanonicalPath());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Erro em " + path);
		}
	}
}
