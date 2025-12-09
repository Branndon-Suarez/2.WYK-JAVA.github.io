document.getElementById('update-compra-form').addEventListener('submit', function (e) {
    e.preventDefault();

    Swal.fire({
        title: '¿Estás seguro?',
        text: 'Se actualizarán los datos de la compra.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, actualizar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {

            const data = {
                idCompra: parseInt(document.getElementById("idCompra").value),
                nombreProveedor: document.getElementById("proveedor").value,
                marca: document.getElementById("marca").value,
                telProveedor: document.getElementById("telefono").value,
                emailProveedor: document.getElementById("email").value,
                descripcionCompra: document.getElementById("descripcion").value,
                estadoFacturaCompra: document.getElementById("estadoPago").value,
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
