// ++++++ AQUI GUARDAMOS LAS VARIABLES GLOBALES +++++++++++
let selectedFiles = []; //Aqui guardo los archivos seleccionados

// ++++++ REFERENCIAS A ELEMENTOS DEL DOM ++++++++
const dropZone = document.getElementById('dropZone');
const fileInput = document.getElementById('fileInput');
const fileList = document.getElementById('fileList');
const fileNames = document.getElementById('fileNames');
const processBtn = document.getElementById('processBtn');
const clearBtn = document.getElementById('clearBtn');

// +++++ MENSAJE DE CONFIRMACION +++++++
console.log('Script cargado correctamente ✅');

// +++++ SELECCIONAR ARCHIVOS CON EL INPUT +++++
fileInput.addEventListener('change', function(e) { //escucha el evento de fileInput, change realiza la accion de cambio de valor (input de archivo)
    handleFiles(e.target.files);
})


// ++++++ MANEJAR ARCHIVOS SELECCIONADOS +++++++
function handleFiles(files){
    selectedFiles = Array.from(files);
    
    displayFileList();

    processBtn.disabled = false;
    clearBtn.disabled = false;

    console.log('Archivos seleccionados', selectedFiles);
}

//+++++++ MOSTRAR LISTA DE ARCHIVOS ++++++++++
function displayFileList(){

    fileNames.innerHTML = '';

    if(selectedFiles.length > 0){
        fileList.style.display = 'block';
        
        // Agregar cada archivo a la lista
        selectedFiles.forEach((file, index) => {
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            li.innerHTML = `
                <span>
                    <i class="bi bi-file-earmark-text text-primary"></i> 
                    ${file.name}
                </span>
                <span class="badge bg-secondary">${(file.size / 1024).toFixed(2)} KB</span>
            `;
            fileNames.appendChild(li);
        });
    } else {
        fileList.style.display = 'none';
    }
}

// ++++++ PREVENIR COMPORTAMIENTO POR DEFECTO DEL NAVEGADOR ++++++++
['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
    dropZone.addEventListener(eventName, preventDefaults, false);
});

function preventDefaults(e) {
    e.preventDefault();
    e.stopPropagation();
}

// ++++++++ EFECTOS VISUALES CUANDO ARRASTRAS SOBRE LA ZONA +++++++
['dragenter', 'dragover'].forEach(eventName => {
    dropZone.addEventListener(eventName, () => {
        dropZone.classList.add('drag-over');
    });
});

['dragleave', 'drop'].forEach(eventName => {
    dropZone.addEventListener(eventName, () => {
        dropZone.classList.remove('drag-over');
    });
});


// +++++++ CUANDO SUELTE LOS ARCHIVOS ++++++++
dropZone.addEventListener('drop', function(e) {
    const files = e.dataTransfer.files;
    handleFiles(files);
});


// +++++++ EVENTO: BOTON DE LIMPIAR +++++++++++
clearBtn.addEventListener('click', function() {
    // Limpiar array de archivos
    selectedFiles = [];
    
    // Limpiar el input
    fileInput.value = '';
    
    // Ocultar la lista
    fileList.style.display = 'none';
    
    // Desactivar botones
    processBtn.disabled = true;
    clearBtn.disabled = true;
    
    // Ocultar secciones de resultados
    document.getElementById('salesSection').style.display = 'none';
    document.getElementById('errorsSection').style.display = 'none';
    document.getElementById('summarySection').style.display = 'none';
    
    console.log('Archivos limpiados ✅');
});

// +++++++++++++++++++++ EVENTO: Boton de Procesar los archivos ++++++++++++++
processBtn.addEventListener('click', async function(){
    //Validar que haya archivos
    if(selectedFiles.length === 0){
        alert('Por favor selecciona al menos un archivo');
        return;
    }

    //Deshabilitar boton mientras procesa
    processBtn.disabled = true;
    processBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Procesando...';

    try{
        //Crear FormData para enviar Archivos
        const formData = new FormData();
        selectedFiles.forEach(file => {
            formData.append('files', file);
        });


        //Enviar al backend
        //Con la direccion del ENDPOINT POST
        const response = await fetch('http://localhost:8080/api/ventas/procesar', { method: 'POST', body: formData});

        //verificar respuesta
        if(!response.ok){
            throw new Error('Error al procesar archivos');
        }

        //Obtener datos del backend
        const data = await response.json();

        //Mostrar resultados
        displayResults(data);

    }catch (error){
        console.error('Error', error);
        alert('Hubo un error al procesar los archivos. Verifica que el backend esta corriendo.')
    
    }finally{
        //Restaurar boton
        processBtn.innerHTML = '<i class="bi bi-gear-fill"></i> Procesar Archivos';
        processBtn.disabled = false;
    }
});

// +++++ Mostrar resultados en las tablas ++++++
function displayResults(data){
    //Mostrar seccion de ventas
    displaySalesTable(data.ventasPorVendedor);

    //Mostrar seccion de errores
    displayErrorsTable(data.errores);

    //Mostrar resumen
    displaySummary(data);
}


// +++++++ Llenar tabla de ventas +++++++++
function displaySalesTable(ventas){
    const salesTableBody = document.getElementById('salesTableBody');
    salesTableBody.innerHTML = '';
    
    if (ventas && ventas.length > 0) {
        ventas.forEach((venta, index) => {
            const row = `
                <tr>
                    <td>${index + 1}</td>
                    <td><strong>${venta.vendedor}</strong></td>
                    <td class="text-success fw-bold">$${venta.totalVentas.toFixed(2)}</td>
                </tr>
            `;
            salesTableBody.innerHTML += row;
        });
        
        // Mostrar la sección
        document.getElementById('salesSection').style.display = 'block';
    }
}

// +++++++++ Funcion llenar tabla de errores ++++++++++
function displayErrorsTable(errores) {
    const errorsTableBody = document.getElementById('errorsTableBody');
    errorsTableBody.innerHTML = '';
    
    if (errores && errores.length > 0) {
        errores.forEach((error, index) => {
            const row = `
                <tr>
                    <td>${index + 1}</td>
                    <td><i class="bi bi-file-earmark-x"></i> ${error.archivo}</td>
                    <td>${error.fila}</td>
                    <td class="text-danger">${error.mensaje}</td>
                </tr>
            `;
            errorsTableBody.innerHTML += row;
        });
        
        // Mostrar la sección
        document.getElementById('errorsSection').style.display = 'block';
    } else {
        // Si no hay errores, mostrar mensaje
        errorsTableBody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-success">
                    <i class="bi bi-check-circle-fill"></i> No se encontraron errores
                </td>
            </tr>
        `;
        document.getElementById('errorsSection').style.display = 'block';
    }
}

// +++++ FUNCION: Mostrar resumen general +++++++++
function displaySummary(data) {
    // Calcular totales
    let totalVentas = 0;
    if (data.ventasPorVendedor) {
        data.ventasPorVendedor.forEach(venta => {
            totalVentas += venta.totalVentas;
        });
    }
    
    const totalVendedores = data.ventasPorVendedor ? data.ventasPorVendedor.length : 0;
    const totalErrores = data.errores ? data.errores.length : 0;
    
    // Actualizar valores en el HTML
    document.getElementById('totalSales').textContent = `$${totalVentas.toFixed(2)}`;
    document.getElementById('totalVendors').textContent = totalVendedores;
    document.getElementById('totalErrors').textContent = totalErrores;
    
    // Mostrar la sección
    document.getElementById('summarySection').style.display = 'block';
}