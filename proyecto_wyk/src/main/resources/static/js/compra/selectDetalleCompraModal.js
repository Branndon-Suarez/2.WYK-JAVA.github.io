document.addEventListener('DOMContentLoaded', function () {
    const detalleCompraModal = document.getElementById('detalleCompraModal');
    const detalleCompraBody = document.getElementById('detalleCompraBody');
    const compraTotalDisplay = document.getElementById('compraTotalDisplay');

    detalleCompraModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const compraId = button.getAttribute('data-id-compra');

        detalleCompraBody.innerHTML = '<tr><td colspan="5" class="text-center">Cargando detalles...</td></tr>';
        compraTotalDisplay.textContent = '...';

        fetch(`${APP_URL}compras/listarDetallesCompraModal?id=${compraId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al obtener el detalle de la compra.');
                }
                return response.json();
            })
            .then(data => {
                if (data.success && data.detalle.length > 0) {
                    detalleCompraBody.innerHTML = '';
                    let totalGeneral = 0;

                    data.detalle.forEach(item => {
                        // Formatear a moneda (usando el campo de precio de tu DTO)
                        const precioUnitarioFormateado = new Intl.NumberFormat('es-CO', {
                            style: 'currency',
                            currency: 'COP',
                            minimumFractionDigits: 0
                        }).format(item.PRECIO_UNITARIO);

                        const subTotalFormateado = new Intl.NumberFormat('es-CO', {
                            style: 'currency',
                            currency: 'COP',
                            minimumFractionDigits: 0
                        }).format(item.SUB_TOTAL);

                        // Mapear los campos del DTO a las columnas del Modal HTML (5 columnas)
                        detalleCompraBody.innerHTML += `
                                <tr>
                                    <td>${item.TIPO || 'N/A'}</td>
                                    <td>${item.ITEM_NOMBRE}</td>
                                    <td>${item.CANTIDAD}</td>
                                    <td>${precioUnitarioFormateado}</td>
                                    <td>${subTotalFormateado}</td>
                                </tr>
                            `;
                        totalGeneral += item.SUB_TOTAL;
                    });

                    // Muestra el total general de la compra
                    compraTotalDisplay.textContent = new Intl.NumberFormat('es-CO', {
                        style: 'currency',
                        currency: 'COP',
                        minimumFractionDigits: 0
                    }).format(totalGeneral);

                } else {
                    detalleCompraBody.innerHTML = '<tr><td colspan="5" class="text-center">No se encontraron detalles para esta compra.</td></tr>';
                    compraTotalDisplay.textContent = 'N/A';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                detalleCompraBody.innerHTML = '<tr><td colspan="5" class="text-center">Error al cargar los datos.</td></tr>';
                compraTotalDisplay.textContent = 'Error';
            });
    });
});