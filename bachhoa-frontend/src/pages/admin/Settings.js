export default function Settings() {
  return (
    <div>
      <h2>Cấu hình hệ thống</h2>
      <form className="settings-form">
        <div className="form-group">
          <label>Tên cửa hàng</label>
          <input className="form-control" defaultValue="Bách Hóa Online" />
        </div>
        <div className="form-row">
          <div className="form-group col-md-6">
            <label>Email</label>
            <input className="form-control" defaultValue="admin@bachhoa.vn" />
          </div>
          <div className="form-group col-md-6">
            <label>Số điện thoại</label>
            <input className="form-control" defaultValue="0123456789" />
          </div>
        </div>
        <div className="form-group">
          <label>Địa chỉ</label>
          <input className="form-control" defaultValue="TP. Hồ Chí Minh" />
        </div>
        <button className="btn btn-primary">Lưu thay đổi</button>
      </form>
    </div>
  );
}
