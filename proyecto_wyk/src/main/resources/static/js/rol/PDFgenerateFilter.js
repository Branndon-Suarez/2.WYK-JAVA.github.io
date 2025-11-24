document.addEventListener('DOMContentLoaded', function () {
    const generatePdfBtn = document.getElementById('generatePdfBtn');

    if (!generatePdfBtn) return;

    generatePdfBtn.addEventListener('click', () => {

        // =====================================================
        // 1️⃣  BUSCAR TEXTO (principal o modal)
        // =====================================================
        const searchTextPrincipal = document.getElementById('buscarRapido')?.value || "";
        const searchTextModal = document.getElementById('buscarRapidoModal')?.value || "";
        const finalSearchText = searchTextPrincipal || searchTextModal;

        // =====================================================
        // 2️⃣  FILTRO DE ESTADO (botones dinámicos)
        // =====================================================
        let estadoFilter = null;
        const btnEstado = document.querySelector("#botonesColumnas button[data-estado]");
        if (btnEstado) {
            estadoFilter = btnEstado.dataset.estado;
        }

        // =====================================================
        // 3️⃣  FILTROS AVANZADOS (chips activos en acordeones)
        // =====================================================
        const chipsActivos = document.querySelectorAll(".chip.bg-primary");
        const chipFilters = {};

        // Normalizar tildes y caracteres especiales
        const normalizeString = (str) => {
            return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        };

        chipsActivos.forEach(chip => {

            const accordionBody = chip.closest(".accordion-body");
            if (!accordionBody) return;

            const colElement = accordionBody.querySelector(".filtro-columna");
            if (!colElement) return;

            const colIndex = colElement.dataset.col;

            // Tomar el nombre real de la columna desde la tabla
            const th = document.querySelector(`#tablaRoles thead th:nth-child(${parseInt(colIndex, 10) + 1})`);
            if (!th) return;

            const columnaNombre = th.textContent.trim();
            const normalizedColumnaNombre = normalizeString(columnaNombre);

            // Crear array si no existe
            if (!chipFilters[normalizedColumnaNombre]) {
                chipFilters[normalizedColumnaNombre] = [];
            }

            chipFilters[normalizedColumnaNombre].push(chip.textContent.trim());
        });

        // =====================================================
        // 4️⃣  Construir la query final
        // =====================================================

        let params = [];

        if (finalSearchText) {
            params.push(`search=${encodeURIComponent(finalSearchText)}`);
        }

        if (estadoFilter && estadoFilter !== "todos") {
            params.push(`estado=${estadoFilter}`);
        }

        // Agregar cada columna filtrada
        for (const columna in chipFilters) {
            const paramName = `filtro_${columna.replace(/\s+/g, '_').toUpperCase()}`;
            const paramValue = chipFilters[columna].map(v => encodeURIComponent(v)).join(',');
            params.push(`${paramName}=${paramValue}`);
        }

        // Unir parámetros
        const queryString = params.length > 0 ? "?" + params.join("&") : "";

        // =====================================================
        // 5️⃣  ABRIR PDF (compatible con Spring Boot)
        // =====================================================

        window.open(`/roles/generateReportPDF${queryString}`, "_blank");
    });
});
