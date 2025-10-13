<!--
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Test Thêm/Xóa/Sửa Sản Phẩm</title>
    <meta charset="UTF-8">
    <script>
        // Lấy danh sách sản phẩm
        function fetchProducts() {
            fetch('api/products')
                .then(res => res.json())
                .then(data => {
                    console.log('API Response:', data); // Debug log

                    // Kiểm tra cấu trúc dữ liệu trả về
                    const list = data.data || data || [];
                    console.log('Product List:', list); // Debug log

                    let html = '';
                    if (Array.isArray(list) && list.length > 0) {
                        list.forEach((p, index) => {
                            console.log(`Product ${index}:`, p); // Debug từng product

                            // Sử dụng cách đơn giản hơn để tránh lỗi
                            const productId = p.productId || 0;
                            const name = p.name || 'N/A';
                            const basePrice = p.basePrice || 0;
                            const sku = p.sku || 'N/A';
                            const categoryName = p.categoryName || 'N/A';
                            const supplierName = p.supplierName || 'N/A';

                            html += '<tr>';
                            html += '<td>' + productId + '</td>';
                            html += '<td>' + name + '</td>';
                            html += '<td>' + basePrice + '</td>';
                            html += '<td>' + sku + '</td>';
                            html += '<td>' + categoryName + '</td>';
                            html += '<td>' + supplierName + '</td>';
                            html += '<td>';
                            html += '<button onclick="showEditSimple(' + productId + ')">Sửa</button> ';
                            html += '<button onclick="deleteProduct(' + productId + ')">Xóa</button>';
                            html += '</td>';
                            html += '</tr>';
                        });
                        console.log('Generated HTML length:', html.length); // Debug HTML
                    } else {
                        console.error('Data is not an array or empty:', list);
                        html = '<tr><td colspan="7">Không có dữ liệu</td></tr>';
                    }

                    const tableBody = document.getElementById('productBody');
                    if (tableBody) {
                        tableBody.innerHTML = html;
                        console.log('HTML inserted into table successfully');
                    } else {
                        console.error('productBody element not found');
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                    const tableBody = document.getElementById('productBody');
                    if (tableBody) {
                        tableBody.innerHTML = '<tr><td colspan="7">Lỗi khi tải dữ liệu: ' + error.message + '</td></tr>';
                    }
                });
        }

        // Thêm sản phẩm
        function addProduct() {
            const name = document.getElementById('addName').value.trim();
            const basePrice = parseFloat(document.getElementById('addPrice').value) || 0;
            const sku = document.getElementById('addSku').value.trim();
            const description = document.getElementById('addDescription').value.trim();
            const categoryId = parseInt(document.getElementById('addCategoryId').value) || 0;
            const supplierId = parseInt(document.getElementById('addSupplierId').value) || 0;

            // Validation
            if (!name || !sku || !categoryId || !supplierId) {
                alert('Vui lòng nhập đầy đủ: Tên, SKU, Category ID, Supplier ID');
                return;
            }

            console.log('Adding product:', {name, basePrice, sku, description, categoryId, supplierId}); // Debug

            fetch('api/products', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({name, basePrice, sku, description, categoryId, supplierId})
            })
            .then(res => res.json())
            .then(data => {
                console.log('Add product response:', data); // Debug
                if (data.error) {
                    alert('Lỗi: ' + data.error);
                } else {
                    alert('Thêm thành công!');
                    // Clear form
                    document.getElementById('addName').value = '';
                    document.getElementById('addPrice').value = '';
                    document.getElementById('addSku').value = '';
                    document.getElementById('addDescription').value = '';
                    document.getElementById('addCategoryId').value = '';
                    document.getElementById('addSupplierId').value = '';
                    // Refresh list
                    fetchProducts();
                }
            })
            .catch(error => {
                console.error('Add product error:', error);
                alert('Lỗi khi thêm sản phẩm: ' + error.message);
            });
        }

        // Hiện form sửa - phiên bản đơn giản
        function showEditSimple(productId) {
            // Tìm sản phẩm trong dữ liệu đã load
            fetch('api/products/' + productId)
                .then(res => res.json())
                .then(data => {
                    const product = data.data || data;
                    if (product) {
                        document.getElementById('editId').value = product.productId || '';
                        document.getElementById('editName').value = product.name || '';
                        document.getElementById('editPrice').value = product.basePrice || '';
                        document.getElementById('editSku').value = product.sku || '';
                        document.getElementById('editCategoryName').value = product.categoryName || '';
                        document.getElementById('editSupplierName').value = product.supplierName || '';
                        document.getElementById('editForm').style.display = 'block';
                    }
                })
                .catch(error => {
                    console.error('Error loading product for edit:', error);
                    alert('Lỗi khi tải thông tin sản phẩm');
                });
        }

        // Hiện form sửa - giữ lại để tương thích
        function showEdit(id, name, basePrice, sku, categoryName, supplierName) {
            showEditSimple(id);
        }

        // Sửa sản phẩm
        function editProduct() {
            const id = document.getElementById('editId').value;
            const name = document.getElementById('editName').value.trim();
            const basePrice = parseFloat(document.getElementById('editPrice').value) || 0;
            const sku = document.getElementById('editSku').value.trim();

            if (!name || !sku) {
                alert('Vui lòng nhập đầy đủ tên và SKU');
                return;
            }

            fetch('api/products/' + id, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({name, basePrice, sku})
            })
            .then(res => res.json())
            .then(data => {
                console.log('Edit product response:', data); // Debug
                if (data.error) {
                    alert('Lỗi: ' + data.error);
                } else {
                    alert('Sửa thành công!');
                    document.getElementById('editForm').style.display = 'none';
                    fetchProducts();
                }
            })
            .catch(error => {
                console.error('Edit product error:', error);
                alert('Lỗi khi sửa sản phẩm: ' + error.message);
            });
        }

        // Xóa sản phẩm
        function deleteProduct(id) {
            if (!confirm('Bạn có chắc muốn xóa sản phẩm ID ' + id + '?')) return;

            fetch('api/products/' + id, {method: 'DELETE'})
                .then(response => {
                    console.log('Delete response status:', response.status); // Debug
                    if (response.status === 204 || response.ok) {
                        alert('Xóa thành công!');
                        fetchProducts();
                    } else {
                        return response.json().then(data => {
                            alert('Lỗi khi xóa: ' + (data.error || 'Unknown error'));
                        });
                    }
                })
                .catch(error => {
                    console.error('Delete product error:', error);
                    alert('Lỗi khi xóa sản phẩm: ' + error.message);
                });
        }

        // Load khi trang sẵn sàng
        window.onload = function() {
            console.log('Page loaded, fetching products...');
            fetchProducts();
        };
    </script>
