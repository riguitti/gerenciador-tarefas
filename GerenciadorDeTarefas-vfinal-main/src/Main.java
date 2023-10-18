import classes.GerenciadorTarefas;

import java.io.IOException;

public class Main {
    public static boolean autenticado;

    public static void main(String[] args) throws IOException {
        System.out.println("Olá, seja bem vindo! ");

        new GerenciadorTarefas();
    }
}