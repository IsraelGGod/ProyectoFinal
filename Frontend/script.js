// Variables globales
let clientes = [];
let libros = [];
let carrito = [];
let editando = false;

// DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  cargarClientes();
  cargarLibros();
  setupTabs();
  setupForms();
});

// Configurar pestañas
function setupTabs() {
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

      btn.classList.add('active');
      document.getElementById(btn.dataset.tab).classList.add('active');
    });
  });
}

// ========== CLIENTES ==========
async function cargarClientes() {
  try {
    const response = await fetch('http://localhost:8080/api/clientes');
    clientes = await response.json();
    renderClientes();
    renderClienteOptions();
  } catch (error) {
    console.error('Error cargando clientes:', error);
  }
}

function renderClientes() {
  const tbody = document.querySelector('#tabla-clientes tbody');
  tbody.innerHTML = clientes.map(cliente => `
    <tr>
      <td>${cliente.nombre}</td>
      <td>${cliente.email}</td>
      <td>${cliente.matricula || 'N/A'}</td>
      <td>
        <button onclick="editarCliente(${cliente.id})">✏️</button>
        <button onclick="eliminarCliente(${cliente.id})">🗑️</button>
      </td>
    </tr>
  `).join('');
}

function renderClienteOptions() {
  const select = document.getElementById('cliente-venta');
  select.innerHTML = '<option value="">Seleccione cliente</option>' +
      clientes.map(c => `<option value="${c.id}">${c.nombre} (${c.email})</option>`).join('');
}

// CRUD Clientes
async function guardarCliente(e) {
  e.preventDefault();
  const cliente = {
    id: document.getElementById('cliente-id').value || null,
    nombre: document.getElementById('cliente-nombre').value,
    email: document.getElementById('cliente-email').value,
    telefono: document.getElementById('cliente-telefono').value,
    matricula: document.getElementById('cliente-matricula').value
  };

  const method = cliente.id ? 'PUT' : 'POST';
  const url = cliente.id ? `http://localhost:8080/api/clientes/${cliente.id}` : 'http://localhost:8080/api/clientes';

  try {
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente)
    });

    if (!response.ok) throw new Error(await response.text());
    cargarClientes();
    resetForm('form-cliente');
  } catch (error) {
    alert('Error: ' + error.message);
  }
}

function editarCliente(id) {
  const cliente = clientes.find(c => c.id === id);
  if (!cliente) return;

  editando = true;
  document.getElementById('cliente-id').value = cliente.id;
  document.getElementById('cliente-nombre').value = cliente.nombre;
  document.getElementById('cliente-email').value = cliente.email;
  document.getElementById('cliente-telefono').value = cliente.telefono || '';
  document.getElementById('cliente-matricula').value = cliente.matricula || '';
}

async function eliminarCliente(id) {
  if (!confirm('¿Eliminar este cliente?')) return;

  try {
    await fetch(`http://localhost:8080/api/clientes/${id}`, { method: 'DELETE' });
    cargarClientes();
  } catch (error) {
    alert('Error eliminando cliente: ' + error.message);
  }
}

// ========== LIBROS ==========
async function cargarLibros() {
  try {
    const response = await fetch('http://localhost:8080/api/libros');
    libros = await response.json();
    renderLibros();
  } catch (error) {
    console.error('Error cargando libros:', error);
  }
}

function renderLibros() {
  const container = document.getElementById('lista-libros');
  container.innerHTML = libros.map(libro => `
    <div class="libro-card">
      <h3>${libro.titulo}</h3>
      <p>Autor: ${libro.autor}</p>
      <p>Precio: $${libro.precio.toFixed(2)}</p>
      <p>Stock: ${libro.stock}</p>
      <button onclick="editarLibro(${libro.id})">Editar</button>
      <button onclick="eliminarLibro(${libro.id})">Eliminar</button>
      <button onclick="agregarAlCarrito(${libro.id})">Vender</button>
    </div>
  `).join('');
}

// CRUD Libros (similar a clientes)
// ... (implementar guardarLibro, editarLibro, eliminarLibro)

// ========== CARRITO ==========
function agregarAlCarrito(libroId) {
  const libro = libros.find(l => l.id === libroId);
  if (!libro) return;

  const cantidad = prompt(`Cantidad de "${libro.titulo}" (Stock: ${libro.stock}):`, 1);
  if (!cantidad || isNaN(cantidad) || cantidad <= 0) return;

  const item = {
    libroId: libro.id,
    titulo: libro.titulo,
    precio: libro.precio,
    cantidad: parseInt(cantidad),
    subtotal: libro.precio * parseInt(cantidad)
  };

  carrito.push(item);
  renderCarrito();
}

function renderCarrito() {
  const lista = document.getElementById('items-carrito');
  lista.innerHTML = carrito.map(item => `
    <li>
      ${item.cantidad}x ${item.titulo} - $${item.precio.toFixed(2)} 
      ($${item.subtotal.toFixed(2)})
      <button onclick="removerDelCarrito(${item.libroId})">❌</button>
    </li>
  `).join('');

  calcularTotal();
}

function removerDelCarrito(libroId) {
  carrito = carrito.filter(item => item.libroId !== libroId);
  renderCarrito();
}

function calcularTotal() {
  const subtotal = carrito.reduce((sum, item) => sum + item.subtotal, 0);
  const descuento = parseFloat(document.getElementById('descuento').value) || 0;
  const total = subtotal * (1 - descuento / 100);

  document.getElementById('subtotal').textContent = subtotal.toFixed(2);
  document.getElementById('total-venta').textContent = total.toFixed(2);
}

// Finalizar venta
document.getElementById('finalizar-venta').addEventListener('click', async () => {
  const clienteId = document.getElementById('cliente-venta').value;
  if (!clienteId) return alert('Seleccione un cliente');
  if (carrito.length === 0) return alert('Carrito vacío');

  const venta = {
    clienteId: parseInt(clienteId),
    descuento: parseFloat(document.getElementById('descuento').value) || 0,
    detalles: carrito.map(item => ({
      libroId: item.libroId,
      cantidad: item.cantidad
    }))
  };

  try {
    const response = await fetch('http://localhost:8080/api/ventas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(venta)
    });

    const ticket = await response.json();
    mostrarTicket(ticket);
    carrito = [];
    renderCarrito();
    cargarLibros(); // Actualizar stock
  } catch (error) {
    alert('Error en venta: ' + error.message);
  }
});

function mostrarTicket(ticket) {
  const ticketDiv = document.getElementById('ticket-preview');
  ticketDiv.innerHTML = `
    <h3>Ticket #${ticket.numeroTicket}</h3>
    <p>Fecha: ${new Date(ticket.fecha).toLocaleString()}</p>
    <p>Cliente: ${ticket.clienteNombre}</p>
    <hr>
    ${ticket.items.map(item => `
      <p>${item.cantidad}x ${item.tituloLibro} - $${item.precioUnitario.toFixed(2)}</p>
    `).join('')}
    <hr>
    <p>Subtotal: $${ticket.subtotal.toFixed(2)}</p>
    <p>Descuento: ${ticket.descuento}%</p>
    <p><strong>Total: $${ticket.total.toFixed(2)}</strong></p>
  `;
}

// Helpers
function resetForm(formId) {
  document.getElementById(formId).reset();
  document.getElementById(formId.replace('form-', '') + '-id').value = '';
  editando = false;
}

function setupForms() {
  document.getElementById('form-cliente').addEventListener('submit', guardarCliente);
  document.getElementById('cancelar-cliente').addEventListener('click', () => resetForm('form-cliente'));
  document.getElementById('descuento').addEventListener('change', calcularTotal);
}