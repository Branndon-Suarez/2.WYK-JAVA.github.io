let productosSeleccionados = [];
let productosDisponibles = [];

const tablaProductos = document.getElementById("tablaProductos");
const totalGeneralEl = document.getElementById("totalGeneral");

const modal = document.getElementById("modalProductos");
const listaProductos = document.getElementById("listaProductos");
const buscarProducto = document.getElementById("buscarProducto");
const btnAddProducto = document.getElementById("btnAddProducto");
const btnCerrarModal = document.getElementById("btnCerrarModal");
const btnGuardarPedido = document.getElementById("btnGuardarPedido");

const fechaVentaInput = document.getElementById("fechaVenta");

// ------------------ INICIALIZACIÓN ------------------
document.addEventListener("DOMContentLoaded", () => {
    // Establecer la fecha y hora actual (formato: YYYY-MM-DDTmm:ss)
    if (!fechaVentaInput.value) {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        fechaVentaInput.value = `${year}-${month}-${day}T${hours}:${minutes}`;
    }
});

// ------------------ TABLA (productos seleccionados) ------------------

function actualizarTabla() {
    tablaProductos.innerHTML = "";
    let total = 0;

    productosSeleccionados.forEach((p, i) => {
        // Asegurar que las variables sean números antes de calcular
        const cantidad = Number(p.cantidad) || 0;
        const precio = Number(p.precio) || 0;
        const subtotal = cantidad * precio;
        total += subtotal;

        const tr = document.createElement("tr");

        const tdNombre = document.createElement("td");
        tdNombre.textContent = p.nombre;

        const tdCantidad = document.createElement("td");
        const inputCantidad = document.createElement("input");
        inputCantidad.type = "number";
        inputCantidad.min = "1";
        inputCantidad.value = cantidad;
        inputCantidad.className = "input-cantidad";

        // Obtener la existencia máxima del producto
        const productoExistente = productosDisponibles.find(prod => String(prod.id) === String(p.id));
        const maxExistencia = productoExistente ? Number(productoExistente.existencia) : 9999;

        inputCantidad.addEventListener("change", (e) => {
            let v = parseInt(e.target.value, 10);
            if (isNaN(v) || v < 1) v = 1;

            if (v > maxExistencia) {
                // Mensaje de alerta
                Swal.fire({
                    icon: 'warning',
                    title: 'Stock Insuficiente',
                    text: `La cantidad máxima disponible es ${maxExistencia}.`,
                    confirmButtonColor: '#3085d6',
                    confirmButtonText: 'Aceptar'
                });
                v = maxExistencia;
                inputCantidad.value = v;
            }

            productosSeleccionados[i].cantidad = v;
            actualizarTabla();
        });
        tdCantidad.appendChild(inputCantidad);

        const tdPrecio = document.createElement("td");
        tdPrecio.textContent = `$${precio.toFixed(0)}`;

        const tdSubtotal = document.createElement("td");
        tdSubtotal.textContent = `$${subtotal.toFixed(0)}`;

        const tdAcciones = document.createElement("td");
        const btnEliminar = document.createElement("button");
        btnEliminar.className = "btn-rojo";
        btnEliminar.type = "button";
        btnEliminar.innerHTML = `
            <lord-icon
                src="https://cdn.lordicon.com/hfacemai.json"
                trigger="hover"
                stroke="light"
                colors="primary:#121331,secondary:#c71f16,tertiary:#ebe6ef"
                style="width:30px;height:30px">
            </lord-icon>`;

        btnEliminar.addEventListener("click", () => {
            Swal.fire({
                title: '¿Estás seguro?',
                text: "¡El producto se eliminará de la lista de productos seleccionados!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    productosSeleccionados.splice(i, 1);
                    actualizarTabla();
                    Swal.fire('¡Removido!', 'El producto ha sido removido.', 'success');
                }
            });
        });
        tdAcciones.appendChild(btnEliminar);

        tr.appendChild(tdNombre);
        tr.appendChild(tdCantidad);
        tr.appendChild(tdPrecio);
        tr.appendChild(tdSubtotal);
        tr.appendChild(tdAcciones);

        tablaProductos.appendChild(tr);
    });

    totalGeneralEl.textContent = total.toFixed(0);
}

