document.getElementById('update-venta-form').addEventListener('submit', function (e) {
    e.preventDefault();

    Swal.fire({
        title: '¿Estás seguro?',
        text: 'Se actualizarán los datos de la venta.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, actualizar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {

            const data = {
                idVenta: parseInt(document.getElementById("idVenta").value),
                numeroMesa: document.getElementById("numMesa").value ? parseInt(document.getElementById("numMesa").value) : null,
                descripcion: document.getElementById("descripcion").value,
                estadoPedido: document.getElementById("estadoPedido").value,
                estadoPago: document.getElementById("estadoPago").value,
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
