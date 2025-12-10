document.addEventListener('DOMContentLoaded', () => {
    const switches = document.querySelectorAll('.check-trail');
    switches.forEach(switchElement => {
        const checkbox = switchElement.previousElementSibling;

        checkbox.addEventListener('change', async (event) => {
            const productoId = event.target.dataset.id;
            const nuevoEstado = event.target.checked ? 1 : 0;

            const result = await Swal.fire({
                title: '¿Estás seguro?',
                text: `¿Quieres ${nuevoEstado === 1 ? 'activar' : 'desactivar'} este producto?`,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, ¡cambiar estado!',
                cancelButtonText: 'Cancelar'
            });

            if (result.isConfirmed) {
                const base = APP_URL.endsWith("/") ? APP_URL : APP_URL + "/";

                const url = `${base}productos/updateState`;

                const headers = {
                    'Content-Type': 'application/json'
                };

                if (typeof CSRF_HEADER !== 'undefined' && typeof CSRF_TOKEN !== 'undefined') {
                    headers[CSRF_HEADER] = CSRF_TOKEN;
                } else {
                    console.error("ADVERTENCIA: Variables CSRF no definidas. La petición podría fallar.");
                }

                try {
                    const response = await fetch(url, {
                        method: 'POST',
                        headers: headers,
                        body: JSON.stringify({
                            id: productoId,
                            estado: nuevoEstado
                        })
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de Servidor',
                            text: errorData.message || `Error: ${response.status} ${response.statusText}`
                        });
                        event.target.checked = !event.target.checked;
                    } else {
                        const data = await response.json();
                        Swal.fire({
                            icon: 'success',
                            title: '¡Actualizado!',
                            text: data.message
                        });
                    }
                } catch (error) {
                    console.error('Error en la petición fetch:', error);
                    event.target.checked = !event.target.checked;
                    Swal.fire({
                        icon: 'error',
                        title: 'Error de conexión',
                        text: 'No se pudo conectar con el servidor. Por favor, revisa la consola del navegador para más detalles.'
                    });
                }
            } else {
                event.target.checked = !event.target.checked;
            }
        });
    });
});