document.getElementById("create-producto-form").addEventListener("submit", function(e) {
    e.preventDefault(); // Evita el envío normal

    // 1. Obtener datos del formulario
    const data = {
        idProducto: document.getElementById("idProducto").value,
        nombreProducto: document.getElementById("nombreProducto").value,
        valorUnitarioProducto: document.getElementById("valorUnitarioProducto").value,
        cantExistProducto: document.getElementById("cantExistProducto").value,
        // La fecha debe enviarse como String en formato YYYY-MM-DD
        fechaVencimientoProducto: document.getElementById("fechaVencimientoProducto").value,
        tipoProducto: document.getElementById("tipoProducto").value
    };

    Swal.fire({
        title: "¿Crear producto?",
        text: "Se registrará un nuevo producto en el inventario.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, crear",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {

            // 2. Preparar headers (incluyendo CSRF)
            const headers = {
                "Content-Type": "application/json"
            };
            headers[CSRF_HEADER] = CSRF_TOKEN;

            // 3. Enviar solicitud AJAX
            fetch(URL_GUARDAR, {
                method: "POST",
                headers: headers,
                body: JSON.stringify(data)
            })
            .then(response => {
                if(!response.ok) {
                    // Si el servidor responde con un status 4xx o 5xx
                    throw new Error('Error en la conexión con el servidor: HTTP status ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                // 4. Manejar la respuesta del controlador
                if (data.success) {
                    Swal.fire({
                        title: "¡Éxito!",
                        text: data.message,
                        icon: "success"
                    }).then(() => {
                        window.location.href = URL_REDIRECT; // Redirige al dashboard de productos
                    });

                } else {
                    Swal.fire({
                        title: "Error de Validación",
                        text: data.message,
                        icon: "error"
                    });
                }
            })
            .catch(err => {
                // 5. Manejar errores de red o errores de conexión
                console.error(err);
                Swal.fire({
                    title: "Error",
                    text: "No se pudo conectar con el servidor o hubo un error inesperado.",
                    icon: "error"
                });
            });
        }
    });
});