// ------------------ MODAL DE PRODUCTOS ------------------
btnAddProducto.addEventListener("click", () => abrirModal());
btnCerrarModal.addEventListener("click", () => cerrarModal());

function abrirModal() {
    modal.classList.remove("hidden");
    // Siempre intenta recargar productos para tener el stock actualizado
    cargarProductos();
}

function cerrarModal() {
    modal.classList.add("hidden");
}

// Renderiza la lista de productos en el modal
function renderizarProductos(filtro = "") {
    listaProductos.innerHTML = "";
    const filtroLow = (filtro || "").toLowerCase();

    const filtrados = productosDisponibles.filter(p => p.nombre.toLowerCase().includes(filtroLow));
    if (filtrados.length === 0) {
        listaProductos.innerHTML = `<tr><td colspan="4">No hay productos disponibles</td></tr>`;
        return;
    }

    filtrados.forEach(p => {
        const tr = document.createElement("tr");

        if (p.existencia <= 5 && p.existencia > 0) {
            tr.classList.add("low-stock"); // Estilo CSS para stock bajo
        } else if (p.existencia === 0) {
            tr.classList.add("no-stock"); // Estilo CSS para sin stock
        }

        const tdNombre = document.createElement("td");
        tdNombre.textContent = p.nombre;

        const tdPrecio = document.createElement("td");
        tdPrecio.textContent = `$${Number(p.precio).toFixed(0)}`;

        const tdExistencia = document.createElement("td");
        tdExistencia.textContent = p.existencia;

        const tdAccion = document.createElement("td");
        const btn = document.createElement("button");
        btn.className = "btn-verde";
        btn.innerHTML = `
            <lord-icon
                src="https://cdn.lordicon.com/ueoydrft.json"
                trigger="hover"
                stroke="light"
                style="width:30px;height:30px">
            </lord-icon>`;
        btn.type = "button";

        btn.addEventListener("click", () => {
            if (p.existencia <= 0) {
                Swal.fire({
                    icon: 'error',
                    title: 'Sin Stock',
                    text: 'Este producto no tiene existencias disponibles.',
                    confirmButtonColor: '#3085d6',
                    confirmButtonText: 'Aceptar'
                });
            } else {
                agregarProducto(p);
            }
        });
        tdAccion.appendChild(btn);

        tr.appendChild(tdNombre);
        tr.appendChild(tdPrecio);
        tr.appendChild(tdExistencia);
        tr.appendChild(tdAccion);

        listaProductos.appendChild(tr);
    });
}

// búsqueda en tiempo real
buscarProducto && buscarProducto.addEventListener("input", (e) => {
    renderizarProductos(e.target.value);
});

// ------------------ CARGAR PRODUCTOS DESDE SPRING BOOT ------------------

async function cargarProductos() {
    listaProductos.innerHTML = `<tr><td colspan="4">Cargando productos...</td></tr>`;
    try {
        // Llama al nuevo endpoint del ProductoController
        const url = APP_URL + 'productos/listar';

        const res = await fetch(url, { cache: "no-store" });
        if (!res.ok) throw new Error('Error en la respuesta del servidor');
        const data = await res.json();

        // Mapeo adaptado a las entidades que devuelve Spring Boot
        productosDisponibles = (Array.isArray(data) ? data : []).map(item => ({
            // Mapeo a camelCase/nombres de entidades de Java
            id: item.idProducto,
            nombre: item.nombreProducto,
            precio: Number(item.valorUnitarioProducto),
            existencia: Number(item.cantExistProducto)
        }));

        renderizarProductos();
    } catch (err) {
        console.error("Error cargando productos:", err);
        listaProductos.innerHTML = `<tr><td colspan="4">Error al cargar productos. Intenta recargar la página.</td></tr>`;
    }
}

// ------------------ AGREGAR PRODUCTO ------------------

