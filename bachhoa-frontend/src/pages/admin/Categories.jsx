import {useEffect, useState} from 'react';
import api from '../api/api';

export default function Categories(){
  const [categories, setCategories] = useState([]);

  useEffect(()=> { load(); }, []);

  function load(){
    api.get('/categories')
      .then(res => setCategories(res.data))
      .catch(err => console.error(err));
  }

  function remove(id){
    if(!window.confirm('Xóa danh mục?')) return;
    api.delete(`/categories/${id}`)
      .then(()=> load())
      .catch(err => alert('Xóa thất bại'));
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4>Danh mục</h4>
        <a className="btn btn-primary" href="/admin/categories/add">+ Thêm</a>
      </div>

      <div className="card">
        <div className="card-body">
          <table className="table table-hover">
            <thead><tr><th>ID</th><th>Tên</th><th>Mô tả</th><th>Hành động</th></tr></thead>
            <tbody>
              {categories.map(c=>(
                <tr key={c.id}>
                  <td>{c.id}</td>
                  <td>{c.name}</td>
                  <td>{c.description}</td>
                  <td>
                    <a className="btn btn-sm btn-outline-secondary me-2" href={`/admin/categories/edit/${c.id}`}>Sửa</a>
                    <button className="btn btn-sm btn-danger" onClick={()=>remove(c.id)}>Xóa</button>
                  </td>
                </tr>
              ))}
              {categories.length===0 && <tr><td colSpan="4" className="text-center text-muted">Không có dữ liệu</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
