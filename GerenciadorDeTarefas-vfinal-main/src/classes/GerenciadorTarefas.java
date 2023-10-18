package classes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class GerenciadorTarefas {

    private Usuario usuario;
    private FileWriter fileWriter;
    private List<Tarefa> tarefasPendentes;
    private final List<Tarefa> tarefasConcluidas;
    private final Scanner scanner;
    private final Gson gson;

    public GerenciadorTarefas() throws IOException {
        this.tarefasPendentes = new ArrayList<>();
        this.tarefasConcluidas = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.gson = new Gson();

        login();
    }

    public void login() throws IOException {
        this.usuario = new Usuario();

        System.out.print("Digite seu usuário: ");
        usuario.setNome(scanner.nextLine());

        System.out.print("Digite sua senha: ");
        usuario.setSenha(scanner.nextLine());

        File file = new File(usuario.getNomeArquivo());

        if (!file.exists()) {
            gravarArquivo();
            System.out.println("Arquivo criado para usuário!");
            buscarArquivo();
            exibirMenu();

        } else {
            JsonObject conteudoJson = new JsonParser().parse(lerArquivo(file)).getAsJsonObject();

            if (conteudoJson.get("usuario").getAsJsonObject().get("senha").getAsString()
                    .equals(this.usuario.getSenha())) {
                buscarArquivo();
                exibirMenu();
            } else {
                System.out.println("Usuário/Senha inválido.");
                login();
            }
        }
    }

    public void adicionarTarefa() {
        Tarefa tarefa = new Tarefa();

        System.out.println("Digite o título da tarefa:");
        tarefa.setTitulo(scanner.nextLine());

        System.out.println("Digite a descrição da tarefa:");
        tarefa.setDescricao(scanner.nextLine());

        System.out.println("Digite a categoria da tarefa:");
        tarefa.setCategoria(scanner.nextLine());

        tarefa.setDtCriacao(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        tarefa.setConcluida(false);
        tarefa.setId(this.getProximoId());

        this.tarefasPendentes.add(tarefa);

        subTarefaMsg(tarefa.getId());
        gravarArquivo();

        System.out.println("Tarefa criada com sucesso!");
    }

    private void subTarefaMsg(int id) {
        System.out.println("Deseja adicionar uma sub-tarefa? (S/N)");

        if (scanner.next().equalsIgnoreCase("S")) {
            adicionarSubTarefa(id);
            subTarefaMsg(id);
        }
    }

    public void adicionarSubTarefa(int idTarefa) {
        Tarefa tarefa = this.findTarefaPendenteById(idTarefa);
        SubTarefa subTarefa = new SubTarefa();

        System.out.println("Digite o título da sub-tarefa:");
        subTarefa.setTitulo(scanner.next());

        System.out.println("Digite a descrição da sub-tarefa:");
        subTarefa.setDescricao(scanner.next());

        subTarefa.setId(tarefa.getSubTarefas().size() + 1);

        tarefa.getSubTarefas().add(subTarefa);

        gravarArquivo();

        System.out.println("Sub-Tarefa criada com sucesso!");
    }

    private void gravarArquivo() {

        JsonObject dadosArquivo = new JsonObject();

        dadosArquivo.add("usuario", gson.toJsonTree(this.usuario));
        dadosArquivo.add("tarefasPendentes", gson.toJsonTree(this.tarefasPendentes));
        dadosArquivo.add("tarefasConcluidas", gson.toJsonTree(this.tarefasConcluidas));

        try {
            // Reescreve o arquivo com os dados atualizados do json
            this.fileWriter = new FileWriter(this.usuario.getNomeArquivo());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(dadosArquivo.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            System.err.println("Erro ao gravar arquivo: " + e.getMessage());
        }
    }

    private void buscarArquivo() {

        File arquivo = new File(this.usuario.getNomeArquivo());

        try {
            if (arquivo.exists()) {

                String conteudo = lerArquivo(arquivo);

                try {
                    JsonObject conteudoJson = new JsonParser().parse(conteudo).getAsJsonObject();

                    for (JsonElement j : conteudoJson.get("tarefasPendentes").getAsJsonArray()) {
                        this.tarefasPendentes.add(gson.fromJson(j, Tarefa.class));
                    }

                    for (JsonElement j : conteudoJson.get("tarefasConcluidas").getAsJsonArray()) {
                        this.tarefasConcluidas.add(gson.fromJson(j, Tarefa.class));
                    }
                } catch (IllegalStateException e) {
                    conteudo = null;
                }
            } else {
                this.fileWriter = new FileWriter(this.usuario.getNomeArquivo());
            }
        } catch (IOException e) {
            System.err.println("Erro ao buscar arquivo: " + e.getMessage());
        }
    }

    private String lerArquivo(File arquivo) throws IOException {
        FileReader fileReader = new FileReader(arquivo);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder conteudo = new StringBuilder();
        String linha;
        while ((linha = bufferedReader.readLine()) != null) {
            conteudo.append(linha).append("\n");
        }

        return conteudo.toString();
    }

    public void concluirTarefa(int id) {
        exibirTarefasPendentes();
        List<Tarefa> tarefasPend = new ArrayList<>();

        this.tarefasPendentes.forEach(tarefa -> {
            if (tarefa.getId() == id) {
                tarefa.setConcluida(true);
                tarefa.setDtConclusao(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                tarefasConcluidas.add(tarefa);
            } else {
                tarefasPend.add(tarefa);
            }
        });

        this.tarefasPendentes = tarefasPend;
        gravarArquivo();
    }

    private void exibirMenu() {
        boolean sair = false;
        while (!sair) {
            System.out.println("Digite uma opcao:");
            System.out.println("1 - Criar nova tarefa");
            System.out.println("2 - Concluir tarefa");
            System.out.println("3 - Exibir tarefas pendentes");
            System.out.println("4 - Exibir tarefas concluidas");
            System.out.println("5 - Exibir tarefas pendentes por categoria");
            System.out.println("6 - Exibir tarefas concluidas por categoria");
            System.out.println("7 - Exibir tarefas pendentes (Filtro)");
            System.out.println("8 - Exibir tarefas concluídas (Filtro)");
            System.out.println("9 - Adicionar Sub-Tarefa");
            System.out.println("10 - Sair");
            try {
                int opcao = scanner.nextInt();
                scanner.nextLine();
                switch (opcao) {
                    case 1 -> this.adicionarTarefa();
                    case 2 -> {
                        System.out.print("Selecione a tarefa que deseja concluir:\n");
                        this.exibirTarefasPendentes();
                        System.out.println("\n");
                        this.concluirTarefa(scanner.nextInt());
                    }
                    case 3 -> this.exibirTarefasPendentes();
                    case 4 -> this.exibirTarefasConcluidas();
                    case 5 -> {
                        System.out.print("Digite a categoria:");
                        this.exibirTarefasPendentesPorCategoria(scanner.nextLine());
                    }
                    case 6 -> {
                        System.out.print("Digite a categoria:");
                        this.exibirTarefasConcluidasPorCategoria(scanner.nextLine());
                    }
                    case 7 -> {
                        System.out.print("Digite o critério de busca:");
                        this.exibirTarefasPendentesPorFiltro(scanner.nextLine());
                    }
                    case 8 -> {
                        System.out.print("Digite o critério de busca:");
                        this.exibirTarefasConcluidasPorFiltro(scanner.nextLine());
                    }
                    case 9 -> {
                        this.exibirTarefasPendentes();
                        System.out.print("Informe o Id tarefa que deseja incluir:");
                        this.adicionarSubTarefa(scanner.nextInt());
                    }
                    case 10 -> {
                        sair = true; // Define a variável 'sair' como true para sair do loop
                        break;
                    }
                    default -> {
                        System.out.println("Op��o inv�lida.");

                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Opção inválida. Digite novamente.");
                scanner.nextLine(); // Limpa o buffer do scanner
            }
        }
    }

    public void exibirTarefasPendentes() {
        tarefasPendentes.forEach(System.out::println);
    }

    public void exibirTarefasPendentesPorCategoria(String categoria) {
        tarefasPendentes.forEach(tarefa -> {
            if (tarefa.getCategoria().equals(categoria)) {
                System.out.println(tarefa);
            }
        });
    }

    public void exibirTarefasPendentesPorFiltro(String filtro) {
        tarefasPendentes.forEach(tarefa -> {
            if (tarefa.getDescricao().contains(filtro) || tarefa.getTitulo().contains(filtro)) {
                System.out.println(tarefa);
            }
        });
    }

    public void exibirTarefasConcluidas() {
        tarefasConcluidas.forEach(System.out::println);
    }

    public void exibirTarefasConcluidasPorCategoria(String categoria) {
        tarefasConcluidas.forEach(tarefa -> {
            if (tarefa.getCategoria().equals(categoria)) {
                System.out.println(tarefa);
            }
        });
    }

    public void exibirTarefasConcluidasPorFiltro(String filtro) {
        tarefasConcluidas.forEach(tarefa -> {
            if (tarefa.getCategoria().contains(filtro) || tarefa.getTitulo().contains(filtro)) {
                System.out.println(tarefa);
            }
        });
    }

    private int getProximoId() {
        return (this.tarefasPendentes.size() + this.tarefasConcluidas.size()) + 1;
    }

    private Tarefa findTarefaPendenteById(int id) {
        for (Tarefa tarefa : this.tarefasPendentes) {
            if (tarefa.getId() == id) {
                return tarefa;
            }
        }

        return null;
    }
}