function agregarProducto(p) {
    const existente = productosSeleccionados.find(x => String(x.id) === String(p.id));
    if (existente) {
        // Lógica de stock antes de agregar +1
        if (Number(existente.cantidad) >= Number(p.existencia)) {
            Swal.fire({
                icon: 'warning',
                title: 'Stock Insuficiente',
                text: 'Has alcanzado la cantidad máxima disponible.',
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'Aceptar'
            });
            return;
        }
        existente.cantidad = Number(existente.cantidad) + 1;
    } else {
        productosSeleccionados.push({
            id: p.id,
            nombre: p.nombre,
            precio: p.precio,
            cantidad: 1 // Empieza en 1
        });
    }
    actualizarTabla();
    cerrarModal();
}


// ------------------ GUARDAR PEDIDO (AJAX con CSRF) ------------------

btnGuardarPedido.addEventListener("click", () => {
    const fecha = document.getElementById("fechaVenta").value;
    const mesa = document.getElementById("numeroMesa").value || null;
    const estadoPedido = document.getElementById("estadoPedido").value;
    const estadoPago = document.getElementById("estadoPago").value;
    const descripcion = document.getElementById("descripcion").value || '';
    const usuarioId = (typeof USER_ID !== 'undefined') ? USER_ID : null;

    if (productosSeleccionados.length === 0) {
        // ... (Alerta de sin productos)
        Swal.fire({ icon: 'warning', title: 'Sin Productos', text: 'Debes añadir al menos un producto.' });
        return;
    }
    if (!usuarioId) {
        // ... (Alerta de error de usuario)
        Swal.fire({ icon: 'error', title: 'Error de Autenticación', text: 'Usuario no identificado.' });
        return;
    }

    Swal.fire({
        title: '¿Deseas finalizar la venta?',
        text: "El pedido será registrado y el inventario actualizado.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#28a745',
        cancelButtonColor: '#dc3545',
        confirmButtonText: 'Sí, finalizar',
        cancelButtonText: 'Cancelar'
    }).then(async (result) => {
        if (result.isConfirmed) {

            // Mapeo final al formato del VentaDTO de Spring Boot
            const productosParaEnviar = productosSeleccionados.map(p => ({
                id: p.id, // ID del producto
                cantidad: Number(p.cantidad),
                precio: Number(p.precio) // Precio Unitario
            }));

            const total = Number(totalGeneralEl.textContent) || 0;

            const payload = {
                fecha,
                mesa: mesa ? Number(mesa) : null, // Enviar como número o null
                estadoPedido,
                estadoPago,
                descripcion,
                usuarioId: Number(usuarioId),
                productos: productosParaEnviar,
                total // Total General calculado
            };

            btnGuardarPedido.disabled = true;
            btnGuardarPedido.textContent = "Guardando...";

            try {
                // 🔑 CONFIGURACIÓN DE SEGURIDAD CSRF
                const headers = { 'Content-Type': 'application/json' };
                const url = window.APP_URL + 'ventas/guardar'; // Usar window.APP_URL
                if (window.CSRF_HEADER && window.CSRF_TOKEN) {
                    headers[window.CSRF_HEADER] = window.CSRF_TOKEN;
                }
                const res = await fetch(url, {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify(payload)
                });

                const json = await res.json();

                if (res.ok && json.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Pedido guardado!',
                        text: "El pedido se ha guardado correctamente. ID Venta: " + json.idVenta
                    }).then(() => {
                        window.location.href = window.APP_URL + 'ventas/formGuardar';
                    });
                } else {
                    // Manejo de errores de negocio (Ej: Stock Insuficiente)
                    const message = json.message || "Error desconocido al guardar el pedido.";
                    Swal.fire({
                        icon: 'error',
                        title: 'Error al guardar',
                        text: message
                    });
                }
            } catch (err) {
                console.error(err);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de red',
                    text: 'No se pudo contactar al servidor.'
                });
            } finally {
                btnGuardarPedido.disabled = false;
                btnGuardarPedido.textContent = "✅ Aceptar Pedido";
            }
        }
    });
});