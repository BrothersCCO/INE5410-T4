package controlled;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class Pesquisador extends Thread {
	String nome;
	Long tam_min, tam_max;
	Date data_min, data_max;
	String conteudo;

	public Pesquisador(String nome, Long tam_min, Long tam_max, Date data_min,
			Date data_max, String conteudo) {
		this.nome = nome;
		this.tam_min = tam_min;
		this.tam_max = tam_max;
		this.data_min = data_min;
		this.data_max = data_max;
		this.conteudo = conteudo;
	}

	public void run() {
		while (Main.caminhos.size() > 0) {
			File path = Main.caminhos.remove();
			if (!path.canRead()) {
				return;
			}
			try {
				Main.pesquisadores.acquire();
				File[] subdirs = path.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.isDirectory();
					}
				});
				for (File subdir : subdirs) {
					Main.caminhos.add(subdir);
				}

				File[] matches = path.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						Long tam = new Long(pathname.length());
						Date modif = new Date(pathname.lastModified());
						String fnome = pathname.getName();
						return fnome.matches(nome)
								&& tam >= tam_min
								&& tam <= tam_max
								&& (!modif.before(data_min))
								&& (!modif.after(data_max));
					}
				});
				for (File arq : matches) {
					if (!conteudo.isEmpty()) {
						Scanner f = new Scanner(arq);
						while (f.hasNextLine()) {
							if (f.nextLine().contains(conteudo)) {
								Main.encontrados.put(arq);
								break;
							}
						}
						f.close();
					} else {
						Main.encontrados.put(arq);
					}
				}
				Main.pesquisadores.release();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
