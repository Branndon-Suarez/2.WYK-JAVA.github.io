document.addEventListener('DOMContentLoaded', function () {
    const modalElement = document.getElementById('detalleProduccionModal');
    const tableBody = document.getElementById('detalleProduccionBody');
    const loteDisplay = document.getElementById('loteNombreDisplay');

    if (modalElement) {
        modalElement.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id-produccion');

            const fila = button.closest('tr');
            const nombreLote = fila ? fila.querySelector('td:nth-child(2)').textContent : "N/A";
            loteDisplay.textContent = nombreLote;

            tableBody.innerHTML = '<tr><td colspan="3" class="text-center">Cargando...</td></tr>';

            fetch(`${window.APP_URL}produccion/listarDetallesProduccionModal?id=${id}`)
                .then(response => response.json())
                .then(data => {
                    console.log("Datos del detalle recibidos:", data); // Esto te dirá los nombres reales

                    if (data.success && data.detalle && data.detalle.length > 0) {
                        tableBody.innerHTML = '';
                        data.detalle.forEach(item => {
                            // Soporte para ambos formatos (Mayúsculas del controlador o minúsculas)
                            const nombre = item.NOMBRE_MATERIA_PRIMA || item.nombreMateriaPrima || "Sin nombre";
                            const cantidad = item.CANTIDAD_REQUERIDA || item.cantidadRequerida || 0;
                            const unidad = item.PRESENTACION || item.presentacion || "N/A";

                            tableBody.innerHTML += `
                                <tr>
                                    <td>${nombre}</td>
                                    <td><strong>${cantidad}</strong></td>
                                    <td>${unidad}</td>
                                </tr>
                            `;
                        });
                    } else {
                        tableBody.innerHTML = '<tr><td colspan="3" class="text-center">No hay insumos registrados para esta producción</td></tr>';
                    }
                })
                .catch(error => {
                    console.error('Error en fetch detalle:', error);
                    tableBody.innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error al conectar con el servidor</td></tr>';
                });
        });
    }
});