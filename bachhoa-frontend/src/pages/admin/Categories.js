import React, { useState, useEffect } from "react";
import axios from 'axios';

export default function Categories() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    axios.get('http://localhost:8080/api/categories')
      .then(res => setCategories(res.data))
      .catch(err => console.error('Lỗi tải danh mục:', err));
  }, []);

  return (
    <div>
      <h2>Danh mục sản phẩm</h2>
      <table className="table">
        <thead><tr><th>ID</th><th>Tên</th><th>Mô tả</th></tr></thead>
        <tbody>
          {categories.map(c => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.name}</td>
              <td>{c.description}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
