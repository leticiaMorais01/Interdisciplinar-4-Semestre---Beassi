
document.addEventListener("DOMContentLoaded", () => {
    const botao = document.getElementById("btnStatusCliente");
    const situacao = document.querySelector(".perfil-status");

    if (!botao || !situacao) return;

    botao.addEventListener("click", () => {
        const estaAtivo = botao.dataset.status === "ativo";

        const mensagem = estaAtivo
            ? "Tem certeza de que deseja desativar este cliente? Os projetos e feedbacks serão preservados."
            : "Deseja reativar este cadastro?";

        if (!confirm(mensagem)) return;

        if (estaAtivo) {
            botao.dataset.status = "inativo";
            botao.textContent = "Reativar cadastro";
            botao.classList.add("reativar");

            situacao.textContent = "Cadastro inativo";
            situacao.classList.add("inativo");
        } else {
            botao.dataset.status = "ativo";
            botao.textContent = "Desativar cadastro";
            botao.classList.remove("reativar");

            situacao.textContent = "Cadastro ativo";
            situacao.classList.remove("inativo");
        }
    });
});
