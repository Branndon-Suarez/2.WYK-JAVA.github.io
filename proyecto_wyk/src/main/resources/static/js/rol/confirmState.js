document.addEventListener('DOMContentLoaded', () => {
    const switches = document.querySelectorAll('.check-trail');
    switches.forEach(switchElement => {
        const checkbox = switchElement.previousElementSibling;
        
        checkbox.addEventListener('change', async (event) => {
            const rolId = event.target.dataset.id;
            const nuevoEstado = event.target.checked ? 1 : 0;

            const result = await Swal.fire({
                title: '¿Estás seguro?',
                text: `¿Quieres ${nuevoEstado === 1 ? 'activar' : 'desactivar'} este rol?`,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, ¡cambiar estado!',
                cancelButtonText: 'Cancelar'
            });

            if (result.isConfirmed) {

                // 🔥 AJUSTE IMPORTANTE:
                // Garantiza que la URL se forme correctamente aunque APP_URL no termine en "/"
                const base = APP_URL.endsWith("/") ? APP_URL : APP_URL + "/";

                const url = `${base}roles/updateState`;

                try {
                    const response = await fetch(url, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            id: rolId,
                            estado: nuevoEstado
                        })
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        Swal.fire({
                            icon: 'error',
                            title: 'Error de Servidor',
                            text: errorData.error || `Error: ${response.status} ${response.statusText}`
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
                        text: 'No se pudo conectar con el servidor.'
                    });
                }
            } else {
                event.target.checked = !event.target.checked;
            }
        });
    });
});
