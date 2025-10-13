import "./Dashboard.css";
import { FaBoxOpen, FaMoneyBillWave, FaClipboardList, FaUsers } from "react-icons/fa";

export default function Dashboard() {
  return (
    <div className="dashboard">
      <h2>Dashboard</h2>
      <div className="cards">
        <div className="card blue"><FaBoxOpen /> <p>Sản phẩm</p><h3>0</h3></div>
        <div className="card green"><FaMoneyBillWave /> <p>Doanh thu</p><h3>0.0</h3></div>
        <div className="card yellow"><FaClipboardList /> <p>Đơn hàng</p><h3>0</h3></div>
        <div className="card cyan"><FaUsers /> <p>Khách hàng</p><h3>0</h3></div>
      </div>

      <div className="charts">
        <div className="chart">Doanh thu (6 tháng)</div>
        <div className="chart">Đơn hàng theo trạng thái</div>
      </div>
    </div>
  );
}
