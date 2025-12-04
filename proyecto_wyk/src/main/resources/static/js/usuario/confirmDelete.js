document.addEventListener('DOMContentLoaded', () => {
    const deleteButtons = document.querySelectorAll('.delete-usuario');

    deleteButtons.forEach(button => {
        button.addEventListener('click', async (event) => {

            const target = event.target.closest('.delete-usuario');
            const idUsuario = target.dataset.id;

            // Confirmación
            const result = await Swal.fire({
                title: '¿Estás seguro?',
                text: "No podrás revertir esta acción.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            });

            if (!result.isConfirmed) return;

            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded'
            };

            // 🔑 PASO 2 TOKEN: Añadir CSRF Header y Token (CORRECCIÓN)
            // Se usa typeof para verificar que la variable exista antes de usarla
            if (typeof CSRF_HEADER !== 'undefined' && typeof CSRF_TOKEN !== 'undefined') {
                headers[CSRF_HEADER] = CSRF_TOKEN;
            } else {
                console.error("ADVERTENCIA: Variables CSRF no definidas. La petición podría fallar (403 Forbidden).");
            }

            try {
                const response = await fetch('/usuarios/delete', {
                    method: 'POST',
                    headers: headers,
                    body: `id=${idUsuario}`
                });

                const data = await response.json();

                // Si está relacionado
                if (data.code === "FK_CONSTRAINT") {
                    return Swal.fire({
                        icon: 'error',
                        title: 'No se puede eliminar',
                        text: "Este usuario está siendo usado en otros registros."
                    });
                }

                // Error general
                if (!data.success) {
                    return Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: data.message
                    });
                }

                // Eliminado
                Swal.fire({
                    icon: 'success',
                    title: 'Eliminado',
                    text: data.message
                }).then(() => location.reload());

            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error de conexión',
                    text: "No se pudo contactar al servidor."
                });
            }
        });
    });
});
