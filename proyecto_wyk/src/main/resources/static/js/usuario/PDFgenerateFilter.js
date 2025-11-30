document.addEventListener('DOMContentLoaded', function () {
    const generatePdfBtn = document.getElementById('generatePdfBtnUsuario');

    if (!generatePdfBtn) return;

    generatePdfBtn.addEventListener('click', () => {

        // 1. BÚSQUEDA
        const searchTextPrincipal = document.getElementById('buscarRapido')?.value || "";
        const searchTextModal = document.getElementById('buscarRapidoModal')?.value || "";
        const finalSearchText = searchTextPrincipal || searchTextModal;

        // 2. FILTRO ESTADO
        let estadoFilter = null;
        const btnEstado = document.querySelector("#botonesColumnas button[data-estado]");
        if (btnEstado) estadoFilter = btnEstado.dataset.estado;

        // 3. CHIPS DE FILTROS
        const chipsActivos = document.querySelectorAll(".chip.bg-primary");
        const chipFilters = {};

        const normalizeString = (str) =>
            str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");

        chipsActivos.forEach(chip => {
            const accordionBody = chip.closest(".accordion-body");
            if (!accordionBody) return;

            const colElement = accordionBody.querySelector(".filtro-columna");
            if (!colElement) return;

            const colIndex = colElement.dataset.col;

            const th = document.querySelector(`#tablaUsuarios thead th:nth-child(${parseInt(colIndex, 10) + 1})`);
            if (!th) return;

            const columnaNombre = normalizeString(th.textContent.trim());

            if (!chipFilters[columnaNombre]) {
                chipFilters[columnaNombre] = [];
            }

            chipFilters[columnaNombre].push(chip.textContent.trim());
        });

        // 4. CONSTRUIR QUERY
        let params = [];

        if (finalSearchText) params.push(`search=${encodeURIComponent(finalSearchText)}`);
        if (estadoFilter && estadoFilter !== "todos") params.push(`estado=${estadoFilter}`);

        for (const columna in chipFilters) {
            const paramName = `filtro_${columna.replace(/\s+/g, '_').toUpperCase()}`;
            const paramValue = chipFilters[columna].map(v => encodeURIComponent(v)).join(',');
            params.push(`${paramName}=${paramValue}`);
        }

        const queryString = params.length ? "?" + params.join("&") : "";

        // 5. ABRIR PDF
        window.open(`/usuarios/generateReportPDF${queryString}`, "_blank");
    });
});
