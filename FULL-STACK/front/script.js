const apiUrl = 'http://localhost:8080/api/v1/products';

document.addEventListener('DOMContentLoaded', () => {
    fetchProducts();
    document.getElementById('product-form').addEventListener('submit', handleFormSubmit);
    document.getElementById('product-table').addEventListener('click', handleTableClick);
    document.getElementById('btn-cancel').addEventListener('click', resetForm);
});

function fetchProducts() {
    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const productTable = document.getElementById('product-table');
            productTable.innerHTML = '';
            data.forEach(product => {
                const row = document.createElement('tr');
                const categoryName = product.category ? product.category.name : 'Sin Categoría';

                row.innerHTML = `
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>$${product.price.toFixed(2)}</td>
                <td>${product.quantity}</td>
                <td>${product.description}</td>
                <td><span class="badge badge-info">${categoryName}</span></td>
                <td>
                    <button class="btn btn-warning btn-sm edit-btn" data-id="${product.id}">Editar</button>
                    <button class="btn btn-danger btn-sm delete-btn" data-id="${product.id}">Eliminar</button>
                </td>
            `;
                productTable.appendChild(row);  
            });
        })
        .catch(error => console.error('Error al obtener productos:', error));
}

function addProduct(product) {
    fetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(product)
    })
        .then(response => {
            if (!response.ok) throw new Error('Error al guardar');
            return response.json();
        })
        .then(() => {
            fetchProducts();
            resetForm();
        })
        .catch(error => console.error('Error:', error));
}

function updateProduct(id, product) {
    fetch(`${apiUrl}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(product)
    })
        .then(response => {
            if (!response.ok) throw new Error('Error al actualizar');
            return response.json();
        })
        .then(() => {
            fetchProducts();
            resetForm();
        })
        .catch(error => console.error('Error:', error));
}

function deleteProduct(id) {
    if (confirm('¿Desea eliminar este producto?')) {
        fetch(`${apiUrl}/${id}`, { method: 'DELETE' })
            .then(response => {
                if (response.status === 204 || response.ok) {
                    fetchProducts();
                }
            })
            .catch(error => console.error('Error al eliminar:', error));
    }
}

function handleFormSubmit(event) {
    event.preventDefault();
    const id = document.getElementById('product-id').value;
    const product = {
        name: document.getElementById('name').value,
        price: parseFloat(document.getElementById('price').value),
        quantity: parseInt(document.getElementById('quantity').value),
        description: document.getElementById('description').value,
        categoryId: parseInt(document.getElementById('categoryId').value)
    };

    if (id) {
        updateProduct(id, product);
    } else {
        addProduct(product);
    }
}

function handleTableClick(event) {
    const id = event.target.dataset.id;

    if (event.target.classList.contains('edit-btn')) {
        fetch(`${apiUrl}/${id}`)
            .then(response => response.json())
            .then(product => {
                document.getElementById('product-id').value = product.id;
                document.getElementById('name').value = product.name;
                document.getElementById('price').value = product.price;
                document.getElementById('quantity').value = product.quantity;
                document.getElementById('description').value = product.description;

                document.getElementById('form-title').innerText = 'Editar Producto';
                document.getElementById('btn-submit').innerText = 'Actualizar';
                document.getElementById('btn-cancel').classList.remove('d-none');
            });
    }

    if (event.target.classList.contains('delete-btn')) {
        deleteProduct(id);
    }
}

function resetForm() {
    document.getElementById('product-form').reset();
    document.getElementById('product-id').value = '';
    document.getElementById('form-title').innerText = 'Agregar Producto';
    document.getElementById('btn-submit').innerText = 'Guardar';
    document.getElementById('btn-cancel').classList.add('d-none');
}