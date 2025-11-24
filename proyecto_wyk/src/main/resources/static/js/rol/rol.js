document.addEventListener('DOMContentLoaded', function () {

    const formulario = document.getElementById('update-rol-form');
    const botonEnviar = formulario.querySelector('button[type="submit"]');

    // 🔵 SELECT DE ESTADO (ADAPTADO A TU NUEVO ID)
    const selectEstado = document.getElementById('estadoRol');

    if (selectEstado) {
        function actualizarColorEstado() {
            if (selectEstado.value === "true") {
                selectEstado.style.color = '#22c55e'; // Verde Activo
            } else {
                selectEstado.style.color = '#ef4444'; // Rojo Inactivo
            }
        }

        // Aplicar color inicial
        actualizarColorEstado();

        // Detectar cambios
        selectEstado.addEventListener('change', actualizarColorEstado);
    }
});
