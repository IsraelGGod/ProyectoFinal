import { api } from './api.js';

// Exportar función de inicialización
export function initLibros() {
    const librosSection = document.getElementById('libros');
    if (!librosSection) return;

    const tablaLibros = document.getElementById('tabla-libros').querySelector('tbody');
    const buscarLibroInput = document.getElementById('buscar-libro');
    const filtroLibro = document.getElementById('filtro-libro');
    const btnBuscarLibro = document.getElementById('btn-buscar-libro');
    const nuevoLibroBtn = document.getElementById('nuevo-libro');
    const modalLibro = document.getElementById('modal-libro');
    const formLibro = document.getElementById('form-libro');
    const modalTitulo = document.getElementById('modal-libro-titulo');

    // Configurar eventos
    btnBuscarLibro.addEventListener('click', buscarLibros);
    buscarLibroInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') buscarLibros();
    });

    nuevoLibroBtn.addEventListener('click', () => {
        formLibro.reset();
        document.getElementById('libro-id').value = '';
        modalTitulo.textContent = 'Nuevo Libro';
        modalLibro.style.display = 'block';
    });

    modalLibro.querySelector('.close').addEventListener('click', () => {
        modalLibro.style.display = 'none';
    });

    formLibro.addEventListener('submit', guardarLibro);

    // Cargar libros iniciales
    cargarLibros();

    // Funciones internas
    async function cargarLibros() {
        try {
            const libros = await api.getLibros();
            renderLibros(libros);
        } catch (error) {
            alert('Error al cargar libros: ' + error.message);
        }
    }

    async function buscarLibros() {
        const query = buscarLibroInput.value.trim();
        const type = filtroLibro.value;

        if (!query) {
            cargarLibros();
            return;
        }

        try {
            const libros = await api.searchLibros(type, query);
            renderLibros(libros);
        } catch (error) {
            alert('Error al buscar libros: ' + error.message);
        }
    }

    function renderLibros(libros) {
        tablaLibros.innerHTML = '';
        libros.forEach(libro => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${libro.id}</td>
                <td>${libro.titulo}</td>
                <td>${libro.autor}</td>
                <td>$${libro.precio.toFixed(2)}</td>
                <td>${libro.stock}</td>
                <td>
                    <button class="btn-edit" data-id="${libro.id}">Editar</button>
                    <button class="btn-danger" data-id="${libro.id}">Eliminar</button>
                </td>
            `;
            tablaLibros.appendChild(tr);
        });

        // Configurar eventos de los botones
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', cargarLibroParaEditar);
        });

        document.querySelectorAll('.btn-danger').forEach(btn => {
            btn.addEventListener('click', eliminarLibro);
        });
    }

    async function cargarLibroParaEditar(e) {
        const id = e.target.getAttribute('data-id');
        try {
            const libro = await api.getLibro(id);
            document.getElementById('libro-id').value = libro.id;
            document.getElementById('libro-titulo').value = libro.titulo;
            document.getElementById('libro-autor').value = libro.autor;
            document.getElementById('libro-precio').value = libro.precio;
            document.getElementById('libro-stock').value = libro.stock;

            modalTitulo.textContent = 'Editar Libro';
            modalLibro.style.display = 'block';
        } catch (error) {
            alert('Error al cargar libro: ' + error.message);
        }
    }

    async function eliminarLibro(e) {
        if (!confirm('¿Estás seguro de eliminar este libro?')) return;

        const id = e.target.getAttribute('data-id');
        try {
            await api.deleteLibro(id);
            cargarLibros();
        } catch (error) {
            alert('Error al eliminar libro: ' + error.message);
        }
    }

    async function guardarLibro(e) {
        e.preventDefault();

        const libro = {
            titulo: document.getElementById('libro-titulo').value,
            autor: document.getElementById('libro-autor').value,
            precio: parseFloat(document.getElementById('libro-precio').value),
            stock: parseInt(document.getElementById('libro-stock').value)
        };

        const id = document.getElementById('libro-id').value;

        try {
            if (id) {
                await api.updateLibro(id, libro);
            } else {
                await api.createLibro(libro);
            }

            modalLibro.style.display = 'none';
            cargarLibros();
        } catch (error) {
            alert('Error al guardar libro: ' + error.message);
        }
    }
}