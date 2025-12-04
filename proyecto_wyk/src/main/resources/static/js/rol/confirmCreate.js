document.getElementById("create-rol-form").addEventListener("submit", function(e) {
    e.preventDefault(); // Evita el envío normal

    const data = {
        rol: document.getElementById("rol").value,
        clasificacion: document.getElementById("clasificacion").value
    };

    Swal.fire({
        title: "¿Crear rol?",
        text: "Se registrará un nuevo rol en el sistema.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, crear",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {
            // 1. CONFIGURACIÓN CSRF:
            // Creamos los encabezados HTTP, incluyendo el Content-Type
            const headers = {
                "Content-Type": "application/json"
            };

            // Añadimos el encabezado CSRF usando las variables que inyectaste en el HTML
            // (CSRF_HEADER contiene el nombre del encabezado, ej: X-CSRF-TOKEN)
            // (CSRF_TOKEN contiene el valor del token real)
            headers[CSRF_HEADER] = CSRF_TOKEN;
            fetch(URL_GUARDAR, {
                method: "POST",
                headers: headers,
                body: JSON.stringify(data)
            })
            .then(response => {
                if(!response.ok) {
                    throw new Error('Error en la conexión con el servidor o token CSRF inválido.');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        title: "¡Éxito!",
                        text: data.message,
                        icon: "success"
                    }).then(() => {
                        window.location.href = URL_REDIRECT;
                    });

                } else {
                    Swal.fire({
                        title: "Error",
                        text: data.message,
                        icon: "error"
                    });
                }
            })
            .catch(err => {
                Swal.fire({
                    title: "Error",
                    text: "No se pudo conectar con el servidor.",
                    icon: "error"
                });
            });
        }
    });
});
