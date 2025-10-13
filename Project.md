

#  Project E-commerce Plan (React + Servlet/Tomcat + SQL Server)


###  Authentication

* Đăng ký (Register)
* Đăng nhập (Login)
* Đăng xuất (Logout)
* Quản lý session (cookie + JWT/token lưu ở client)
* Reset mật khẩu (optional)

###  User Management

* Xem và cập nhật profile (tên, email, địa chỉ, số điện thoại)
* Quản lý quyền (user, admin)
* Admin có thể khóa / mở user

### Product Management

* CRUD sản phẩm (Admin): thêm, sửa, xóa
* Upload hình ảnh sản phẩm
* Quản lý danh mục (Category)
* Hiển thị danh sách sản phẩm + chi tiết sản phẩm

###  Cart & Checkout

* Thêm sản phẩm vào giỏ hàng
* Cập nhật số lượng
* Xóa sản phẩm khỏi giỏ hàng
* Checkout (điền địa chỉ, chọn phương thức thanh toán)

###  Orders & Payments

* Tạo order khi checkout
* Quản lý trạng thái order (Pending, Paid, Shipped, Completed, Cancelled)
* Thanh toán (giả lập hoặc tích hợp cổng thanh toán như PayPal/MoMo)
* Lưu lịch sử đơn hàng

### Admin Dashboard

* Quản lý users
* Quản lý products
* Quản lý orders
* Thống kê (doanh thu, số đơn, top sản phẩm)

###  Search & Filter

* Tìm kiếm sản phẩm theo tên/mô tả
* Lọc theo category, giá, rating
