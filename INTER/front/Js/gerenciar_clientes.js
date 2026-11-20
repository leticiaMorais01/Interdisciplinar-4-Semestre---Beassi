
document.addEventListener("DOMContentLoaded", () => {
    const busca = document.getElementById("buscarCliente");
    const filtro = document.getElementById("filtroProjetos");
    const tabela = document.getElementById("tabelaClientes");
    const vazio = document.getElementById("clientesVazio");

    if (!busca || !filtro || !tabela || !vazio) return;

    function filtrar() {
        // Busca as linhas novamente a cada filtragem
        const linhas = [...tabela.querySelectorAll("tr")];

        const termo = busca.value.trim().toLocaleLowerCase("pt-BR");
        const tipo = filtro.value;

        let visiveis = 0;

        linhas.forEach(linha => {
            // Ignora linhas de carregamento ou mensagens
            if (!linha.dataset.projetos) {
                linha.hidden = true;
                return;
            }

            const texto = linha.textContent.toLocaleLowerCase("pt-BR");
            const quantidade = Number(linha.dataset.projetos);

            const correspondeTexto = texto.includes(termo);

            const correspondeTipo =
                tipo === "todos" ||
                (tipo === "com" && quantidade > 0) ||
                (tipo === "sem" && quantidade === 0);

            const mostrar = correspondeTexto && correspondeTipo;

            linha.hidden = !mostrar;

            if (mostrar) visiveis++;
        });

        vazio.hidden = visiveis > 0;
    }

    busca.addEventListener("input", filtrar);
    filtro.addEventListener("change", filtrar);

    // Reaplica os filtros quando a tabela recebe novos clientes
    const observador = new MutationObserver(() => {
        filtrar();
    });

    observador.observe(tabela, {
        childList: true,
        subtree: true
    });
});
