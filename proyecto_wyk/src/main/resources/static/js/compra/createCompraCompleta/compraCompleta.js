// compraCompleta.js
// Asume la existencia de las variables globales: APP_URL, CSRF_HEADER, CSRF_TOKEN, USER_ID

let itemsSeleccionados = [];
let materiaPrimaDisponible = [];
let productosDisponibles = [];

// Elementos de la tabla de detalles (IDs del HTML de Thymeleaf)
const tipoCompraSelect = document.getElementById("tipoCompra");
const tablaItemsBody = document.getElementById("tablaItemsBody"); // 🔑 ID CORREGIDO: tablaItemsBody es el <tbody> de la tabla
const totalGeneralEl = document.getElementById("totalGeneral");
const totalCompraHidden = document.getElementById("totalCompraHidden"); // Campo oculto para el total

// Elementos del modal y botones
const btnAddItem = document.getElementById("btnAddItem");
const btnGuardarCompra = document.getElementById("btnGuardarCompra");

// Modales de Compra (IDs del HTML de Thymeleaf)
const modalMateriaPrima = document.getElementById("modalMateriaPrima");
const listaMateriaPrima = document.getElementById("listaMateriaPrima");
const buscarMateriaPrimaInput = document.getElementById("buscarMateriaPrima");
const btnCerrarModalMP = document.getElementById("btnCerrarModalMP");

const modalProductos = document.getElementById("modalProductos");
const listaProductos = document.getElementById("listaProductos");
const buscarProductoInput = document.getElementById("buscarProducto");
const btnCerrarModalProd = document.getElementById("btnCerrarModalProd");

// ------------------ FUNCIONES AUXILIARES DE MODALES ------------------

// Función para mostrar/ocultar los modales personalizados
const toggleModal = (modalElement, show) => {
    if (!modalElement) return;
    if (show) {
        modalElement.classList.remove('hidden');
        modalElement.setAttribute('aria-hidden', 'false');
    } else {
        modalElement.classList.add('hidden');
        modalElement.setAttribute('aria-hidden', 'true');
    }
};

const cerrarModales = () => {
    toggleModal(modalMateriaPrima, false);
    toggleModal(modalProductos, false);
};

