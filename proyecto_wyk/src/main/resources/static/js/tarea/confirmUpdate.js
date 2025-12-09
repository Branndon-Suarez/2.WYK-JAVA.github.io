document.getElementById('update-tarea-form').addEventListener('submit', function (e) {
    e.preventDefault();

    const form = this;

    Swal.fire({
        title: '¿Estás seguro?',
        text: 'Se actualizarán los datos de esta tarea.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, actualizar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {

            // --- INICIO DE LA CORRECCIÓN: CONVERSIÓN A JSON ---
            const formData = new FormData(form);
            const jsonData = {};

            // Convertir FormData a un objeto JavaScript simple
            for (const [key, value] of formData.entries()) {
                // Asegurar que los campos numéricos (Long, Integer, Float) se envíen como números
                if (key === 'idUsuarioAsignado') {
                    jsonData[key] = parseInt(value, 10);
                } else if (key === 'idTarea' || key === 'tiempoEstimadoHoras') {
                    jsonData[key] = parseFloat(value);
                } else {
                    jsonData[key] = value;
                }
            }

            // Asegúrate de que las constantes CSRF_HEADER y CSRF_TOKEN estén definidas en el HTML
            const headers = {
                'Content-Type': 'application/json'
            };

            // Añadir cabeceras CSRF si están disponibles
            if (typeof CSRF_HEADER !== 'undefined' && typeof CSRF_TOKEN !== 'undefined') {
                 headers[CSRF_HEADER] = CSRF_TOKEN;
            }
            // --- FIN DE LA CORRECCIÓN ---


            // CORRECCIÓN DE LA URL: Se usa 'tareas/actualizar' y se envía JSON
            fetch(URL_UPDATE, {
                method: 'POST',
                headers: headers, // Usar las cabeceras JSON y CSRF
                body: JSON.stringify(jsonData) // Enviar el cuerpo como JSON
            })
            .then(response => {
                // Modificación: Leer el error JSON si la respuesta no es OK
                if (!response.ok) {
                    // Intenta leer el cuerpo de la respuesta para obtener un mensaje de error detallado (e.g., validación)
                    return response.json().then(errorData => {
                        throw new Error(errorData.message || 'Error desconocido del servidor (' + response.status + ').');
                    }).catch(() => {
                        // Si falla la lectura del JSON (ej: error 500), devuelve un error genérico
                        throw new Error('Error de servidor. Código de estado HTTP: ' + response.status);
                    });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Éxito!',
                        text: data.message
                    }).then(() => {
                        window.location.href = URL_REDIRECT; // Redirige usando la constante corregida
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: data.message
                    });
                }
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error de conexión',
                    text: 'No se pudo contactar al servidor: ' + error.message
                });
            });
        }
    });
});