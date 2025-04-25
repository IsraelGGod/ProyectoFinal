/**
 * CONFIGURACIÓN PRINCIPAL
 * -----------------------------------
 * Ajusta estas constantes según tu API
 */
const CONFIG = {
    BASE_URL: "http://localhost:8080",    // URL de tu backend
    API_ENDPOINT: "/api/libros",          // Ruta base de tu API
    get FULL_API_URL() {
      return `${this.BASE_URL}${this.API_ENDPOINT}`;
    }
  };
  
  /**
   * ELEMENTOS DEL DOM
   * -----------------------------------
   * Todos los elementos HTML que interactúan con JS
   */
  const DOM = {
    // Tabla
    tableBody: document.querySelector("#data-table tbody"),
    
    // Modal y formulario
    modal: document.getElementById("modal"),
    modalTitle: document.getElementById("modal-title"),
    itemForm: document.getElementById("item-form"),
    itemIdInput: document.getElementById("item-id"),
    nameInput: document.getElementById("name"),
    descriptionInput: document.getElementById("description"),
    
    // Botones
    addBtn: document.getElementById("add-btn"),
    closeModal: document.querySelector(".close"),
    
    // Notificaciones
    toast: document.getElementById("toast"),
    loadingIndicator: document.createElement("div") // Spinner de carga
  };
  
  /**
   * ESTADO DE LA APLICACIÓN
   * -----------------------------------
   * Guarda información temporal durante el uso
   */
  const APP_STATE = {
    currentOperation: "crear", // 'crear' | 'editar'
    currentItemId: null
  };
  
  // ==============================================
  // FUNCIONES PRINCIPALES
  // ==============================================
  
  /**
   * Inicializa la aplicación
   */
  function initApp() {
    setupLoadingIndicator();
    setupEventListeners();
    loadData();
  }
  
  /**
   * Carga los libros desde la API
   */
  async function loadData() {
    showLoading(true);
    
    try {
      const response = await fetch(CONFIG.FULL_API_URL);
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${await response.text()}`);
      }
      
      const libros = await response.json();
      renderData(libros);
      
    } catch (error) {
      console.error("Error al cargar libros:", error);
      showToast(`Error al cargar libros: ${error.message}`, "error");
    } finally {
      showLoading(false);
    }
  }
  
  /**
   * Muestra los libros en la tabla
   */
  function renderData(libros) {
    DOM.tableBody.innerHTML = libros.map(libro => `
      <tr>
        <td>${libro.id || libro._id}</td>
        <td>${libro.titulo}</td>
        <td>${libro.descripcion || "Sin descripción"}</td>
        <td>
          <button class="btn btn-warning" onclick="handleEdit('${libro.id || libro._id}')">
            <i class="fas fa-edit"></i> Editar
          </button>
          <button class="btn btn-danger" onclick="handleDelete('${libro.id || libro._id}')">
            <i class="fas fa-trash"></i> Eliminar
          </button>
        </td>
      </tr>
    `).join("");
  }
  
  // ==============================================
  // OPERACIONES CRUD
  // ==============================================
  
  /**
   * Maneja el envío del formulario (Crear/Editar)
   */
  async function handleSubmit(e) {
    e.preventDefault();
    
    // Validaciones
    if (!DOM.nameInput.value.trim()) {
      showToast("El título es requerido", "warning");
      DOM.nameInput.focus();
      return;
    }
    
    const libroData = {
      titulo: DOM.nameInput.value.trim(),
      descripcion: DOM.descriptionInput.value.trim()
    };
    
    try {
      showLoading(true);
      
      const url = APP_STATE.currentOperation === "editar" 
        ? `${CONFIG.FULL_API_URL}/${APP_STATE.currentItemId}`
        : CONFIG.FULL_API_URL;
      
      const method = APP_STATE.currentOperation === "editar" ? "PUT" : "POST";
      
      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(libroData)
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error en la operación");
      }
      
      showToast(
        `Libro ${APP_STATE.currentOperation === "editar" ? "actualizado" : "creado"} correctamente`,
        "success"
      );
      
      closeModal();
      loadData();
      
    } catch (error) {
      console.error("Error al guardar:", error);
      showToast(`Error: ${error.message}`, "error");
    } finally {
      showLoading(false);
    }
  }
  
  /**
   * Prepara el formulario para editar
   */
  async function handleEdit(id) {
    try {
      showLoading(true);
      APP_STATE.currentOperation = "editar";
      APP_STATE.currentItemId = id;
      
      const response = await fetch(`${CONFIG.FULL_API_URL}/${id}`);
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${await response.text()}`);
      }
      
      const libro = await response.json();
      
      // Llenar formulario
      DOM.itemIdInput.value = id;
      DOM.nameInput.value = libro.titulo;
      DOM.descriptionInput.value = libro.descripcion || "";
      DOM.modalTitle.innerHTML = `<i class="fas fa-edit"></i> Editar Libro`;
      
      openModal();
      
    } catch (error) {
      console.error("Error al cargar libro:", error);
      showToast(`Error: ${error.message}`, "error");
    } finally {
      showLoading(false);
    }
  }
  
  /**
   * Elimina un libro
   */
  async function handleDelete(id) {
    if (!confirm("¿Estás seguro de eliminar este libro?")) return;
    
    try {
      showLoading(true);
      
      const response = await fetch(`${CONFIG.FULL_API_URL}/${id}`, {
        method: "DELETE"
      });
      
      if (!response.ok) {
        throw new Error(`Error ${response.status}: ${await response.text()}`);
      }
      
      showToast("Libro eliminado correctamente", "success");
      loadData();
      
    } catch (error) {
      console.error("Error al eliminar:", error);
      showToast(`Error: ${error.message}`, "error");
    } finally {
      showLoading(false);
    }
  }
  
  // ==============================================
  // FUNCIONES DE INTERFAZ
  // ==============================================
  
  /**
   * Configura el indicador de carga
   */
  function setupLoadingIndicator() {
    DOM.loadingIndicator.className = "loading-indicator";
    DOM.loadingIndicator.innerHTML = `
      <div class="spinner"></div>
      <p>Cargando...</p>
    `;
    document.body.appendChild(DOM.loadingIndicator);
  }
  
  /**
   * Muestra/oculta el estado de carga
   */
  function showLoading(show) {
    DOM.loadingIndicator.style.display = show ? "flex" : "none";
    document.body.style.cursor = show ? "wait" : "default";
  }
  
  /**
   * Muestra notificaciones
   */
  function showToast(message, type = "success") {
    const iconMap = {
      success: "check-circle",
      error: "times-circle",
      warning: "exclamation-circle"
    };
    
    DOM.toast.innerHTML = `
      <i class="fas fa-${iconMap[type] || "info-circle"}"></i>
      ${message}
    `;
    
    DOM.toast.className = `toast ${type}`;
    DOM.toast.style.visibility = "visible";
    
    setTimeout(() => {
      DOM.toast.style.visibility = "hidden";
    }, 3000);
  }
  
  /**
   * Abre/cierra el modal
   */
  function openModal() {
    DOM.modal.style.display = "flex";
    document.body.style.overflow = "hidden";
  }
  
  function closeModal() {
    DOM.modal.style.display = "none";
    document.body.style.overflow = "auto";
    DOM.itemForm.reset();
    APP_STATE.currentOperation = "crear";
    APP_STATE.currentItemId = null;
  }
  
  // ==============================================
  // CONFIGURACIÓN DE EVENTOS
  // ==============================================
  
  function setupEventListeners() {
    // Botón "Nuevo Libro"
    DOM.addBtn.addEventListener("click", () => {
      APP_STATE.currentOperation = "crear";
      DOM.modalTitle.innerHTML = `<i class="fas fa-plus"></i> Nuevo Libro`;
      openModal();
    });
    
    // Cerrar modal
    DOM.closeModal.addEventListener("click", closeModal);
    
    // Cerrar al hacer clic fuera del modal
    DOM.modal.addEventListener("click", (e) => {
      if (e.target === DOM.modal) closeModal();
    });
    
    // Enviar formulario
    DOM.itemForm.addEventListener("submit", handleSubmit);
  }
  
  // ==============================================
  // INICIALIZACIÓN
  // ==============================================
  
  // Hacer funciones disponibles globalmente
  window.handleEdit = handleEdit;
  window.handleDelete = handleDelete;
  
  // Iniciar la aplicación cuando el DOM esté listo
  document.addEventListener("DOMContentLoaded", initApp);