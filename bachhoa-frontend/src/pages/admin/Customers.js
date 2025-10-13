import { useEffect, useState } from 'react';
import axios from 'axios';

export default function Customers() {
  const [customers, setCustomers] = useState([]);

  useEffect(() => {
    axios.get('http://localhost:8080/api/customers')
      .then(res => setCustomers(res.data))
      .catch(err => console.error('Lỗi tải khách hàng:', err));
  }, []);

  return (
    <div>
      <h2>Danh sách khách hàng</h2>
      <table className="table">
        <thead><tr><th>ID</th><th>Tên</th><th>Email</th><th>SĐT</th><th>Ngày tạo</th></tr></thead>
        <tbody>
          {customers.map(c => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.name}</td>
              <td>{c.email}</td>
              <td>{c.phone}</td>
              <td>{new Date(c.createdAt).toLocaleDateString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
