document.getElementById("create-tarea-form").addEventListener("submit", function(e) {
    e.preventDefault(); // Evita el envío normal

    const data = {
        tarea: document.getElementById("tarea")
        numDoc: document.getElementById("numDoc").value,
        nombre: document.getElementById("nombre").value,
        passwordUsuario: document.getElementById("password").value,
        telUsuario: document.getElementById("telefono").value,
        emailUsuario: document.getElementById("email").value,
        rolId: document.getElementById("rol_fk").value
    };

    Swal.fire({
        title: "¿Crear tarea?",
        text: "Se registrará una nueva tarera dentro del sistema.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, crear",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {
            const headers = {
                "Content-Type": "application/json"
            };
            headers[CSRF_HEADER] = CSRF_TOKEN;

            fetch(URL_GUARDAR, {
                method: "POST",
                headers: headers,
                body: JSON.stringify(data)
            })
            .then(response => {
                if(!response.ok) {
                    throw new Error('Error en la conexión con el servidor');
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
