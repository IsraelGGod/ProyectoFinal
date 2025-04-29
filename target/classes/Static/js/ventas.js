import { api } from './api.js';

// Exportar función de inicialización
export function initVentas() {
    const ventasSection = document.getElementById('ventas');
    if (!ventasSection) return;

    const tablaVentas = document.getElementById('tabla-ventas').querySelector('tbody');
    const nuevaVentaBtn = document.getElementById('nueva-venta');
    const modalVenta = document.getElementById('modal-venta');
    const formVenta = document.getElementById('form-venta');
    const detallesContainer = document.querySelector('.detalles-venta');
    const agregarDetalleBtn = document.getElementById('agregar-detalle');
    const clienteSelect = document.getElementById('venta-cliente');
    const modalDetalleVenta = document.getElementById('modal-detalle-venta');

    // Configurar eventos
    nuevaVentaBtn.addEventListener('click', initNuevaVenta);
    modalVenta.querySelector('.close').addEventListener('click', () => {
        modalVenta.style.display = 'none';
    });
    modalDetalleVenta.querySelector('.close').addEventListener('click', () => {
        modalDetalleVenta.style.display = 'none';
    });
    agregarDetalleBtn.addEventListener('click', agregarDetalle);
    formVenta.addEventListener('change', calcularTotal);
    formVenta.addEventListener('submit', guardarVenta);

    // Cargar ventas iniciales
    cargarVentas();

    // Funciones internas
    async function initNuevaVenta() {
        formVenta.reset();
        document.getElementById('venta-id').value = '';
        detallesContainer.innerHTML = '';
        await agregarDetalle(); // Añadido await para sincronizar

        try {
            const clientes = await api.getClientes();
            clienteSelect.innerHTML = '<option value="">Seleccione un cliente</option>';
            clientes.forEach(cliente => {
                const option = document.createElement('option');
                option.value = cliente.id;
                option.textContent = `${cliente.nombre} (${cliente.email})`;
                clienteSelect.appendChild(option);
            });

            const libros = await api.getLibros();
            document.querySelectorAll('.detalle-libro').forEach(select => {
                select.innerHTML = '<option value="">Seleccione un libro</option>';
                libros.forEach(libro => {
                    const option = document.createElement('option');
                    option.value = libro.id;
                    option.textContent = `${libro.titulo} - $${libro.precio.toFixed(2)} (Stock: ${libro.stock})`;
                    option.dataset.precio = libro.precio;
                    option.dataset.stock = libro.stock;
                    select.appendChild(option);
                });
            });

            modalVenta.style.display = 'block';
        } catch (error) {
            console.error('Error al cargar datos:', error);
            alert('Error al cargar datos para nueva venta. Por favor, recarga la página.');
        }
    }

    async function cargarVentas() {
        try {
            const ventas = await api.getVentas();
            renderVentas(ventas);
        } catch (error) {
            console.error('Error al cargar ventas:', error);
            alert('No se pudieron cargar las ventas. Verifica la conexión.');
        }
    }

    function renderVentas(ventas) {
        if (!ventas || ventas.length === 0) {
            tablaVentas.innerHTML = '<tr><td colspan="5">No hay ventas registradas</td></tr>';
            return;
        }

        tablaVentas.innerHTML = '';
        ventas.forEach(venta => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${venta.id}</td>
                <td>${venta.cliente?.nombre || 'Cliente no disponible'}</td>
                <td>${new Date(venta.fecha).toLocaleDateString()}</td>
                <td>$${venta.total?.toFixed(2) || '0.00'}</td>
                <td>
                    <button class="btn-edit" data-id="${venta.id}">Detalles</button>
                </td>
            `;
            tablaVentas.appendChild(tr);
        });

        // Configurar eventos de los botones
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', mostrarDetallesVenta);
        });
    }

    async function agregarDetalle() {
        try {
            const detalleDiv = document.createElement('div');
            detalleDiv.className = 'detalle-item';
            detalleDiv.innerHTML = `
                <select class="detalle-libro">
                    <option value="">Seleccione un libro</option>
                </select>
                <input type="number" class="detalle-cantidad" min="1" value="1" placeholder="Cantidad">
                <button type="button" class="btn-eliminar-detalle">Eliminar</button>
            `;

            const libros = await api.getLibros();
            const select = detalleDiv.querySelector('.detalle-libro');

            libros.forEach(libro => {
                const option = document.createElement('option');
                option.value = libro.id;
                option.textContent = `${libro.titulo} - $${libro.precio.toFixed(2)} (Stock: ${libro.stock})`;
                option.dataset.precio = libro.precio;
                option.dataset.stock = libro.stock;
                select.appendChild(option);
            });

            // Eliminar detalle
            detalleDiv.querySelector('.btn-eliminar-detalle').addEventListener('click', () => {
                detalleDiv.remove();
                calcularTotal();
            });

            detallesContainer.appendChild(detalleDiv);
        } catch (error) {
            console.error('Error al cargar libros:', error);
            alert('No se pudieron cargar los libros. Intenta nuevamente.');
        }
    }

    function calcularTotal() {
        let subtotal = 0;

        document.querySelectorAll('.detalle-item').forEach(item => {
            const select = item.querySelector('.detalle-libro');
            const cantidadInput = item.querySelector('.detalle-cantidad');

            if (select.value && cantidadInput.value) {
                const precio = parseFloat(select.selectedOptions[0].dataset.precio);
                const cantidad = parseInt(cantidadInput.value);
                subtotal += precio * cantidad;
            }
        });

        const descuento = parseFloat(document.getElementById('venta-descuento').value) || 0;
        const descuentoMonto = subtotal * (descuento / 100);
        const total = subtotal - descuentoMonto;

        document.getElementById('venta-subtotal').textContent = subtotal.toFixed(2);
        document.getElementById('venta-descuento-monto').textContent = descuentoMonto.toFixed(2);
        document.getElementById('venta-total').textContent = total.toFixed(2);
    }

    async function guardarVenta(e) {
        e.preventDefault();

        const clienteId = document.getElementById('venta-cliente').value;
        if (!clienteId) {
            alert('Seleccione un cliente');
            return;
        }

        const detalles = [];
        let error = false;

        document.querySelectorAll('.detalle-item').forEach(item => {
            const select = item.querySelector('.detalle-libro');
            const cantidadInput = item.querySelector('.detalle-cantidad');

            if (!select.value) {
                alert('Seleccione un libro para todos los detalles');
                error = true;
                return;
            }

            if (!cantidadInput.value || parseInt(cantidadInput.value) <= 0) {
                alert('Ingrese una cantidad válida');
                error = true;
                return;
            }

            const stock = parseInt(select.selectedOptions[0].dataset.stock);
            if (parseInt(cantidadInput.value) > stock) {
                alert(`Stock insuficiente para: ${select.selectedOptions[0].textContent}`);
                error = true;
                return;
            }

            detalles.push({
                libroId: parseInt(select.value),
                cantidad: parseInt(cantidadInput.value)
            });
        });

        if (error || detalles.length === 0) return;

        const venta = {
            clienteId: parseInt(clienteId),
            descuento: parseFloat(document.getElementById('venta-descuento').value) || 0,
            detalles: detalles
        };

        try {
            await api.createVenta(venta);
            modalVenta.style.display = 'none';
            await cargarVentas(); // Recargar ventas después de guardar
        } catch (error) {
            console.error('Error al registrar venta:', error);
            alert('Error al guardar la venta. Verifica los datos.');
        }
    }

    async function mostrarDetallesVenta(e) {
        const ventaId = e.target.getAttribute('data-id');
        try {
            const venta = await api.getVenta(ventaId);
            const detalles = await api.getDetallesVenta(ventaId);

            document.getElementById('detalle-cliente').textContent = `${venta.cliente?.nombre || 'N/A'} (${venta.cliente?.email || 'N/A'})`;
            document.getElementById('detalle-fecha').textContent = new Date(venta.fecha).toLocaleDateString();
            document.getElementById('detalle-total').textContent = venta.total?.toFixed(2) || '0.00';

            const tbody = document.querySelector('#tabla-detalle-venta tbody');
            tbody.innerHTML = '';

            detalles.forEach(detalle => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${detalle.libro?.titulo || 'Libro no disponible'}</td>
                    <td>${detalle.cantidad}</td>
                    <td>$${detalle.libro?.precio?.toFixed(2) || '0.00'}</td>
                    <td>$${detalle.subtotal?.toFixed(2) || '0.00'}</td>
                `;
                tbody.appendChild(tr);
            });

            modalDetalleVenta.style.display = 'block';
        } catch (error) {
            console.error('Error al cargar detalles:', error);
            alert('No se pudieron cargar los detalles de la venta.');
        }
    }
}