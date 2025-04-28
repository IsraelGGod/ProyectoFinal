const API_BASE_URL = 'http://localhost:8080/api';

// Funciones genéricas para llamadas API
async function fetchData(endpoint, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
        },
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error en la solicitud:', error);
        throw error;
    }
}

// Exportar todas las funciones de la API
export const api = {
    // Clientes
    getClientes: () => fetchData('/clientes'),
    getCliente: (id) => fetchData(`/clientes/${id}`),
    createCliente: (cliente) => fetchData('/clientes', 'POST', cliente),
    updateCliente: (id, cliente) => fetchData(`/clientes/${id}`, 'PUT', cliente),
    deleteCliente: (id) => fetchData(`/clientes/${id}`, 'DELETE'),
    searchClientes: (query) => fetchData(`/clientes/buscar?matricula=${query}`),

    // Libros
    getLibros: () => fetchData('/libros'),
    getLibro: (id) => fetchData(`/libros/${id}`),
    createLibro: (libro) => fetchData('/libros', 'POST', libro),
    updateLibro: (id, libro) => fetchData(`/libros/${id}`, 'PUT', libro),
    deleteLibro: (id) => fetchData(`/libros/${id}`, 'DELETE'),
    updateStock: (id, cantidad) => fetchData(`/libros/${id}/stock?cantidad=${cantidad}`, 'PATCH'),
    searchLibros: (type, query) => fetchData(`/libros?${type}=${query}`),

    // Ventas
    getVentas: () => fetchData('/ventas'),
    getVenta: (id) => fetchData(`/ventas/${id}`),
    createVenta: (venta) => fetchData('/ventas', 'POST', venta),
    getDetallesVenta: (ventaId) => fetchData(`/detalle-venta/venta/${ventaId}`),
};