// Importar todos los módulos necesarios
import { initClientes } from './clientes.js';
import { initLibros } from './libros.js';
import { initVentas } from './ventas.js';

// Función para manejar la navegación
function setupNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const sectionId = link.getAttribute('data-section');

            // Actualizar navegación
            document.querySelectorAll('.nav-link').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.content-section').forEach(el => el.classList.remove('active'));

            link.classList.add('active');
            document.getElementById(sectionId).classList.add('active');
        });
    });
}

// Inicializar la aplicación
document.addEventListener('DOMContentLoaded', () => {
    setupNavigation();
    initClientes();
    initLibros();
    initVentas();
});