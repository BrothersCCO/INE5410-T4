package controlled;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
	public static Semaphore pesquisadores = new Semaphore(6);
	public static ExecutorService threads = Executors.newFixedThreadPool(12);
	public static ArrayBlockingQueue<File> encontrados = new ArrayBlockingQueue<File>(
			4);

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		System.out.print("Que nome de arquivo deseja pesquisar? ");
		String nome;
		nome = in.nextLine().trim();
		if (nome.isEmpty()) {
			nome = ".*";
		} else {
			nome = nome.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
		}

		System.out.print("Que extensão de arquivo deseja pesquisar (sem ponto)? ");
		String extensao;
		extensao = in.nextLine().trim();
		if (!extensao.isEmpty()) {
			extensao = "\\." + extensao.replaceAll("\\.", "\\.");
		}
		nome += extensao.replaceAll("\\*", ".*");
		System.out.println(nome);

		System.out.print("Que tamanho de arquivo deseja pesquisar?\nÉ possível usar operadores lógicos (<n e >n) e prefixos (k, m, g) ");
		Long tam_min = 0L, tam_max = Long.MAX_VALUE;
		while (true) {
			try {
				String aux = in.nextLine().trim().toLowerCase();
				if (aux.isEmpty()) {
					break;
				}
				int tam_mult = 1;
				switch (aux.charAt(aux.length() - 1)) {
				case 'g':
					tam_mult *= 1024;
				case 'm':
					tam_mult *= 1024;
				case 'k':
					tam_mult *= 1024;
					aux = aux.substring(0, aux.length() - 1);
					break;
				default:
					throw new NumberFormatException();
				}

				switch (aux.charAt(0)) {
				case '<':
					tam_max = Long.valueOf(aux.substring(1)) * tam_mult;
					break;
				case '>':
					tam_min = Long.valueOf(aux.substring(1)) * tam_mult;
					break;
				default:
					tam_min = tam_max = Long.valueOf(aux);
				}
				break;
			} catch (NumberFormatException e) {
				System.out.print("O número é inválido. Tente novamente: ");
			}
		}

		System.out.print("Que data de modificação do arquivo deseja pesquisar?\nÉ possível usar operadores lógicos (<n e >n) ");
		Date data_min = new Date(0), data_max = new Date();
		while (true) {
			try {
				String aux = in.nextLine().trim();
				if (aux.isEmpty()) {
					break;
				}
				switch (aux.charAt(0)) {
				case '<':
					data_max = new SimpleDateFormat("yyyy-mm-dd").parse(aux.substring(1));
					break;
				case '>':
					data_min = new SimpleDateFormat("yyyy-mm-dd").parse(aux.substring(1));
					break;
				default:
					data_min = data_max = new SimpleDateFormat("yyyy-mm-dd").parse(aux);
				}
				break;
			} catch (ParseException e) {
				System.out.println("A data é inválida. Formato: yyyy-mm-dd");
			}
		}

		System.out.print("Que conteúdo no arquivo deseja pesquisar? ");
		String conteudo = null;
		conteudo = in.nextLine();

		System.out.print("Que diretório deseja pesquisar? ");
		File file;
		while (true) {
			String aux = in.nextLine();
			if (aux.isEmpty()) {
				aux = ".";
			}
			file = Paths.get(aux).normalize().toFile();
			if (!file.isDirectory()) {
				System.out.println("O diretório não existe. Tente novamente: ");
			} else {
				break;
			}
		}
		in.close();
		
		Task.setTask(file);
		threads.submit(new Pesquisador(nome, tam_min, tam_max, data_min, data_max, conteudo));
		
		Print printer = new Print();
		printer.start();
		try {
			threads.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		printer.stopp();

		/*
		 * boolean acabou = false; while (!acabou) { acabou = true; for (int i =
		 * 0; i < pesquisadores.size(); ++i) { if
		 * (pesquisadores.get(i).isAlive()) { acabou = false; break; } } }
		 */
	}
}