</head>
<body>
    <h2>Thêm sản phẩm</h2>
    <div style="margin-bottom: 20px;">
        <input id="addName" placeholder="Tên sản phẩm *" style="width: 200px;" />
        <input id="addPrice" type="number" placeholder="Giá *" style="width: 100px;" />
        <input id="addSku" placeholder="SKU *" style="width: 120px;" />
        <input id="addDescription" placeholder="Mô tả" style="width: 300px;" />
        <br><br>
        <input id="addCategoryId" type="number" placeholder="Category ID (1-5) *" style="width: 150px;" />
        <input id="addSupplierId" type="number" placeholder="Supplier ID (1-10) *" style="width: 150px;" />
        <button onclick="addProduct()" style="padding: 5px 15px;">Thêm</button>
    </div>

    <h2>Danh sách sản phẩm</h2>
    <table border="1" style="width: 100%; border-collapse: collapse;">
        <thead>
            <tr style="background-color: #f0f0f0;">
                <th>ID</th>
                <th>Tên</th>
                <th>Giá</th>
                <th>SKU</th>
                <th>Category</th>
                <th>Supplier</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody id="productBody">
            <tr><td colspan="7">Đang tải...</td></tr>
        </tbody>
    </table>

    <div id="editForm" style="display:none; margin-top: 20px; padding: 20px; border: 1px solid #ccc; background-color: #f9f9f9;">
        <h3>Sửa sản phẩm</h3>
        <input id="editId" type="hidden" />
        <input id="editName" placeholder="Tên sản phẩm" style="width: 200px;" />
        <input id="editPrice" type="number" placeholder="Giá" style="width: 100px;" />
        <input id="editSku" placeholder="SKU" style="width: 120px;" />
        <br><br>
        <input id="editCategoryName" placeholder="Category" style="width: 150px;" disabled />
        <input id="editSupplierName" placeholder="Supplier" style="width: 150px;" disabled />
        <br><br>
        <button onclick="editProduct()">Lưu</button>
        <button onclick="document.getElementById('editForm').style.display='none'">Hủy</button>
    </div>

    <div style="margin-top: 20px; padding: 10px; background-color: #fff3cd; border: 1px solid #ffeaa7;">
        <p><strong>Hướng dẫn:</strong></p>
        <ul>
            <li>Mở F12 > Console để xem log debug</li>
            <li>Category ID: 1=Thịt, 2=Rau củ, 3=Đông lạnh, 4=Gia vị, 5=Đóng gói</li>
            <li>Supplier ID: 1-10 (xem trong bảng sản phẩm có sẵn)</li>
        </ul>
    </div>
</body>
</html>

-->