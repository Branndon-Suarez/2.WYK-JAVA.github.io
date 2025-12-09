document.addEventListener('DOMContentLoaded', () => {
    // Selecciona todos los botones con la clase para eliminar tarea
    const deleteButtons = document.querySelectorAll('.delete-tarea');

    deleteButtons.forEach(button => {
        button.addEventListener('click', async (event) => {
            // Asegura que el target sea el botón más cercano que contenga el ID
            const targetButton = event.target.closest('.delete-tarea');
            const tareaId = targetButton.dataset.id; // Obtiene el ID de la tarea

            // Muestra la alerta de confirmación
            const result = await Swal.fire({
                title: '¿Estás seguro?',
                text: "¡No podrás revertir esta acción!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, ¡eliminar!',
                cancelButtonText: 'Cancelar'
            });

            if (result.isConfirmed) {
                // La URL de la API de eliminación
                const url = `${APP_URL}tareas/delete`;

                // --- INICIO DE LA CORRECCIÓN CLAVE ---
                // Se usa URLSearchParams para enviar el 'id' en el cuerpo
                // como si fuera un formulario. Esto se mapea a @RequestParam en Spring.
                const formData = new URLSearchParams();
                formData.append('id', tareaId);

                // Si estás usando CSRF, asegúrate de que las constantes
                // CSRF_HEADER y CSRF_TOKEN estén definidas en el HTML.
                const headers = {};
                if (typeof CSRF_HEADER !== 'undefined' && typeof CSRF_TOKEN !== 'undefined') {
                    headers[CSRF_HEADER] = CSRF_TOKEN;
                }

                try {
                    const response = await fetch(url, {
                        method: 'POST',
                        headers: headers, // Añade solo las cabeceras necesarias (ej. CSRF)
                        body: formData // Envía el ID como parámetro de formulario
                    });
                // --- FIN DE LA CORRECCIÓN CLAVE ---

                    const data = await response.json();

                    // Verifica el estado de la respuesta y el éxito en el cuerpo
                    if (!response.ok || !data.success) {

                        let errorMessage = 'No se pudo eliminar la tarea.';

                        // Muestra el mensaje de error de Spring (especialmente si es FK_CONSTRAINT)
                        if (data.code === 'FK_CONSTRAINT') {
                             errorMessage = data.message;
                        } else if (data.message) {
                             errorMessage = data.message;
                        } else {
                             errorMessage = 'Error desconocido al procesar la solicitud.';
                        }

                        Swal.fire('Error', errorMessage, 'error');

                    } else {
                        // Éxito
                        Swal.fire('¡Eliminado!', data.message, 'success')
                            .then(() => {
                                // Recarga la página para ver la lista actualizada
                                window.location.reload();
                            });
                    }
                } catch (error) {
                    console.error('Error:', error);
                    Swal.fire('Error de Conexión', 'No se pudo conectar con el servidor: ' + error.message, 'error');
                }
            }
        });
    });
});