// ------------------ INICIALIZACIÓN ------------------
document.addEventListener("DOMContentLoaded", () => {
    const fechaCompraInput = document.getElementById("fechaCompra");

    // 1. Establecer la fecha y hora actual al cargar
    if (fechaCompraInput && !fechaCompraInput.value) {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        fechaCompraInput.value = `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    // 2. Event Listeners para cerrar modales
    btnCerrarModalMP && btnCerrarModalMP.addEventListener("click", cerrarModales);
    btnCerrarModalProd && btnCerrarModalProd.addEventListener("click", cerrarModales);

    // 3. Listener principal para añadir ítem (abre el modal correcto)
    btnAddItem && btnAddItem.addEventListener("click", abrirModalItems);

    // 4. Listeners de búsqueda
    // 🔑 USAMOS buscarMateriaPrimaInput y buscarProductoInput
    buscarMateriaPrimaInput && buscarMateriaPrimaInput.addEventListener("input", (e) => renderizarItems(e.target.value, 'MATERIA PRIMA'));
    buscarProductoInput && buscarProductoInput.addEventListener("input", (e) => renderizarItems(e.target.value, 'PRODUCTO TERMINADO'));

    // 5. Listener para guardar
    btnGuardarCompra && btnGuardarCompra.addEventListener("click", guardarCompra);

    // 6. Inicializar el manejo de tipo de compra
    tipoCompraSelect && tipoCompraSelect.addEventListener('change', manejarCambioTipo);

    // Ejecutar al inicio para asegurar el estado inicial
    const tipoItemDisplay = document.getElementById('tipoItemDisplay');
    if (tipoCompraSelect) {
        // La lógica debe tomar el valor real de Thymeleaf si existe
        const valorInicial = tipoCompraSelect.value;
        if (valorInicial) {
            tipoItemDisplay.textContent = valorInicial;
            btnAddItem.removeAttribute('disabled');
        } else {
            tipoItemDisplay.textContent = 'No Seleccionado';
            btnAddItem.setAttribute('disabled', 'true');
        }
    }
});

// ------------------ MANEJO DE TIPO DE COMPRA ------------------

function manejarCambioTipo() {
    const nuevoTipo = this.value;
    const tipoActual = document.getElementById('tipoItemDisplay').textContent;

    // 🔑 Usamos tablaItemsBody
    if (tablaItemsBody.children.length > 0 && nuevoTipo !== tipoActual && tipoActual !== 'No Seleccionado') {
        Swal.fire({
            title: '¿Cambiar Tipo de Compra?',
            text: `Si cambias a "${nuevoTipo}", se perderán todos los ítems agregados.`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#933e0d',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, cambiar tipo',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                tablaItemsBody.innerHTML = ''; // Limpia la tabla
                itemsSeleccionados = []; // Limpia el array JS
                actualizarTabla(); // Recalcula el total
                actualizarBotonYDisplay(nuevoTipo);
            } else {
                // Revertir la selección
                this.value = tipoActual;
            }
        });
    } else {
        actualizarBotonYDisplay(nuevoTipo);
    }
}

function actualizarBotonYDisplay(tipo) {
    const tipoItemDisplay = document.getElementById('tipoItemDisplay');
    if (tipo) {
        tipoItemDisplay.textContent = tipo;
        btnAddItem.removeAttribute('disabled');
    } else {
        tipoItemDisplay.textContent = 'No Seleccionado';
        btnAddItem.setAttribute('disabled', 'true');
    }
}

// ------------------ MODALES Y CARGA DE DATOS ------------------

function abrirModalItems() {
    const tipoSeleccionado = tipoCompraSelect.value;

    if (!tipoSeleccionado) {
        Swal.fire({
            icon: 'warning',
            title: 'Advertencia',
            text: 'Por favor, selecciona primero el Tipo de Compra (Materia Prima o Producto Terminado).',
            confirmButtonColor: '#3085d6'
        });
        return;
    }

    cerrarModales();

    // Limpiar campos de búsqueda al abrir
    buscarMateriaPrimaInput.value = '';
    buscarProductoInput.value = '';

    if (tipoSeleccionado === 'MATERIA PRIMA') {
        toggleModal(modalMateriaPrima, true);
        if (materiaPrimaDisponible.length === 0) {
            cargarDatos('MATERIA PRIMA');
        } else {
            renderizarItems("", 'MATERIA PRIMA');
        }
    } else if (tipoSeleccionado === 'PRODUCTO TERMINADO') {
        toggleModal(modalProductos, true);
        if (productosDisponibles.length === 0) {
            cargarDatos('PRODUCTO TERMINADO');
        } else {
            renderizarItems("", 'PRODUCTO TERMINADO');
        }
    } else {
        Swal.fire({ icon: 'info', title: 'Atención', text: 'Por favor, seleccione el Tipo de Compra primero.', confirmButtonColor: '#3085d6' });
    }
}

async function cargarDatos(tipo) {
    const endpoint = (tipo === 'MATERIA PRIMA') ? 'compras/listarMateriaPrimaAjax' : 'compras/listarProductosAjax';
    const targetList = (tipo === 'MATERIA PRIMA') ? listaMateriaPrima : listaProductos;

    targetList.innerHTML = `<tr><td colspan="5">Cargando ${tipo}...</td></tr>`;

    try {
        const url = window.APP_URL + endpoint; // 🔑 USAMOS window.APP_URL
        const res = await fetch(url, { cache: "no-store" });
        if (!res.ok) throw new Error('Error en la respuesta del servidor');
        const data = await res.json();

        const itemsMapeados = (Array.isArray(data) ? data : []).map(item => {
            if (tipo === 'MATERIA PRIMA') {
                return {
                    id: item.ID_MATERIA_PRIMA ?? null,
                    nombre: item.NOMBRE_MATERIA_PRIMA ?? '',
                    // El `parseInt` es crucial para que las operaciones sean numéricas
                    precio_referencia: parseInt(item.VALOR_UNITARIO_MAT_PRIMA ?? 0, 10),
                    existencia: parseInt(item.CANTIDAD_EXIST_MATERIA_PRIMA ?? 0, 10),
                    unidad: item.PRESENTACION_MATERIA_PRIMA ?? 'N/A'
                };
            } else {
                return {
                    id: item.ID_PRODUCTO ?? null,
                    nombre: item.NOMBRE_PRODUCTO ?? '',
                    precio_referencia: parseInt(item.VALOR_UNITARIO_PRODUCTO ?? 0, 10),
                    existencia: parseInt(item.CANT_EXIST_PRODUCTO ?? 0, 10),
                    unidad: 'N/A'
                };
            }
        });

        if (tipo === 'MATERIA PRIMA') {
            materiaPrimaDisponible = itemsMapeados;
        } else {
            productosDisponibles = itemsMapeados;
        }

        renderizarItems("", tipo);
    } catch (err) {
        console.error(`Error cargando ${tipo}:`, err);
        targetList.innerHTML = `<tr><td colspan="5">Error cargando ${tipo}</td></tr>`;
        Swal.fire({
            icon: 'error',
            title: 'Error de Servidor',
            text: `No se pudieron cargar los ítems de ${tipo}. Revisa la consola para más detalles.`,
            confirmButtonColor: '#d33'
        });
    }
}

// Renderiza la lista de Materias Primas o Productos en el modal
function renderizarItems(filtro = "", tipo) {
    const data = (tipo === 'MATERIA PRIMA') ? materiaPrimaDisponible : productosDisponibles;
    const targetList = (tipo === 'MATERIA PRIMA') ? listaMateriaPrima : listaProductos;
    targetList.innerHTML = "";
    const filtroLow = (filtro || "").toLowerCase();

    const filtrados = data.filter(p => p.nombre.toLowerCase().includes(filtroLow));

    // colspan: MP tiene 5 columnas, Prod tiene 4 columnas
    const colspan = (tipo === 'MATERIA PRIMA') ? 5 : 4;

    if (filtrados.length === 0) {
        targetList.innerHTML = `<tr><td colspan="${colspan}">No hay ${tipo.toLowerCase()} disponibles</td></tr>`;
        return;
    }

    filtrados.forEach(p => {
        const tr = document.createElement("tr");

        const tdNombre = document.createElement("td");
        tdNombre.textContent = p.nombre;

        const tdSecundario = document.createElement("td"); // Unidad o Precio Venta
        const tdPrecioRef = document.createElement("td"); // Precio Ref (MP) o Venta (Prod)
        const tdExistencia = document.createElement("td"); // Stock Actual

        // Lógica de visualización
        if (tipo === 'MATERIA PRIMA') {
            // TH: Nombre | Unidad | Precio Ref. | Stock Actual | Acción
            tdSecundario.textContent = p.unidad;
            tdPrecioRef.textContent = `$${p.precio_referencia}`;
            tdExistencia.textContent = p.existencia;

            tr.appendChild(tdNombre);
            tr.appendChild(tdSecundario);
            tr.appendChild(tdPrecioRef);
            tr.appendChild(tdExistencia);

        } else {
            // TH: Nombre | Precio Venta | Stock Actual | Acción
            tdSecundario.textContent = `$${p.precio_referencia}`; // Precio Venta
            tdExistencia.textContent = p.existencia; // Stock Actual

            tr.appendChild(tdNombre);
            tr.appendChild(tdSecundario);
            tr.appendChild(tdExistencia);
        }

        const tdAccion = crearBotonAgregar(p, tipo);
        tr.appendChild(tdAccion);

        targetList.appendChild(tr);
    });
}

function crearBotonAgregar(p, tipo) {
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

    btn.addEventListener("click", () => agregarItem(p, tipo));
    tdAccion.appendChild(btn);
    return tdAccion;
}


// ------------------ AGREGAR ITEM A LA TABLA DE COMPRA ------------------

function agregarItem(item, tipo) {
    const itemTipoCorto = tipo === 'MATERIA PRIMA' ? 'MP' : 'PROD';
    const itemExistente = itemsSeleccionados.find(x => String(x.id) === String(item.id) && x.tipo === itemTipoCorto);

    if (itemExistente) {
        Swal.fire({ icon: 'info', title: 'Ya Agregado', text: `El ${tipo.toLowerCase()} ya está en la lista de compra. Edite la cantidad y el precio allí.`, confirmButtonColor: '#3085d6' });
    } else {
        itemsSeleccionados.push({
            id: item.id,
            nombre: item.nombre,
            tipo: itemTipoCorto,
            cantidad: 1,
            // Usamos el precio de referencia como precio inicial de la compra
            precio: item.precio_referencia
        });
        actualizarTabla();
    }
    cerrarModales();
}

// ------------------ TABLA DE ITEMS SELECCIONADOS ------------------

function actualizarTabla() {
    tablaItemsBody.innerHTML = "";
    let total = 0;

    itemsSeleccionados.forEach((item, i) => {
        // Cálculo con valores enteros
        const cantidad = parseInt(item.cantidad, 10);
        const precio = parseInt(item.precio, 10);
        const subtotal = Math.round(cantidad * precio);
        total += subtotal;

        const tr = document.createElement("tr");

        const tdTipo = document.createElement("td");
        tdTipo.textContent = item.tipo === 'MP' ? 'M. Prima' : 'Producto';

        const tdNombre = document.createElement("td");
        tdNombre.textContent = item.nombre;

        // Campo Cantidad (Editable)
        const tdCantidad = document.createElement("td");
        const inputCantidad = document.createElement("input");
        inputCantidad.type = "number";
        inputCantidad.min = "1";
        inputCantidad.value = cantidad;
        inputCantidad.className = "input-cantidad"; // Usamos el input-cantidad general
        inputCantidad.style.width = '80px';
        inputCantidad.addEventListener("change", (e) => {
            let v = parseInt(e.target.value, 10);
            if (isNaN(v) || v < 1) v = 1;
            itemsSeleccionados[i].cantidad = v;
            actualizarTabla();
        });
        tdCantidad.appendChild(inputCantidad);

        // Campo Precio Unitario (Editable en tabla)
        const tdPrecio = document.createElement("td");
        const inputPrecio = document.createElement("input");
        inputPrecio.type = "number";
        inputPrecio.min = "1";
        inputPrecio.value = precio;
        inputPrecio.className = "input-cantidad"; // Reutilizamos el estilo
        inputPrecio.style.width = '100px';

        inputPrecio.addEventListener("change", (e) => {
            let v = parseInt(e.target.value, 10);
            if (isNaN(v) || v < 1) v = 1;

            itemsSeleccionados[i].precio = v;
            actualizarTabla();
        });
        tdPrecio.appendChild(inputPrecio);

        // Subtotal
        const tdSubtotal = document.createElement("td");
        tdSubtotal.textContent = `$${subtotal}`;

        // Acciones (Eliminar)
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
                text: "¡El ítem se eliminará de la lista!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    itemsSeleccionados.splice(i, 1);
                    actualizarTabla();
                    Swal.fire({ icon: 'success', title: '¡Removido!', text: 'El ítem ha sido removido.', confirmButtonColor: '#3085d6' });
                }
            });
        });
        tdAcciones.appendChild(btnEliminar);

        tr.appendChild(tdTipo);
        tr.appendChild(tdNombre);
        tr.appendChild(tdCantidad);
        tr.appendChild(tdPrecio);
        tr.appendChild(tdSubtotal);
        tr.appendChild(tdAcciones);

        tablaItemsBody.appendChild(tr);
    });

    // Mostrar el total como entero
    totalGeneralEl.textContent = Math.round(total);
    totalCompraHidden.value = Math.round(total);
}

// ------------------ GUARDAR COMPRA ------------------
async function guardarCompra(e) {
    e.preventDefault();

    const form = document.getElementById("formNuevaCompra");
    const formData = new FormData(form);

    const total = parseInt(totalCompraHidden.value, 10);
    const usuarioId = document.getElementById("usuarioId").value; // Obtener el ID inyectado por Thymeleaf

    // Obtener los valores del ENUM directamente del select.
     const tipoCompraValue = tipoCompraSelect.value;
     const estadoCompraValue = formData.get('estadoCompra');

    // 1. Validaciones
    if (itemsSeleccionados.length === 0) {
        Swal.fire({ icon: 'warning', title: 'Sin Ítems', text: 'Debes añadir al menos un ítem a la compra.', confirmButtonColor: '#3085d6' });
        return;
    }

    const itemsSinPrecioValido = itemsSeleccionados.filter(item => item.precio <= 0);
    if (itemsSinPrecioValido.length > 0) {
        Swal.fire({ icon: 'error', title: 'Precio Inválido', text: 'Uno o más ítems tienen un Precio Unitario de $0 o menos. Por favor, corrígelo en la tabla de detalles.', confirmButtonColor: '#d33' });
        return;
    }

    if (total <= 0 || isNaN(total)) {
        Swal.fire({ icon: 'warning', title: 'Total Inválido', text: 'El total de la compra debe ser mayor a cero.', confirmButtonColor: '#3085d6' });
        return;
    }
    if (!formData.get('nombreProveedor') || !formData.get('marca') || !formData.get('telProveedor') || !formData.get('emailProveedor') || !formData.get('tipo')) {
        Swal.fire({ icon: 'warning', title: 'Datos Faltantes', text: 'Complete todos los campos de la compra (Proveedor, Marca, Teléfono, Email, Tipo de Compra).', confirmButtonColor: '#3085d6' });
        return;
    }
    // Validamos que los ENUMs tengan un valor
    if (!tipoCompraValue || !estadoCompraValue) {
        Swal.fire({ icon: 'error', title: 'Error de Selección', text: 'El Tipo de Compra y el Estado de Factura son obligatorios.', confirmButtonColor: '#d33' });
        return;
    }

    // 2. Mapear ítems para el backend (estructura CompraDetalleDTO)
    const itemsParaEnviar = itemsSeleccionados.map(item => ({
        id: item.id,
        tipo: item.tipo, // 'MP' o 'PROD'
        cantidad: parseInt(item.cantidad, 10),
        precio_unitario: parseInt(item.precio, 10),
        subtotal: Math.round(parseInt(item.cantidad, 10) * parseInt(item.precio, 10))
    }));

    // 3. Crear Payload para el Controller (JSON)
    const payload = {
        fecha: formData.get('fecha'),
        nombreProveedor: formData.get('nombreProveedor'),
        marca: formData.get('marca'),
        telProveedor: formData.get('telProveedor'),
        emailProveedor: formData.get('emailProveedor'),
        tipo: formData.get('tipo'),
        estadoCompra: formData.get('estadoCompra'),
        usuarioId: parseInt(usuarioId, 10), // Usamos el ID del input hidden
        descripcion: formData.get('descripcion'),
        totalCompra: total,
        items: itemsParaEnviar
    };

    // 4. Diálogo de confirmación
    Swal.fire({
        title: '¿Deseas registrar la compra?',
        text: "Una vez aceptada, la compra será registrada y el inventario actualizado.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#28a745',
        cancelButtonColor: '#dc3545',
        confirmButtonText: 'Sí, registrar',
        cancelButtonText: 'Cancelar'
    }).then(async (result) => {
        if (result.isConfirmed) {
            btnGuardarCompra.disabled = true;
            btnGuardarCompra.textContent = "Guardando...";

            try {
                // 🔑 CONFIGURACIÓN DE SEGURIDAD CSRF (Header)
                const headers = { 'Content-Type': 'application/json' };
                const url = window.APP_URL + 'compras/guardar'; // Usamos la URL correcta de Spring Boot
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
                        title: '¡Compra registrada!',
                        text: `Compra ID ${json.idCompra} registrada correctamente.`,
                        confirmButtonColor: '#3085d6'
                    }).then(() => {
                        // Redireccionar al listado/reporte de compras o recargar
                        window.location.href = window.APP_URL + 'compras';
                    });
                } else {
                    const message = json.message || "Error desconocido al registrar la compra. Revise el log del servidor.";
                    Swal.fire({
                        icon: 'error',
                        title: 'Error al guardar',
                        text: message,
                        confirmButtonColor: '#d33'
                    });
                }
            } catch (err) {
                console.error("Error de Fetch/Red:", err);
                Swal.fire({
                    icon: 'error',
                    title: 'Error de red',
                    text: 'No se pudo contactar al servidor. Revisa tu conexión o la URL del endpoint.',
                    confirmButtonColor: '#d33'
                });
            } finally {
                btnGuardarCompra.disabled = false;
                btnGuardarCompra.textContent = "✅ Registrar Compra";
            }
        }
    });
}