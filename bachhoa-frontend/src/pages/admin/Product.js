import React, { useEffect, useState } from "react";
import axios from "axios";
import { FaSearch } from "react-icons/fa";

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");

  // Lấy danh sách sản phẩm từ backend
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/products")
      .then((res) => setProducts(res.data))
      .catch((err) => console.error(err));
  }, []);

  // Tìm kiếm theo tất cả trường (ID, tên, giá)
  const filteredProducts = products.filter((p) => {
    const keyword = search.toLowerCase();
    return (
      p.name?.toLowerCase().includes(keyword) ||
      p.id?.toString().includes(keyword) ||
      p.price?.toString().includes(keyword)
    );
  });

  return (
    <div style={{ padding: "24px" }}>
      <h2 style={{ marginBottom: "16px", color: "#333" }}>
        Danh sách sản phẩm
      </h2>

      {/* Ô tìm kiếm có icon và hiệu ứng focus */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          backgroundColor: "#fff",
          borderRadius: "12px",
          padding: "10px 14px",
          width: "350px",
          boxShadow: "0 3px 8px rgba(0,0,0,0.08)",
          marginBottom: "24px",
          transition: "all 0.3s ease",
        }}
      >
        <FaSearch style={{ color: "#888", marginRight: "10px" }} />
        <input
          type="text"
          placeholder="🔍 Tìm kiếm sản phẩm, ID, giá..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{
            border: "none",
            outline: "none",
            background: "transparent",
            width: "100%",
            fontSize: "15px",
            color: "#333",
          }}
          onFocus={(e) => {
            e.target.parentNode.style.boxShadow =
              "0 0 8px rgba(0,123,255,0.4)";
          }}
          onBlur={(e) => {
            e.target.parentNode.style.boxShadow =
              "0 3px 8px rgba(0,0,0,0.08)";
          }}
        />
      </div>

      {/* Bảng danh sách sản phẩm */}
      <table
        style={{
          width: "100%",
          borderCollapse: "collapse",
          backgroundColor: "#fff",
          borderRadius: "12px",
          overflow: "hidden",
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
        }}
      >
        <thead style={{ background: "#007bff", color: "#fff" }}>
          <tr>
            <th style={{ padding: "10px" }}>ID</th>
            <th style={{ padding: "10px" }}>Tên sản phẩm</th>
            <th style={{ padding: "10px" }}>Giá</th>
          </tr>
        </thead>
        <tbody>
          {filteredProducts.length > 0 ? (
            filteredProducts.map((p) => (
              <tr key={p.id} style={{ borderBottom: "1px solid #eee" }}>
                <td style={{ padding: "10px", textAlign: "center" }}>{p.id}</td>
                <td style={{ padding: "10px" }}>{p.name}</td>
                <td style={{ padding: "10px" }}>
                  {p.price?.toLocaleString()} ₫
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan="3"
                style={{ textAlign: "center", padding: "12px", color: "#777" }}
              >
                Không tìm thấy sản phẩm nào
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ProductList;
