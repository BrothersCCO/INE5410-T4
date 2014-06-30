package wild;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static List<Pesquisador> pesquisadores = new LinkedList<Pesquisador>();

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

		System.out
				.print("Que extensão de arquivo deseja pesquisar (sem ponto)? ");
		String extensao;
		extensao = in.nextLine().trim();
		if (!extensao.isEmpty()) {
			extensao = "\\." + extensao.replaceAll("\\.", "\\.");
		}
		nome += extensao.replaceAll("\\*", ".*");
		System.out.println(nome);

		System.out
				.print("Que tamanho de arquivo deseja pesquisar?\nÉ possível usar operadores lógicos (<n e >n) e prefixos (k, m, g) ");
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

		System.out
				.print("Que data de modificação do arquivo deseja pesquisar?\nÉ possível usar operadores lógicos (<n e >n) ");
		Date data_min = new Date(0), data_max = new Date();
		while (true) {
			try {
				String aux = in.nextLine().trim();
				if (aux.isEmpty()) {
					break;
				}
				switch (aux.charAt(0)) {
				case '<':
					data_max = new SimpleDateFormat("yyyy-mm-dd").parse(aux
							.substring(1));
					break;
				case '>':
					data_min = new SimpleDateFormat("yyyy-mm-dd").parse(aux
							.substring(1));
					break;
				default:
					data_min = data_max = new SimpleDateFormat("yyyy-mm-dd")
							.parse(aux);
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
		Path path;
		while (true) {
			String aux = in.nextLine();
			if (aux.isEmpty()) {
				aux = ".";
			}
			path = Paths.get(aux).normalize();
			if (path.toFile().isDirectory()) {
				break;
			} else {
				System.out.println("O diretório não existe. Tente novamente: ");
			}
		}

		Pesquisador aux = new Pesquisador(nome, tam_min, tam_max, data_min,
				data_max, conteudo, path.toFile());
		pesquisadores.add(aux);
		aux.start();

		in.close();

		/*
		 * boolean acabou = false; while (!acabou) { acabou = true; for (int i =
		 * 0; i < pesquisadores.size(); ++i) { if
		 * (pesquisadores.get(i).isAlive()) { acabou = false; break; } } }
		 */
	}
}
