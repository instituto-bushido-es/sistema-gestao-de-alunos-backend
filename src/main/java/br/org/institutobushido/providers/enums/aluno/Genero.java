package br.org.institutobushido.providers.enums.aluno;

public enum Genero {
    M("Masculino"),
    F("Feminino"),
    OUTRO("Outro");

    private final String descricao;

    Genero(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static Genero fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Genero.valueOf(value.toUpperCase());
    }
}
