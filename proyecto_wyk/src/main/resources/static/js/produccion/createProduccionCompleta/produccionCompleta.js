document.addEventListener('DOMContentLoaded', () => {
    const appUrl = window.APP_URL || '/';
    let insumosSeleccionados = [];
    let listaMateriaPrimaFull = [];
    let listaProductosFull = [];

    const btnGuardar = document.getElementById('btnGuardarProduccion');
    const tablaInsumosBody = document.getElementById('tablaInsumosBody');
    const nombreProduccion = document.getElementById('nombreProduccion');
    const idProductoFinal = document.getElementById('idProductoFinal');
    const nombreProductoFinal = document.getElementById('nombreProductoFinal');
    const cantidadFinal = document.getElementById('cantidadFinal');
    const descripcionProduccion = document.getElementById('descripcionProduccion');
    const elModalProductos = document.getElementById('modalProductos');
    const elModalMP = document.getElementById('modalMP');

    const abrirModal = (el) => {
        el.classList.remove('hidden');
        el.style.display = 'flex'; // Asegura que se vea si el CSS depende de flex
    };
    window.cerrarModal = (el) => {
        el.classList.add('hidden');
        el.style.display = 'none';
    };

    const mostrarAlerta = (icon, title, text) => {
        Swal.fire({ icon, title, text, confirmButtonColor: '#933e0d' });
    };

    const validarFormulario = () => {
        const hayNombre = nombreProduccion.value.trim() !== "";
        const hayProducto = idProductoFinal.value !== "";
        const hayInsumos = insumosSeleccionados.length > 0;
        const cantidadValida = parseFloat(cantidadFinal.value) > 0;
        btnGuardar.disabled = !(hayNombre && hayProducto && hayInsumos && cantidadValida);
    };

    // --- PRODUCTOS ---
    document.getElementById('btnModalProducto').addEventListener('click', async () => {
        const listBody = document.getElementById('listaProductos');
        listBody.innerHTML = '<tr><td colspan="3" class="text-center">Cargando...</td></tr>';
        abrirModal(elModalProductos);
        try {
            const resp = await fetch(`${appUrl}productos/listar`);
            listaProductosFull = await resp.json();
            renderProductos(listaProductosFull);
        } catch (error) {
            listBody.innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error al cargar productos</td></tr>';
        }
    });

    const renderProductos = (data) => {
        const listBody = document.getElementById('listaProductos');
        listBody.innerHTML = data.map(p => `
            <tr>
                <td>${p.nombreProducto || p.NOMBRE_PRODUCTO}</td>
                <td>${p.cantExistProducto || p.CANT_EXIST_PRODUCTO}</td>
                <td>
                    <button type="button" class="btn-azul btn-sm" onclick="seleccionarProducto(${p.idProducto || p.ID_PRODUCTO}, '${p.nombreProducto || p.NOMBRE_PRODUCTO}')">
                        Seleccionar
                    </button>
                </td>
            </tr>
        `).join('');
    };

    window.seleccionarProducto = (id, nombre) => {
        idProductoFinal.value = id;
        nombreProductoFinal.value = nombre;
        cerrarModal(elModalProductos);
        validarFormulario();
    };

    // --- MATERIA PRIMA ---
    document.getElementById('btnAddInsumo').addEventListener('click', async () => {
        const listBody = document.getElementById('listaMP');
        listBody.innerHTML = '<tr><td colspan="4" class="text-center">Cargando...</td></tr>';
        abrirModal(elModalMP);
        try {
            const resp = await fetch(`${appUrl}materiasPrimas/listar`);
            listaMateriaPrimaFull = await resp.json();
            renderMP(listaMateriaPrimaFull);
        } catch (error) {
            listBody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Error al cargar insumos</td></tr>';
        }
    });

    const renderMP = (data) => {
        const listBody = document.getElementById('listaMP');

        // Si no hay datos, mostrar mensaje
        if (!data || data.length === 0) {
            listBody.innerHTML = '<tr><td colspan="4" class="text-center">No hay insumos activos.</td></tr>';
            return;
        }

        listBody.innerHTML = data.map(m => {
            // NOMBRES EXACTOS DE ENTIDAD JAVA
            const id = m.idMateriaPrima;
            const nombre = m.nombreMateriaPrima;
            const unidad = m.presentacionMateriaPrima;
            const stock = m.cantidadExistMateriaPrima;

            // Validamos si no hay stock
            const sinStock = stock <= 0;
            const styleRow = sinStock ? 'style="background-color: #f2f2f2; color: #a9a9a9;"' : (stock < 5 ? 'style="background-color: #ffe6e6; color: #cc0000;"' : '');

            // Estilo rojo si hay poco stock (menos de 5 unidades)
            const styleRed = (stock < 5) ? 'style="background-color: #ffe6e6; color: #cc0000;"' : '';

            // Usamos .replace para evitar errores si el nombre tiene comillas simples
            const nombreLimpio = nombre.replace(/'/g, "\\'");

            return `
                <tr ${styleRed}>
                    <td>${nombre}</td>
                    <td>${unidad}</td>
                    <td><strong>${stock}</strong></td>
                    <td>
                        <button type="button" class="btn-verde btn-sm"
                            onclick="agregarInsumo(${id}, '${nombreLimpio}', '${unidad}', ${stock})">
                            ➕
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    };

    window.agregarInsumo = (id, nombre, unidad, stock) => {
        if (insumosSeleccionados.some(i => i.materiaPrimaId === id)) {
            mostrarAlerta('info', 'Ya agregado', 'Este insumo ya está en la lista.');
            return;
        }
        insumosSeleccionados.push({
            materiaPrimaId: id,
            nombre: nombre,
            unidad: unidad,
            stockDisponible: stock, // Aquí se guarda el valor que viene del modal
            cantidadRequerida: 1
        });
        cerrarModal(elModalMP);
        renderTablaInsumos();
    };

    const renderTablaInsumos = () => {
            tablaInsumosBody.innerHTML = insumosSeleccionados.map((item, index) => {
                return `
                    <tr>
                        <td><strong>${item.nombre}</strong><br><small class="text-muted">Stock: ${item.stockDisponible}</small></td>
                        <td>
                            <input type="number" class="form-control"
                                value="${item.cantidadRequerida}" min="0.1" step="0.1"
                                onchange="actualizarCantidadInsumo(${index}, this.value)">
                        </td>
                        <td>${item.unidad}</td>
                        <td>
                            <button type="button" class="btn-eliminar-item btn-sm" onclick="confirmarEliminar(${index})" style="background:none; border:none; cursor:pointer;">
                                 <lord-icon src="https://cdn.lordicon.com/hfacemai.json" trigger="hover" colors="primary:#c71f16" style="width:30px;height:30px"></lord-icon>
                            </button>
                        </td>
                    </tr>
                `;
            }).join('');
            validarFormulario();
        };

    window.actualizarCantidadInsumo = (index, valor) => {
            const cant = parseFloat(valor);
            const insumo = insumosSeleccionados[index];

            // 1. Validar si la cantidad supera el stock
            if (cant > insumo.stockDisponible) {
                mostrarAlerta('error', 'Cantidad no permitida',
                    `Solo tienes ${insumo.stockDisponible} ${insumo.unidad} disponibles de "${insumo.nombre}".`);

                // Revertir al máximo disponible para evitar errores
                insumo.cantidadRequerida = insumo.stockDisponible;
            }
            // 2. Validar que no sea cero o negativo
            else if (cant <= 0 || isNaN(cant)) {
                insumo.cantidadRequerida = 1;
            }
            // 3. Cantidad correcta
            else {
                insumo.cantidadRequerida = cant;
            }

            renderTablaInsumos();
        };

    window.confirmarEliminar = (index) => {
        Swal.fire({
            title: '¿Quitar insumo?',
            text: `Se eliminará "${insumosSeleccionados[index].nombre}" de esta producción.`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, quitar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                insumosSeleccionados.splice(index, 1);
                renderTablaInsumos();
            }
        });
    };

    // --- LÓGICA DE CIERRE DE MODALES ---

    // Al hacer clic fuera
    document.addEventListener('click', (event) => {
        if (event.target.classList.contains('modal')) {
            cerrarModal(event.target);
        }
    });

    // Prevenir que clics dentro de la tarjeta blanca cierren el modal
    document.querySelectorAll('.modal-content').forEach(content => {
        content.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    });

    // --- BÚSQUEDAS ---
    document.getElementById('buscarProducto').addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase();
        const filtrados = listaProductosFull.filter(p =>
            (p.nombreProducto || p.NOMBRE_PRODUCTO).toLowerCase().includes(query)
        );
        renderProductos(filtrados);
    });

    document.getElementById('buscarMP').addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase();
        const filtrados = listaMateriaPrimaFull.filter(m =>
            (m.nombreMateriaPrima || m.NOMBRE_MATERIA_PRIMA).toLowerCase().includes(query)
        );
        renderMP(filtrados);
    });

    // --- ENVÍO ---
    btnGuardar.addEventListener('click', async () => {
        const sinStock = insumosSeleccionados.some(i => i.cantidadRequerida > i.stockDisponible);
        if (sinStock) {
            mostrarAlerta('error', 'Stock Insuficiente', 'Uno o más insumos superan la cantidad disponible.');
            return;
        }

        const dataProduccion = {
            nombre: nombreProduccion.value,
            cantidadAProducir: parseInt(cantidadFinal.value),
            descripcion: descripcionProduccion.value,
            productoId: parseInt(idProductoFinal.value),
            usuarioId: window.USER_ID,
            insumos: insumosSeleccionados.map(i => ({
                materiaPrimaId: i.materiaPrimaId,
                cantidadRequerida: i.cantidadRequerida
            }))
        };

        Swal.fire({
            title: '¿Confirmar Producción?',
            text: "Se descontarán los insumos y se registrará el lote.",
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sí, finalizar',
            cancelButtonText: 'Revisar',
            confirmButtonColor: '#198754'
        }).then(async (result) => {
            if (result.isConfirmed) {
                Swal.showLoading();
                try {
                    const response = await fetch(`${appUrl}produccion/guardar`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            [window.CSRF_HEADER]: window.CSRF_TOKEN
                        },
                        body: JSON.stringify(dataProduccion)
                    });
                    const res = await response.json();
                    if (res.success) {
                        Swal.fire('¡Éxito!', res.message, 'success').then(() => {
                            window.location.href = `${appUrl}produccion`;
                        });
                    } else {
                        mostrarAlerta('error', 'Error', res.message);
                    }
                } catch (error) {
                    mostrarAlerta('error', 'Error de red', 'No se pudo conectar con el servidor.');
                }
            }
        });
    });

    window.addEventListener('click', (event) => {
        if (event.target === elModalProductos) cerrarModal(elModalProductos);
        if (event.target === elModalMP) cerrarModal(elModalMP);
    });

    nombreProduccion.addEventListener('input', validarFormulario);
    cantidadFinal.addEventListener('input', validarFormulario);
    validarFormulario();
});