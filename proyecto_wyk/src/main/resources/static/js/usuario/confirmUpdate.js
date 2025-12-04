document.getElementById('update-usuario-form').addEventListener('submit', function (e) {
    e.preventDefault();

    Swal.fire({
        title: '¿Estás seguro?',
        text: 'Se actualizarán los datos del usuario.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, actualizar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {

            const data = {
                idUsuario: document.getElementById("idUsuario").value,
                numDoc: document.getElementById("numDoc").value,
                nombre: document.getElementById("nombre").value,
                passwordUsuario: document.getElementById("passwordUsuario").value,
                telUsuario: document.getElementById("telUsuario").value,
                emailUsuario: document.getElementById("emailUsuario").value,
                rolId: parseInt(document.getElementById("rol_fk").value),
                estadoUsuario: document.getElementById("estadoUsuario").value === "true"
            };

            const headers = {
                "Content-Type": "application/json"
            };

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
