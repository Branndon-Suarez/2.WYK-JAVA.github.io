document.addEventListener('DOMContentLoaded', () => {
    const deleteButtons = document.querySelectorAll('.delete-producto');

    deleteButtons.forEach(button => {
        button.addEventListener('click', async (event) => {

            const target = event.target.closest('.delete-producto');
            const idProducto = target.dataset.id;

            // Confirmación
            const result = await Swal.fire({
                title: '¿Estás seguro?',
                text: "Se eliminará este producto y no podrás revertir esta acción.",
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

            if (typeof CSRF_HEADER !== 'undefined' && typeof CSRF_TOKEN !== 'undefined') {
                headers[CSRF_HEADER] = CSRF_TOKEN;
            } else {
                console.error("ADVERTENCIA: Variables CSRF no definidas. La petición podría fallar (403 Forbidden).");
            }

            try {
                const response = await fetch('/productos/delete', {
                    method: 'POST',
                    headers: headers,
                    body: `id=${idProducto}`
                });

                const data = await response.json();

                // Si está relacionado
                if (data.code === "FK_CONSTRAINT") {
                    return Swal.fire({
                        icon: 'error',
                        title: 'No se puede eliminar',
                        text: "Este producto está siendo usado en otros registros (ej. ventas o inventario)."
                    });
                }

                if (!data.success) {
                    return Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: data.message
                    });
                }

                Swal.fire({
                    icon: 'success',
                    title: 'Eliminado',
                    text: "Producto eliminado correctamente."
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