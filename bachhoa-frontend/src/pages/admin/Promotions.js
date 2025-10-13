import { useEffect, useState } from 'react';
import axios from 'axios';

export default function Promotions() {
  const [promotions, setPromotions] = useState([]);

  useEffect(() => {
    axios.get('http://localhost:8080/api/promotions')
      .then(res => setPromotions(res.data))
      .catch(err => console.error('Lỗi tải khuyến mãi:', err));
  }, []);

  return (
    <div>
      <h2>Danh sách khuyến mãi</h2>
      <table className="table">
        <thead><tr><th>ID</th><th>Sản phẩm</th><th>Giá gốc</th><th>Giá KM</th><th>Từ</th><th>Đến</th></tr></thead>
        <tbody>
          {promotions.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.product?.name}</td>
              <td>{p.originalPrice}</td>
              <td>{p.discountPrice}</td>
              <td>{p.startDate}</td>
              <td>{p.endDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
