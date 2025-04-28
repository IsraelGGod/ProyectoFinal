import { api } from './api.js';

// Exportar función de inicialización
export function initClientes() {
    const clientesSection = document.getElementById('clientes');
    if (!clientesSection) return;

    const tablaClientes = document.getElementById('tabla-clientes').querySelector('tbody');
    const buscarClienteInput = document.getElementById('buscar-cliente');
    const btnBuscarCliente = document.getElementById('btn-buscar-cliente');
    const nuevoClienteBtn = document.getElementById('nuevo-cliente');
    const modalCliente = document.getElementById('modal-cliente');
    const formCliente = document.getElementById('form-cliente');
    const modalTitulo = document.getElementById('modal-cliente-titulo');

    // Configurar eventos
    btnBuscarCliente.addEventListener('click', buscarClientes);
    buscarClienteInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') buscarClientes();
    });

    nuevoClienteBtn.addEventListener('click', () => {
        formCliente.reset();
        document.getElementById('cliente-id').value = '';
        modalTitulo.textContent = 'Nuevo Cliente';
        modalCliente.style.display = 'block';
    });

    modalCliente.querySelector('.close').addEventListener('click', () => {
        modalCliente.style.display = 'none';
    });

    formCliente.addEventListener('submit', guardarCliente);

    // Cargar clientes iniciales
    cargarClientes();

    // Funciones internas
    async function cargarClientes() {
        try {
            const clientes = await api.getClientes();
            renderClientes(clientes);
        } catch (error) {
            alert('Error al cargar clientes: ' + error.message);
        }
    }

    async function buscarClientes() {
        const query = buscarClienteInput.value.trim();
        try {
            const clientes = query ? await api.searchClientes(query) : await api.getClientes();
            renderClientes(clientes);
        } catch (error) {
            alert('Error al buscar clientes: ' + error.message);
        }
    }

    function renderClientes(clientes) {
        tablaClientes.innerHTML = '';
        clientes.forEach(cliente => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${cliente.id}</td>
                <td>${cliente.nombre}</td>
                <td>${cliente.email}</td>
                <td>${cliente.matricula || 'N/A'}</td>
                <td>
                    <button class="btn-edit" data-id="${cliente.id}">Editar</button>
                    <button class="btn-danger" data-id="${cliente.id}">Eliminar</button>
                </td>
            `;
            tablaClientes.appendChild(tr);
        });

        // Configurar eventos de los botones
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', cargarClienteParaEditar);
        });

        document.querySelectorAll('.btn-danger').forEach(btn => {
            btn.addEventListener('click', eliminarCliente);
        });
    }

    async function cargarClienteParaEditar(e) {
        const id = e.target.getAttribute('data-id');
        try {
            const cliente = await api.getCliente(id);
            document.getElementById('cliente-id').value = cliente.id;
            document.getElementById('cliente-nombre').value = cliente.nombre;
            document.getElementById('cliente-email').value = cliente.email;
            document.getElementById('cliente-telefono').value = cliente.telefono || '';
            document.getElementById('cliente-matricula').value = cliente.matricula || '';

            modalTitulo.textContent = 'Editar Cliente';
            modalCliente.style.display = 'block';
        } catch (error) {
            alert('Error al cargar cliente: ' + error.message);
        }
    }

    async function eliminarCliente(e) {
        if (!confirm('¿Estás seguro de eliminar este cliente?')) return;

        const id = e.target.getAttribute('data-id');
        try {
            await api.deleteCliente(id);
            cargarClientes();
        } catch (error) {
            alert('Error al eliminar cliente: ' + error.message);
        }
    }

    async function guardarCliente(e) {
        e.preventDefault();

        const cliente = {
            nombre: document.getElementById('cliente-nombre').value,
            email: document.getElementById('cliente-email').value,
            telefono: document.getElementById('cliente-telefono').value || null,
            matricula: document.getElementById('cliente-matricula').value || null
        };

        const id = document.getElementById('cliente-id').value;

        try {
            if (id) {
                await api.updateCliente(id, cliente);
            } else {
                await api.createCliente(cliente);
            }

            modalCliente.style.display = 'none';
            cargarClientes();
        } catch (error) {
            alert('Error al guardar cliente: ' + error.message);
        }
    }
}