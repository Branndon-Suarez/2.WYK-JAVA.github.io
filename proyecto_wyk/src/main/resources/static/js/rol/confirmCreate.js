document.getElementById("create-rol-form").addEventListener("submit", function(e) {
    e.preventDefault(); // Evita el envío normal

    const form = this;
    const formData = new FormData(form);

    Swal.fire({
        title: "¿Crear rol?",
        text: "Se registrará un nuevo rol en el sistema.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, crear",
        cancelButtonText: "Cancelar"
    }).then((result) => {

        if (result.isConfirmed) {

            fetch(URL_GUARDAR, {
                method: "POST",
                body: formData
            })
            .then(res => res.json())
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
