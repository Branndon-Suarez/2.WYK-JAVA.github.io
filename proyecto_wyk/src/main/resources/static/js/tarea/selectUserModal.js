// /js/tarea/selectUserModal.js

const tablaUsuariosModalBody = document.querySelector('#tablaUsuariosModal tbody');
const modalUsuarios = document.getElementById('modalUsuarios');
let usuariosData = [];

// Escucha cuando el modal se muestra
if (modalUsuarios) {
    modalUsuarios.addEventListener('show.bs.modal', async () => {
        // Cargar los usuarios si aún no se han cargado (o recargar si quieres datos frescos)
        if (usuariosData.length === 0) {
            await cargarUsuarios();
        }
    });
}

// Cargar usuarios desde el servidor
async function cargarUsuarios() {
    // Limpiar la tabla y mostrar cargando
    tablaUsuariosModalBody.innerHTML = `<tr><td colspan="4">Cargando usuarios...</td></tr>`;
    usuariosData = []; // Resetear datos

    try {
        // Usar el endpoint que creaste en UsuarioController
        const url = APP_URL + 'usuarios/listarUsuariosModal';
        const res = await fetch(url, { cache: "no-store" });

        if (!res.ok) throw new Error('Error al cargar los usuarios');

        const data = await res.json();

        if (data.success) {
            usuariosData = data.data;
            renderizarUsuarios();
        } else {
            console.error("Error del servidor:", data.message);
            tablaUsuariosModalBody.innerHTML = `<tr><td colspan="4">${data.message}</td></tr>`;
        }
    } catch (err) {
        console.error("Error de red:", err);
        tablaUsuariosModalBody.innerHTML = `<tr><td colspan="4">Error al cargar usuarios.</td></tr>`;
    }
}

// Renderizar usuarios en la tabla del modal
function renderizarUsuarios() {
    tablaUsuariosModalBody.innerHTML = ''; // Limpiar la tabla

    if (usuariosData.length === 0) {
        tablaUsuariosModalBody.innerHTML = `<tr><td colspan="4">No hay usuarios disponibles.</td></tr>`;
        return;
    }

    usuariosData.forEach(usuario => {
        // Asumiendo que tu entidad Usuario tiene campos: numDoc, nombre, y un objeto Rol dentro.
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${usuario.numDoc}</td>
            <td>${usuario.nombre}</td>
            <td>${usuario.rol.rol}</td>
            <td>
                <button type="button" class="btn btn-success btn-sm btn-select-usuario"
                        data-id="${usuario.idUsuario}"
                        data-nombre="${usuario.nombre}"
                        data-bs-dismiss="modal">Seleccionar
                </button>
            </td>
        `;
        tablaUsuariosModalBody.appendChild(tr);
    });

    // Añadir event listeners a los botones de selección
    document.querySelectorAll('.btn-select-usuario').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const id = e.target.dataset.id;
            const nombre = e.target.dataset.nombre;

            // --- CORRECCIÓN APLICADA AQUÍ ---
            // Asignar el ID al campo oculto del formulario
            document.getElementById('idUsuarioAsignado').value = id;
            // Asignar el nombre al campo de visualización del formulario
            document.getElementById('usuario_display').value = nombre;
            // ----------------------------------
        });
    });
}