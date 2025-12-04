document.getElementById('update-rol-form').addEventListener('submit', function (e) {
    e.preventDefault();

    Swal.fire({
        title: '¿Estás seguro?',
        text: 'Se actualizarán los datos del rol.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, actualizar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {

            const data = {
                idRol: document.getElementById("idRol").value,
                rol: document.getElementById("rol").value,
                clasificacion: document.getElementById("clasificacion").value,
                estadoRol: document.getElementById("estadoRol").value === "true"
            };

            const headers = {
                "Content-Type": "application/json"
            };

            // Añadir el token CSRF a los headers
            headers[CSRF_HEADER] = CSRF_TOKEN;

            fetch(URL_UPDATE, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(data)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la conexión con el servidor');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Actualizado!',
                        text: data.message
                    }).then(() => {
                        window.location.href = URL_REDIRECT;
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
                    text: error.message
                });
            });
        }
    });
});
