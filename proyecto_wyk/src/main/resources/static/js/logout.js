document.addEventListener('DOMContentLoaded', () => {
    const logoutLink = document.getElementById('logout-link');

    if (logoutLink) {
        logoutLink.addEventListener('click', async (e) => {
            e.preventDefault();

            const result = await Swal.fire({
                title: '¿Cerrar Sesión?',
                text: "¿Estás seguro de que quieres cerrar la sesión actual?",
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, Cerrar Sesión',
                cancelButtonText: 'Cancelar'
            });

            if (result.isConfirmed) {
                const logoutUrl = '/logout';

                // Construir Headers con CSRF
                const headers = {};
                // Asegurarse de que CSRF_HEADER y CSRF_TOKEN están definidos (inyectados por Thymeleaf)
                if (typeof CSRF_HEADER !== 'undefined' && typeof CSRF_TOKEN !== 'undefined') {
                    headers[CSRF_HEADER] = CSRF_TOKEN;
                } else {
                    console.error("CSRF variables no encontradas. El logout fallará.");
                }

                try {
                    // La petición POST es necesaria para cerrar la sesión en el servidor
                    const response = await fetch(logoutUrl, {
                        method: 'POST',
                        headers: headers
                        // No necesita body, Spring solo necesita el token en el header.
                    });

                    // 3. Redireccionar al login si la petición es exitosa
                    // Spring Security automáticamente redirige al login después de un logout exitoso,
                    // pero es una buena práctica forzar la redirección si la respuesta es ok.
                    if (response.ok || response.status === 403) { // 403 puede ocurrir si Spring ya cerró sesión
                        window.location.href = '/login';
                    } else {
                        throw new Error('Falló el cierre de sesión en el servidor.');
                    }
                } catch (error) {
                    Swal.fire('Error', 'No se pudo contactar al servidor para cerrar la sesión.', 'error');
                }
            }
        });
    }
});