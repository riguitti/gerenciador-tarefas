package classes;

import java.util.ArrayList;
import java.util.List;

public class Tarefa {

    private int id;
    private String titulo;
    private String descricao;
    private String dtCriacao;
    private String dtConclusao;
    private boolean concluida;
    private String categoria;
    private List<SubTarefa> subTarefas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDtCriacao(String dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public void setDtConclusao(String dtConclusao) {
        this.dtConclusao = dtConclusao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<SubTarefa> getSubTarefas() {
        if (subTarefas == null) {
            this.subTarefas = new ArrayList<>();
        }

        return subTarefas;
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dtCriacao='" + dtCriacao + '\'' +
                ", dtConclusao='" + dtConclusao + '\'' +
                ", concluida=" + concluida +
                ", categoria='" + categoria + '\'' +
                ", subTarefas=" + subTarefas +
                '}';
    }
}
